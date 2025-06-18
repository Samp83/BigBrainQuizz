package com.supdevinci.quizz.utils

import android.util.Base64

object Base64Utils {
    fun decode(base64String: String): String {
        return String(Base64.decode(base64String, Base64.DEFAULT))
    }
}
