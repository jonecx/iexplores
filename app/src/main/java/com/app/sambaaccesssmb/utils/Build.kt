package com.app.sambaaccesssmb.utils

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.IntDef

object Build {
    fun has(@SdkVersion sdkVersion: Int): Boolean {
        return VERSION.SDK_INT >= sdkVersion
    }

    @IntDef(
        *[
            VERSION_CODES.LOLLIPOP, // 5.0 - 21
            VERSION_CODES.LOLLIPOP_MR1, // 5.1 - 22
            VERSION_CODES.M, // 6.0 - 23
            VERSION_CODES.N, // 7.0 - 24
            VERSION_CODES.N_MR1, // 7.1 - 25
            VERSION_CODES.O, // 8.0 - 26
            VERSION_CODES.O_MR1, // 8.0 - 27
            VERSION_CODES.P, // 9.0 - 28
            VERSION_CODES.Q, // 10.0 - 29
            VERSION_CODES.R, // 11.0 - 30
            VERSION_CODES.S, // 12.0 - 31
            VERSION_CODES.S_V2, // 12.0L
        ]
    )
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class SdkVersion
}
