package com.mob.adbmultiapp.utils

import android.content.Context
import android.content.pm.ApplicationInfo

fun ApplicationInfo.getAppName(context: Context): String {
    val packageManager = context.packageManager
    return try {
        this.loadLabel(
            packageManager
        ).toString()
    } catch (t: Throwable) {
        this.packageName
    }
}