package com.example.campusconnect

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val userProfile by profileViewModel.userProfile.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()
    val saveSuccess by profileViewModel.saveSuccess.collectAsState()
    val errorMessage by profileViewModel.errorMessage.collectAsState()

    var isInEditMode by remember { mutableStateOf(false) }

    var currentName by remember(userProfile?.name) { mutableStateOf(userProfile?.name ?: "") }
    var currentClassName by remember(userProfile?.className) { mutableStateOf(userProfile?.className ?: "") }
    var currentRollNumber by remember(userProfile?.rollNumber) { mutableStateOf(userProfile?.rollNumber ?: "") }
    var currentCourse by remember(userProfile?.course) { mutableStateOf(userProfile?.course ?: "") }
    var currentMobileNumber by remember(userProfile?.mobileNumber) { mutableStateOf(userProfile?.mobileNumber ?: "") }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserProfile()
    }

    LaunchedEffect(userProfile) {
        userProfile?.let {
            currentName = it.name ?: ""
            currentClassName = it.className ?: ""
            currentRollNumber = it.rollNumber ?: ""
            currentCourse = it.course ?: ""
            currentMobileNumber = it.mobileNumber ?: ""
        }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            Toast.makeText(context, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
            isInEditMode = false
        }
    }
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (isInEditMode) {
                            profileViewModel.saveUserProfile(
                                name = currentName,
                                className = currentClassName,
                                rollNumber = currentRollNumber,
                                course = currentCourse,
                                mobileNumber = currentMobileNumber
                            )
                        } else {
                            isInEditMode = true
                        }
                    }) {
                        Crossfade(targetState = isInEditMode, label = "EditSaveIcon") { editing ->
                            if (isLoading && editing) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Icon(
                                    imageVector = if (editing) Icons.Filled.Done else Icons.Filled.Edit,
                                    contentDescription = if (editing) "Save Profile" else "Edit Profile",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading && userProfile == null) {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator()
            } else {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "User Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Email (non-editable, from auth)
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text("Email", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 4.dp))
                    Text(userProfile?.email ?: "Loading...", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 4.dp))
                }
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                ProfileEditableItem("Name", currentName, isInEditMode) { currentName = it }
                ProfileEditableItem("Mobile Number", currentMobileNumber, isInEditMode) { currentMobileNumber = it }
                ProfileEditableItem("Class", currentClassName, isInEditMode) { currentClassName = it }
                ProfileEditableItem("Roll Number", currentRollNumber, isInEditMode) { currentRollNumber = it }
                ProfileEditableItem("Course", currentCourse, isInEditMode) { currentCourse = it }
            }
        }
    }
}

@Composable
private fun ProfileEditableItem(
    label: String,
    value: String,
    isInEditMode: Boolean,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .animateContentSize()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        AnimatedVisibility(
            visible = isInEditMode,
            enter = fadeIn() + androidx.compose.animation.slideInVertically { it / 2 },
            exit = fadeOut() + androidx.compose.animation.slideOutVertically { it / 2 }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(label) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
            )
        }

        AnimatedVisibility(
            visible = !isInEditMode,
            enter = fadeIn() + androidx.compose.animation.slideInVertically { it / 2 },
            exit = fadeOut() + androidx.compose.animation.slideOutVertically { it / 2 }
        ) {
            Text(
                text = value.ifEmpty { "Not available" },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 12.dp, bottom = 12.dp)
            )
        }
    }
    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
}
