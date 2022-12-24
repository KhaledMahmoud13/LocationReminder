package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//Should insert API level 30
@Config(sdk = [Build.VERSION_CODES.P])
class RemindersListViewModelTest {

    @get:Rule
    var coroutineRule = MainCoroutineRule()

    private lateinit var fakeReminders: FakeDataSource
    private lateinit var remindersViewModel: RemindersListViewModel

    @Before
    fun setUp() {
        fakeReminders = FakeDataSource()
        remindersViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeReminders
        )
    }

    @After
    fun Clear() {
        stopKoin()
    }

    @Test
    fun returningErrorTest() = runBlockingTest {
        saveFakeReminderData()
        fakeReminders.returnError = true
        remindersViewModel.loadReminders()
        MatcherAssert.assertThat(
            remindersViewModel.showSnackBar.value, CoreMatchers.`is`("Reminders are unable to get retrieved")
        )
    }

    @Test
    fun checkLoadingTest() = runBlockingTest {
        coroutineRule.pauseDispatcher()
        saveFakeReminderData()
        remindersViewModel.loadReminders()
        MatcherAssert.assertThat(remindersViewModel.showLoading.value, CoreMatchers.`is`(true))
        coroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(remindersViewModel.showLoading.value, CoreMatchers.`is`(false))
    }

    private suspend fun saveFakeReminderData() {
        fakeReminders.saveReminder(
            ReminderDTO(
                "Test title", "Test",
                "Test location", 30.0, 29.0, "1"
            )
        )
    }

}
