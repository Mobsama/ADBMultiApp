package com.mob.adbmultiapp.date

import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class AppInfo (
    val installed: Boolean = false,
    val list: List<AppInfoItem>
)

data class AppInfoItem(
    val packageInfo: ApplicationInfo,
    val isSelect: MutableState<Boolean> = mutableStateOf(false),
    val appName: String,
    val appLabel: Drawable
)