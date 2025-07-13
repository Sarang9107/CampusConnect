package com.example.campusconnect

import com.google.firebase.firestore.PropertyName

data class EventData(
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("date") @set:PropertyName("date") var date: String = "",
    @get:PropertyName("description") @set:PropertyName("description") var description: String = "",
    @get:PropertyName("location") @set:PropertyName("location") var location: String? = null
) {
    constructor() : this("", "", "", null)
}
