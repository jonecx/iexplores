package com.app.sambaaccesssmb.utils

import android.net.InetAddresses.isNumericAddress
import android.os.Build.VERSION_CODES.Q
import android.util.Patterns

fun String?.isValidAddress(): Boolean {
    return this?.let {
        this.isValidText() && if (Build.has(Q)) isNumericAddress(this) else Patterns.IP_ADDRESS.matcher(this).matches()
    } ?: false
}

fun String?.isValidUsername() = this.isValidText()

fun String?.isValidPassword() = this.isValidText()

private fun String?.isValidText() = this != null && this.isNotEmpty()
