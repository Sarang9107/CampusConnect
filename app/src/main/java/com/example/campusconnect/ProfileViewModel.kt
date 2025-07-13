package com.example.campusconnect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun fetchUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val currentUser = auth.currentUser
            if (currentUser != null) {
                try {
                    val docRef = db.collection("users").document(currentUser.uid)
                    val documentSnapshot = docRef.get().await()
                    if (documentSnapshot.exists()) {
                        var profile = documentSnapshot.toObject<UserProfile>()
                        if (profile?.email != currentUser.email) {
                            profile = (profile ?: UserProfile(uid = currentUser.uid)).copy(email = currentUser.email)
                        }
                        _userProfile.value = profile
                    } else {
                        _userProfile.value = UserProfile(uid = currentUser.uid, email = currentUser.email)
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Error fetching profile: ${e.message}"
                }
            } else {
                _errorMessage.value = "User not logged in."
            }
            _isLoading.value = false
        }
    }

    fun saveUserProfile(
        name: String,
        className: String,
        rollNumber: String,
        course: String,
        mobileNumber: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _saveSuccess.value = false
            _errorMessage.value = null
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val updatedProfile = UserProfile(
                    uid = currentUser.uid,
                    email = currentUser.email, // Email is generally not editable by the user directly here
                    name = name.takeIf { it.isNotBlank() },
                    className = className.takeIf { it.isNotBlank() },
                    rollNumber = rollNumber.takeIf { it.isNotBlank() },
                    course = course.takeIf { it.isNotBlank() },
                    mobileNumber = mobileNumber.takeIf { it.isNotBlank() }
                )
                try {
                    db.collection("users").document(currentUser.uid)
                        .set(updatedProfile)
                        .await()
                    _userProfile.value = updatedProfile // Update local state
                    _saveSuccess.value = true
                } catch (e: Exception) {
                    _errorMessage.value = "Error saving profile: ${e.message}"
                }
            } else {
                _errorMessage.value = "User not logged in. Cannot save profile."
            }
            _isLoading.value = false
        }
    }
}