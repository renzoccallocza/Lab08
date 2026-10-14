package com.example.lab08

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val dao: TaskDao) : ViewModel() {

    // Estado que observa la UI (viene directo de la base de datos)
    val tasks: StateFlow<List<Task>> = dao.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Eventos que llegan desde la UI
    fun addTask(description: String) {
        viewModelScope.launch { dao.insertTask(Task(description = description)) }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch { dao.updateTask(task.copy(isCompleted = !task.isCompleted)) }
    }

    fun deleteAllTasks() {
        viewModelScope.launch { dao.deleteAllTasks() }
    }
}

// Permite crear el ViewModel con un parámetro (el DAO)
class TaskViewModelFactory(private val dao: TaskDao) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = TaskViewModel(dao) as T
}