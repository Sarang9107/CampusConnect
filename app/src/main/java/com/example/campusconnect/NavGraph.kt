package com.example.campusconnect


import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseUser
import java.net.URLDecoder
import java.net.URLEncoder

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
    viewModel: CampusBotViewModel,
    loginUser: (String, String, (String) -> Unit, (String) -> Unit) -> Unit,
    signUpUser: (String, String, () -> Unit, (String) -> Unit) -> Unit,
    fetchEvents: ((List<Event>) -> Unit) -> Unit,
    getCurrentUser: () -> FirebaseUser?,
    logoutUser: (onLoggedOut: () -> Unit) -> Unit
) {

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                onLoginClicked = { email, password ->
                    loginUser(
                        email,
                        password,
                        { _ ->
                            navController.navigate("dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        { errorMessage ->
                            Toast.makeText(
                                navController.context,
                                "Login failed: $errorMessage",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                },
                onSignUpClicked = {
                    navController.navigate("signup")
                }
            )
        }

        composable("signup") {
            SignUpScreen(
                onSignUpClicked = { email, password ->
                    signUpUser(
                        email,
                        password,
                        {
                            Toast.makeText(
                                navController.context,
                                "Sign up successful! Please log in.",
                                Toast.LENGTH_LONG
                            ).show()
                            navController.navigate("login") {
                                popUpTo("signup") { inclusive = true }
                            }
                        },
                        { errorMessage ->
                            Toast.makeText(
                                navController.context,
                                "Sign up failed: $errorMessage",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(
                fetchEvents = fetchEvents,
                onGoToChat = {
                    navController.navigate("chat")
                },
                onEventClick = { event ->
                    val encodedTitle = URLEncoder.encode(event.title, "UTF-8")
                    val encodedDesc = URLEncoder.encode(event.description, "UTF-8")
                    val encodedDate = URLEncoder.encode(event.date, "UTF-8")
                    val encodedDetails = URLEncoder.encode(event.details, "UTF-8")
                    navController.navigate("eventDetail/$encodedTitle/$encodedDesc/$encodedDate/$encodedDetails")
                },
                onLogoutClicked = {
                    logoutUser {
                        navController.navigate("login") {
                            popUpTo("dashboard") { inclusive = true }
                            launchSingleTop = true
                        }
                        Toast.makeText(navController.context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        composable("chat") {
            CampusBotScreen(
                messages = viewModel.messages,
                userInput = viewModel.userInput,
                onUserInputChange = { viewModel.onUserInputChange(it) },
                onSendClick = { viewModel.sendMessage() },
                isLoading = viewModel.isLoading,
                errorMessage = viewModel.errorMessage,
                suggestedQuestions = viewModel.suggestedQuestions,
                onSuggestedQuestionClick = viewModel::onSuggestedQuestionClick
            )
        }

        composable(
            route = "eventDetail/{title}/{description}/{date}/{details}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("details") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title")?.let { URLDecoder.decode(it, "UTF-8") }
            val description = backStackEntry.arguments?.getString("description")?.let { URLDecoder.decode(it, "UTF-8") }
            val date = backStackEntry.arguments?.getString("date")?.let { URLDecoder.decode(it, "UTF-8") }
            val details = backStackEntry.arguments?.getString("details")?.let { URLDecoder.decode(it, "UTF-8") }

            if (title != null && description != null && date != null && details != null) {
                EventDetailScreen(
                    event = Event(title, description, date, details),
                    onNavigateBack = { navController.popBackStack() }
                )
            } else {
                Toast.makeText(navController.context, "Error loading event details.", Toast.LENGTH_SHORT).show()
                navController.popBackStack() // Navigate back if details are missing
            }
        }
    }
}
