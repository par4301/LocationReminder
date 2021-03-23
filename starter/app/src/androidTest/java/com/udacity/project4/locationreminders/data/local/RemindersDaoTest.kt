package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var reminderListDatabase: RemindersDatabase

    @Before
    fun initDb() {
        reminderListDatabase = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = reminderListDatabase.close()

    @Test
    fun getReminder_checkSavedReminderDetailsInDatabase_assertsOutput() = runBlockingTest {
        // Given
        val reminder = ReminderDTO(
                "title",
                "description",
                "location",
                1.00,
                2.00
        )

        // When
        reminderListDatabase.reminderDao().saveReminder(reminder)

        val loaded = reminderListDatabase.reminderDao().getReminderById(reminder.id)

        // Then
        assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.title, `is`(reminder.title))
        assertThat(loaded.description, `is`(reminder.description))
        assertThat(loaded.latitude, `is`(reminder.latitude))
        assertThat(loaded.longitude, `is`(reminder.longitude))
    }

    @Test
    fun getLoadedReminder_checkDeleteAllReminders_returnsIsEmptyTrue() = runBlockingTest {
        // Given
        val reminder = ReminderDTO(
                "title",
                "description",
                "location",
                1.00,
                2.00
        )
        // When
        reminderListDatabase.reminderDao().saveReminder(reminder)
        reminderListDatabase.reminderDao().deleteAllReminders()

        val loadedReminders = reminderListDatabase.reminderDao().getReminders()

        // Then
        assertThat(loadedReminders.isEmpty(), `is`(true))
    }

    @Test
    fun getLoadedReminder_checkSaveTwoReminders_returnsSizeTwo() = runBlockingTest {
        //Given there are two reminders
        val testFirstReminder = ReminderDTO(
                "title1",
                "description1",
                "location1",
                40.00,
                50.00
        )
        val testSecondReminder = ReminderDTO(
                "title2",
                "description2",
                "location2",
                40.00,
                50.00
        )
        //When save both reminders one after another
        reminderListDatabase.reminderDao().saveReminder(testFirstReminder)
        reminderListDatabase.reminderDao().saveReminder(testSecondReminder)

        val loadedReminders = reminderListDatabase.reminderDao().getReminders()

        //Then check the size is two
        assertThat(loadedReminders.size, `is`(2))
    }
}