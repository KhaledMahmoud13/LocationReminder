package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var reminderDB: RemindersDatabase
    private lateinit var reminderRepository: RemindersLocalRepository

    @Before
    fun setup() {
        reminderDB = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        reminderRepository = RemindersLocalRepository(reminderDB.reminderDao())
    }

    @After
    fun Clear() = reminderDB.close()

    @Test
    fun insertDataAndRetrieve() = runBlocking {

        val reminderDTO = ReminderDTO("Title1", "Description1", "Location1", 1.0, 1.0)
        reminderRepository.saveReminder(reminderDTO)
        val result = reminderRepository.getReminder(reminderDTO.id)
        result as Result.Success
        assertThat(result.data != null, `is`(true))
        assertThat(result.data, `is`(reminderDTO))

    }

    @Test
    fun noDataFoundTest() = runBlocking {
        val reminderDTO = ReminderDTO("Title1", "Description1", "Location1", 1.0, 1.0)

        val result = reminderRepository.getReminder(reminderDTO.id)
        assertThat(result is Result.Error, `is`(true))
        result as Result.Error
        assertThat(result.message, `is`("Reminder not found!"))
    }

    @Test
    fun remindersEmptyListTest() = runBlocking {
        val reminderDTO = ReminderDTO("Title1", "Description1", "Location1", 1.0, 1.0)
        reminderRepository.saveReminder(reminderDTO)
        reminderRepository.deleteAllReminders()
        val result = reminderRepository.getReminders()

        assertThat(result is Result.Success, `is`(true))

        result as Result.Success
        assertThat(result.data, `is`(emptyList()))
    }
}