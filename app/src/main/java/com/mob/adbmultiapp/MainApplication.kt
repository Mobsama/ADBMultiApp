package com.mob.adbmultiapp

import android.app.Application
import android.content.Context
import org.lsposed.hiddenapibypass.HiddenApiBypass

class MainApplication: Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        HiddenApiBypass.addHiddenApiExemptions("L")
    }

}