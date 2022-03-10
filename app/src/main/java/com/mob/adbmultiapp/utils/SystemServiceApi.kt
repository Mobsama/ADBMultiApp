package com.mob.adbmultiapp.utils

import android.content.Context
import android.content.pm.IPackageManager
import android.content.pm.UserInfo
import android.os.IUserManager
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper

object SystemServiceApi {

    val PackageManager: IPackageManager by lazy (LazyThreadSafetyMode.NONE) {
        IPackageManager.Stub.asInterface(ShizukuBinderWrapper(SystemServiceHelper.getSystemService("package")))
    }

    private val UserManager by lazy (LazyThreadSafetyMode.NONE) {
        IUserManager.Stub.asInterface(ShizukuBinderWrapper(SystemServiceHelper.getSystemService(Context.USER_SERVICE)))
    }

    fun getUsers(excludePartial: Boolean = true, excludeDying: Boolean = true, excludePreCreated: Boolean = true): List<UserInfo>
        = UserManager.getUsers(excludePartial, excludeDying, excludePreCreated)

}