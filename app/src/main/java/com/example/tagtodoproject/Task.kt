package com.example.tagtodoproject

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class TaskListData(
    var name: String = "",
    var date: String = "",  // Properly declared in primary constructor
    var tags: String = "",
    var category: String = "",
    var isCompleted: Boolean = false
) {
    @Exclude
    var documentId: String = ""

    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "date" to date,  // Now correctly references the class property
            "tags" to tags,
            "category" to category,
            "isCompleted" to isCompleted
        )
    }
}