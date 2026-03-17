package com.example.test

data class UserProfile(
    val name: String,
    val surname: String,
    val numberOfKids: Int,
    val notificationFrequency: NotificationFrequency = NotificationFrequency.NEVER,

) {
    companion object {
        val DEFAULT = UserProfile(
            name = "Ivo",
            surname = "Malý",
            numberOfKids = 6,
            notificationFrequency = NotificationFrequency.NEVER,
        )
    }
}
