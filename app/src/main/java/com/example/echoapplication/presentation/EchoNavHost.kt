package com.example.echoapplication.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun EchoNavHost(
    viewModel: EchoViewModel
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = EchoDestination.Input.route
    ) {
        composable(EchoDestination.Input.route) {
            LaunchedEffect(Unit) {
                viewModel.navigationEvents.collect { event ->
                    when (event) {
                        NavigationEvent.NavigateToResult -> {
                            navController.navigate(EchoDestination.Result.route) {
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }

            InputScreen(
                uiState = uiState,
                onTextChanged = viewModel::onTextChanged,
                onSubmitClicked = viewModel::onSubmitClicked
            )
        }

        composable(EchoDestination.Result.route) {
            ResultScreen(
                uiState = uiState,
                onReturnToInputClicked = {
                    navController.popBackStack(
                        route = EchoDestination.Input.route,
                        inclusive = false
                    )
                }
            )
        }
    }
}

@Composable
fun InputScreen(
    uiState: EchoUiState,
    onTextChanged: (String) -> Unit,
    onSubmitClicked: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Echo App",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "Submit your text and wait for server validation.",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                value = uiState.inputText,
                onValueChange = onTextChanged,
                label = { Text("Your text") },
                singleLine = true,
                enabled = !uiState.isLoading
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                onClick = onSubmitClicked,
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Submit")
                }
            }

            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = "Fake server behavior: first valid request succeeds, second fails, then repeats.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ResultScreen(
    uiState: EchoUiState,
    onReturnToInputClicked: () -> Unit
) {
    BackHandler {
        onReturnToInputClicked()
    }

    val isSuccess = uiState.outputText != null

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isSuccess) "Validation successful" else "Validation failed",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSuccess) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = if (isSuccess) "Server echoed:" else "Server error:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = uiState.outputText
                            ?: uiState.errorMessage
                            ?: "No result available.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isSuccess) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onErrorContainer
                        }
                    )
                }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                onClick = onReturnToInputClicked
            ) {
                Text("Back to input")
            }
        }
    }
}