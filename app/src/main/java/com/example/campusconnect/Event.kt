package com.example.campusconnect

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    val title: String,
    val description: String,
    val date: String,
    val details: String
) : Parcelable
