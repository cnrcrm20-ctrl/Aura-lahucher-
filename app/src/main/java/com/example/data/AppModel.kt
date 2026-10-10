package com.example.data

import android.os.Process
import android.os.UserHandle
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Represents a launchable installed application.
 */
data class AppItem(
    val id: String,
    val packageName: String,
    val activityName: String,
    val label: String,
    val userHandle: UserHandle = Process.myUserHandle(),
    val isSystemApp: Boolean = false,
    val installTime: Long = 0L,
    val imageBitmap: ImageBitmap? = null
) {
    val initial: Char
        get() = label.trim().firstOrNull()?.uppercaseChar()?.let {
            if (it.isLetter()) it else '#'
        } ?: '#'
}
