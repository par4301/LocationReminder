package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.rule.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.succeeded
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    private lateinit var fakeReminderDataSource: FakeDataSource
    private lateinit var remindersViewModel: SaveReminderViewModel

    @get:Rule
    var instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupTestViewModel() {
        fakeReminderDataSource = FakeDataSource()
        remindersViewModel = SaveReminderViewModel( ApplicationProvider.getApplicationContext(), fakeReminderDataSource )
    }

    @After
    fun cancelTestViewModel() {
        stopKoin()
    }

    @Test
    fun getShowLoading_checkValueAfterSaveReminder_returnsTrueFalse() {
        //Given
        mainCoroutineRule.pauseDispatcher()
        val reminderDataItem = ReminderDataItem(
            "title",
            "description",
            "location",
            1.00,
            2.00
        )

        // When
        remindersViewModel.saveReminder(reminderDataItem)

        // Then
        assertThat(remindersViewModel.showLoading.value, `is`(true))
        mainCoroutineRule.resumeDispatcher()

        // Then
        assertThat(remindersViewModel.showLoading.value, `is`(false))
        assertThat(remindersViewModel.showToast.value, `is`("Reminder Saved !"))
    }

    @Test
    fun getDataDetails_checkTitleDescLocLatLang_assertsOutputs() = runBlockingTest {
        //Given
        val reminder = ReminderDataItem(
                "title",
                "description",
                "location",
                1.00,
                2.00
        )

        // When
        remindersViewModel.saveReminder(reminder)

        val result = fakeReminderDataSource.getReminder(reminder.id)

        // Then
        assertThat(result.succeeded, `is`(true))
        result as Result.Success

        // Then
        assertThat(result.data.title, `is`(reminder.title))
        assertThat(result.data.description, `is`(reminder.description))
        assertThat(result.data.location, `is`(reminder.location))
        assertThat(result.data.latitude, `is`(reminder.latitude))
        assertThat(result.data.longitude, `is`(reminder.longitude))

    }

    @Test
    fun getResult_checkEmptyTitle_returnsFalse() {
        //Given
        val reminder = ReminderDataItem(
            "",
            "description",
            "location",
            1.00,
            2.00
        )

        // When
        val result = remindersViewModel.validateEnteredData(reminder)

        // Then
        assertThat(result, `is`(false))
        assertThat(remindersViewModel.showSnackBarInt.value, `is`(R.string.err_enter_title))
    }

    @Test
    fun getResult_checkNullTitle_returnsFalse() {
        //Given
        val reminder = ReminderDataItem(
                null,
                "description",
                "location",
                40.00,
                50.00
        )

        // When
        val result = remindersViewModel.validateEnteredData(reminder)

        // Then
        assertThat(result, `is`(false))
        assertThat(remindersViewModel.showSnackBarInt.value, `is`(R.string.err_enter_title))
    }

    @Test
    fun getResult_checkEmptyLocation_returnsFalse() {
        //Given
        val reminder = ReminderDataItem(
                "title",
                "description",
                "",
                40.00,
                50.00
        )

        // When
        val result = remindersViewModel.validateEnteredData(reminder)

        // Then
        assertThat(result, `is`(false))
        assertThat(remindersViewModel.showSnackBarInt.value, `is`(R.string.err_select_location))
    }

    @Test
    fun getResult_checkNullLocation_returnsFalse() {
        //Given
        val reminder = ReminderDataItem(
                "title",
                "description",
                null,
                40.00,
                50.00
        )

        // When
        val result = remindersViewModel.validateEnteredData(reminder)

        // Then
        assertThat(result, `is`(false))
        assertThat(remindersViewModel.showSnackBarInt.value, `is`(R.string.err_select_location))
    }

    @Test
    fun getResult_checkValidatedEnteredData_returnsTrue() {
        //Given
        val reminder = ReminderDataItem(
                "title",
                "description",
                "location",
                40.00,
                50.00
        )

        // When
        val result = remindersViewModel.validateEnteredData(reminder)

        // Then
        assertThat(result, `is`(true))
    }

}