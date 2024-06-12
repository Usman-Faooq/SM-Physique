package com.buzzwaretech.gymphysique.classes

import com.buzzwaretech.gymphysique.models.UserModel

object Constants {

    lateinit var currentUser : UserModel
    const val stripePublishableKey: String = "pk_test_51N2YD0Bnjq7uedqX836TG41NpxXWBKVLiRfH6SPt24EkJx51Ej119JZ4qfywoDG9LbJrrDT0OIwfxZbLHJoLmBFo00zubL06xQ"


    fun getTimeAgo(timeInMillis: Long): String {
        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - timeInMillis

        val seconds = timeDifference / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val weeks = days / 7
        val months = days / 30
        val years = days / 365

        return when {
            years > 0 -> "$years years ago"
            months > 0 -> "$months months ago"
            weeks > 0 -> "$weeks weeks ago"
            days > 0 -> "$days days ago"
            hours > 0 -> "$hours hours ago"
            minutes > 0 -> "$minutes minutes ago"
            else -> "$seconds seconds ago"
        }
    }

}