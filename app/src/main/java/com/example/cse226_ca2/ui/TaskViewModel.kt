package com.example.cse226_ca2.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cse226_ca2.data.AppDatabase
import com.example.cse226_ca2.data.TaskDao
import com.example.cse226_ca2.data.TaskEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(
    application: Application,
    private val taskDao: TaskDao = AppDatabase.getDatabase(application).taskDao()
) : AndroidViewModel(application) {

    val tasks: StateFlow<List<TaskEntity>> = taskDao.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    /**
     * Launches a coroutine in viewModelScope to insert a new TaskEntity into the Room database via TaskDao.
     */
    fun insertTask(title: String, description: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val task = TaskEntity(
                title = title.trim(),
                description = description.trim()
            )
            taskDao.insertTask(task)
            onSuccess()
        }
    }
}
