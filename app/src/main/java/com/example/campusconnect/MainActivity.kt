package com.example.campusconnect

import android.os.Bundle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val viewModel: CampusBotViewModel = viewModel()

                // Determine start destination
                val startDestination = if (auth.currentUser != null) {
                    "dashboard"
                } else {
                    "login"
                }

                AppNavGraph(
                    navController = navController,
                    startDestination = startDestination,
                    viewModel = viewModel,
                    loginUser = { email, password, onSuccess, onFailure ->
                        loginUser(email, password, onSuccess, onFailure)
                    },
                    signUpUser = { email, password, onSuccess, onFailure ->
                        signUpUser(email, password, onSuccess, onFailure)
                    },
                    fetchEvents = { onResult ->
                        fetchEvents(onResult)
                    },
                    getCurrentUser = { auth.currentUser },
                    logoutUser = { onLoggedOut ->
                        logoutUser(onLoggedOut)
                    }
                )
            }
        }
    }

    fun loginUser(email: String, password: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    onSuccess(userId)
                } else {
                    onFailure(task.exception?.message ?: "Unknown login error")
                }
            }
    }

    fun signUpUser(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception?.message ?: "Unknown sign-up error")
                }
            }
    }

    // New logout function
    fun logoutUser(onLoggedOut: () -> Unit) {
        auth.signOut()
        onLoggedOut()
    }
}

fun fetchEvents(onResult: (List<Event>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("events")
        .get()
        .addOnSuccessListener { result ->
            val events = result.map { doc ->
                Event(
                    title = doc.getString("title") ?: "",
                    description = doc.getString("description") ?: "",
                    date = doc.getString("date") ?: "",
                    details = doc.getString("Details") ?: ""
                )
            }
            onResult(events)
        }
        .addOnFailureListener { e ->
            onResult(emptyList())
        }
}
