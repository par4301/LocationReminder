package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.rule.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
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

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var fakeReminderDataSource: FakeDataSource
    private lateinit var remindersViewModel: RemindersListViewModel

    @get:Rule
    var instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupTestViewModel() {
        fakeReminderDataSource = FakeDataSource()
        remindersViewModel = RemindersListViewModel( ApplicationProvider.getApplicationContext(), fakeReminderDataSource)
    }

    @After
    fun cancelTestViewModel() {
        stopKoin()
    }

    @Test
    fun getRemindersViewModel_checkShowLoadingValue_returnsTrueFalse() {
        // When
        mainCoroutineRule.pauseDispatcher()
        remindersViewModel.loadReminders()

        // Then
        assertThat(remindersViewModel.showLoading.value, `is`(true))

        // When
        mainCoroutineRule.resumeDispatcher()

        // Then
        assertThat(remindersViewModel.showLoading.value, `is`(false))
    }

    @Test
    fun getFakeDataSourceInRemindersViewModel_checkShowNoDataValue_returnsFalse() = runBlockingTest {
        // Given
        fakeReminderDataSource.saveReminder(ReminderDTO(
                "title",
                "description",
                "location",
                40.00,
                50.00
        ))

        // When
        remindersViewModel.loadReminders()

        // Then
        assertThat(remindersViewModel.showNoData.value, `is`(false))
    }

    @Test
    fun getEmptyRemindersViewModel_checkShowNoDataValue_returnsTrue() {
        remindersViewModel.loadReminders()
        assertThat(remindersViewModel.showNoData.value, `is`(true))
    }
}