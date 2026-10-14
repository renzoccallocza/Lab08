package com.example.lab08

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.lab08.ui.theme.Lab08Theme

class MainActivity : ComponentActivity() {

    // Una sola instancia de la base de datos
    private val db by lazy {
        Room.databaseBuilder(applicationContext, TaskDatabase::class.java, "task_db").build()
    }

    // El ViewModel sobrevive a rotaciones
    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(db.taskDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab08Theme {
                TaskScreen(viewModel)
            }
        }
    }
}

@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    var newTaskDescription by remember { mutableStateOf("") }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            TextField(
                value = newTaskDescription,
                onValueChange = { newTaskDescription = it },
                label = { Text("Nueva tarea") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (newTaskDescription.isNotBlank()) {
                        viewModel.addTask(newTaskDescription.trim())
                        newTaskDescription = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) { Text("Agregar tarea") }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(tasks, key = { it.id }) { task ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = { viewModel.toggleTaskCompletion(task) }
                        )
                        Text(text = task.description, modifier = Modifier.weight(1f))
                    }
                }
            }

            Button(
                onClick = { viewModel.deleteAllTasks() },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) { Text("Eliminar todas las tareas") }
        }
    }
}