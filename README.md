# CampusConnect

CampusConnect is an Android application designed to help students and campus members stay informed and connected with university life. It provides easy access to event information, a helpful campus chatbot, and user account management.

## Features

*   **User Authentication**: Secure login and sign-up functionality for personalized access.
*   **Event Dashboard**: View a list of upcoming campus events, including details like descriptions, dates, and specific information for each event.
*   **Event Details**: Get comprehensive information about selected events.
*   **Campus Bot**: An interactive chatbot to answer common questions about the campus, services, and events. Users can type questions or use suggested prompts.
*   **User Profile & Settings (Planned)**: Future enhancements will include user profile management and customizable settings.
*   **Logout**: Securely log out of the application.

## Technologies Used

*   **Kotlin**: The primary programming language for development.
*   **Jetpack Compose**: Used for building the modern, declarative UI.
*   **Firebase**:
    *   Firebase Authentication for user management.
    *   (Potentially Firebase Firestore/Realtime Database for storing event data - though not explicitly confirmed in our interactions, it's a common pattern with Firebase Auth).
*   **Gemini API**: Leveraged for the intelligent features of the Campus Bot, enabling natural language understanding and responses.
*   **Android ViewModel**: For managing UI-related data in a lifecycle-conscious way.
*   **Android Navigation Component**: For handling in-app navigation between different screens.

## Setup and Build

1.  **Clone the repository:**
    ```bash
    git clone <your-repository-url>
    cd CampusConnect
    ```
2.  **Firebase Setup:**
    *   This project uses Firebase. You will need to set up your own Firebase project at [https://console.firebase.google.com/](https://console.firebase.google.com/).
    *   Add an Android app to your Firebase project with the package name `com.example.campusconnect` (or your actual package name if different).
    *   Download the `google-services.json` file from your Firebase project settings and place it in the `app/` directory of this project.
3.  **Open in Android Studio:**
    *   Open Android Studio and select "Open an existing Android Studio project".
    *   Navigate to the cloned CampusConnect directory and open it.
4.  **Build the project:**
    *   Allow Android Studio to sync Gradle files.
    *   Click on `Build > Make Project` or run the app on an emulator or physical device.

## Project Structure (Key Components)

*   `MainActivity.kt`: Main entry point for the application.
*   `NavGraph.kt`: Defines navigation routes and composables for different screens.
*   `LoginScreen.kt`, `SignUpScreen.kt`: Handles user authentication.
*   `DashboardScreen.kt`: Displays upcoming events and provides access to other features.
*   `EventDetailScreen.kt`: Shows detailed information about a specific event.
*   `CampusBotScreen.kt`: Provides the UI for interacting with the campus chatbot.
*   `CampusBotViewModel.kt`: Handles the logic and data for the Campus Bot.
*   `GeminiApi.kt`: Contains the setup and functions for interacting with the Gemini API.
*   `data.kt` (or similar for Event/ChatMessage): Data model classes.

---

This README provides a general overview. You can expand on specific features, add troubleshooting tips, or include contribution guidelines as needed.
