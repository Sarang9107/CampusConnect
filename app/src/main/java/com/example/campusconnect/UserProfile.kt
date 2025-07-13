package com.example.campusconnect

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfile(
    val uid: String = "",
    val email: String? = null,
    val name: String? = null,
    val className: String? = null,
    val rollNumber: String? = null,
    val course: String? = null,
    val mobileNumber: String? = null
) : Parcelable
