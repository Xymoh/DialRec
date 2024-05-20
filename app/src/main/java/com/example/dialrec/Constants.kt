package com.example.dialrec

import timber.log.Timber

object Constants {
    fun asString(data: Int): String {
        val value: String = when (data) {
            0 -> "NEW"
            1 -> "DIALING"
            2 -> "RINGING"
            3 -> "HOLDING"
            4 -> "ACTIVE"
            7 -> "DISCONNECTED"
            8 -> "SELECT_PHONE_ACCOUNT"
            9 -> "CONNECTING"
            10 -> "DISCONNECTING"
            else -> {
                Timber.w("Unknown state $data")
                "UNKNOWN"
            }
        }
        return value
    }
}