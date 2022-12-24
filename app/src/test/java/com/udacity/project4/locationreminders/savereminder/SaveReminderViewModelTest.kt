package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

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

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SaveReminderViewModelTest {

    @get:Rule
    var coroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeReminders: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @Before
    fun setUp() {
        fakeReminders = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeReminders
        )
    }

    @After
    fun Clear() {
        stopKoin()
    }

    private fun createFakeDataItem(): ReminderDataItem {
        val item = ReminderDataItem(
            "Test title", "Test",
            "Test location", 30.0, 29.0
        )
        return item
    }

    private fun createIncorrectDataItem(): ReminderDataItem {
        val item = ReminderDataItem(
            "Test title", "Test",
            "", 30.0, 29.0
        )
        return item
    }

    @Test
    fun incorrectDataItemTest() = runBlockingTest {

        val boolResult = saveReminderViewModel.validateEnteredData(createIncorrectDataItem())
        MatcherAssert.assertThat(
            boolResult, CoreMatchers.`is`(false)
        )
    }

    @Test
    fun loadingDataItemTest() = runBlockingTest {

        coroutineRule.pauseDispatcher()

        saveReminderViewModel.saveReminder(createFakeDataItem())
        MatcherAssert.assertThat(saveReminderViewModel.showLoading.value, CoreMatchers.`is`(true))
        coroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(saveReminderViewModel.showLoading.value, CoreMatchers.`is`(false))

    }


}