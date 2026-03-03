package com.example.test

enum class NotificationFrequency {

    NEVER, DAILY, WEEKLY, MONTHLY;

    // Human-readable label for the UI
    fun displayName(): String = when (this) {
        NEVER   -> "Never"
        DAILY   -> "Daily"
        WEEKLY  -> "Weekly"
        MONTHLY -> "Monthly"
    }
}
