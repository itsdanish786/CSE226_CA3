package com.example.cse226_ca2.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cse226_ca2.data.TaskEntity
import com.example.cse226_ca2.ui.theme.CSE226_CA2Theme
import kotlinx.coroutines.launch

/**
 * Stateful Route:
 * Responsible for hoisting screen state, interacting with [TaskViewModel],
 * and delegating state and event callbacks to the stateless [TaskInputScreen].
 */
@Composable
fun TaskEntryRoute(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = viewModel()
) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var isTitleError by rememberSaveable { mutableStateOf(false) }

    val tasks by viewModel.tasks.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    TaskInputScreen(
        title = title,
        onTitleChange = {
            title = it
            if (isTitleError && it.isNotBlank()) {
                isTitleError = false
            }
        },
        description = description,
        onDescriptionChange = { description = it },
        isTitleError = isTitleError,
        onSaveClick = {
            if (title.isBlank()) {
                isTitleError = true
            } else {
                viewModel.insertTask(
                    title = title,
                    description = description,
                    onSuccess = {
                        title = ""
                        description = ""
                        isTitleError = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Task saved successfully!")
                        }
                    }
                )
            }
        },
        tasks = tasks,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

/**
 * Stateless Input Screen:
 * Adheres to strict state hoisting principles by receiving immutable UI state
 * and delegating user actions through event callbacks. It contains no ViewModel or Room dependencies.
 */
@Composable
fun TaskInputScreen(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    isTitleError: Boolean,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    tasks: List<TaskEntity> = emptyList(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Task Entry Form",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Task Title input field
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Task Title") },
            placeholder = { Text("Enter task title") },
            singleLine = true,
            isError = isTitleError,
            supportingText = {
                if (isTitleError) {
                    Text(
                        text = "Title cannot be empty",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Task Description input field
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Task Description") },
            placeholder = { Text("Enter task description") },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save Task Button
        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Save Task")
        }

        Spacer(modifier = Modifier.height(8.dp))

        SnackbarHost(hostState = snackbarHostState)

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(12.dp))

        // Saved Tasks Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Saved Tasks (${tasks.size})",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (tasks.isEmpty()) {
            Text(
                text = "No tasks saved yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskItemCard(task = task)
                }
            }
        }
    }
}

@Composable
fun TaskItemCard(
    task: TaskEntity,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskInputScreenPreview() {
    CSE226_CA2Theme {
        TaskInputScreen(
            title = "Sample Title",
            onTitleChange = {},
            description = "Sample Description for the task entry.",
            onDescriptionChange = {},
            isTitleError = false,
            onSaveClick = {},
            tasks = listOf(
                TaskEntity(id = 1, title = "Task 1", description = "First task description"),
                TaskEntity(id = 2, title = "Task 2", description = "Second task description")
            )
        )
    }
}
