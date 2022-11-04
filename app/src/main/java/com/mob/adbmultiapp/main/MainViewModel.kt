package com.mob.adbmultiapp.main

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.compose.material.SnackbarDuration
import androidx.lifecycle.viewModelScope
import com.mob.adbmultiapp.BuildConfig
import com.mob.adbmultiapp.base.BaseViewModel
import com.mob.adbmultiapp.base.ErrorPageState
import com.mob.adbmultiapp.date.AppInfoItem
import com.mob.adbmultiapp.utils.SystemServiceApi
import com.mob.adbmultiapp.utils.getAppName
import kotlinx.coroutines.launch
import rikka.shizuku.Shizuku

class MainViewModel : BaseViewModel<MainContract.State, MainContract.Event, MainContract.Action>() {

    private val mListener: Shizuku.OnRequestPermissionResultListener =
        Shizuku.OnRequestPermissionResultListener { _, grantResult ->
            if (grantResult == PERMISSION_GRANTED) {
                setState { copy(errorState = null, getUser = true, getShizukuPermission = false) }
                setAction(MainContract.Action.GetUsers)
            } else {
                setState {
                    copy(
                        getShizukuPermission = false,
                        errorState = ErrorPageState("已被拒绝使用Shizuku", "重试") {
                            setState { copy(errorState = null, getShizukuPermission = true) }
                            setAction(MainContract.Action.GetShizukuPermission)
                        })
                }
            }
        }

    init {
        Shizuku.addRequestPermissionResultListener(mListener)
    }

    override fun setInitialState(): MainContract.State =
        MainContract.State(getShizukuPermission = true)

    override fun handleAction(action: MainContract.Action) {
        when (action) {
            is MainContract.Action.Init -> setState { copy(context = action.context) }
            is MainContract.Action.GetShizukuPermission -> getShizukuPermission()
            is MainContract.Action.GetUsers -> getUsers()
            is MainContract.Action.GetAllUserApps -> {
                getAppsByUserId(0)
                getAppsByUserId(999)
            }
            is MainContract.Action.ShowSnackBar -> setEvent {
                MainContract.Event.ShowSnackBar(
                    message = action.message,
                    SnackbarDuration.Short,
                    null
                )
            }
            is MainContract.Action.InstallApp -> installApp()
        }
    }

    private fun getShizukuPermission() {
        viewModelScope.launch {
            if (Shizuku.isPreV11()) {
                setState {
                    copy(
                        getShizukuPermission = false,
                        errorState = ErrorPageState("请升级Shizuku至v12版本", "重试") {
                            setState {
                                copy(
                                    errorState = null,
                                    getShizukuPermission = true
                                )
                            }
                            setAction(MainContract.Action.GetShizukuPermission)
                        }
                    )
                }
                setEvent {
                    MainContract.Event.ShowSnackBar(
                        message = "请升级Shizuku至v12版本",
                        SnackbarDuration.Short,
                        null
                    )
                }
            } else {
                try {
                    when {
                        Shizuku.checkSelfPermission() == PERMISSION_GRANTED -> {
                            setState { copy(getShizukuPermission = false, getUser = true) }
                            setAction(MainContract.Action.GetUsers)
                        }
                        Shizuku.shouldShowRequestPermissionRationale() -> {
                            setState {
                                copy(
                                    getShizukuPermission = false,
                                    errorState = ErrorPageState("已被拒绝使用Shizuku", "重试") {
                                        setState {
                                            copy(
                                                errorState = null,
                                                getShizukuPermission = true
                                            )
                                        }
                                        setAction(MainContract.Action.GetShizukuPermission)
                                    })
                            }
                        }
                        else -> {
                            Shizuku.requestPermission(1)
                            setEvent {
                                MainContract.Event.ShowSnackBar(
                                    message = "请同意权限申请",
                                    SnackbarDuration.Short,
                                    null
                                )
                            }
                        }
                    }
                } catch (t: Throwable) {
                    setState {
                        copy(
                            getShizukuPermission = false,
                            errorState = ErrorPageState(
                                "${t.message.toString()} \n 请确保已启动Shizuku",
                                "重试"
                            ) {
                                setState { copy(errorState = null, getShizukuPermission = true) }
                                setAction(MainContract.Action.GetShizukuPermission)
                            })
                    }
                }
            }
        }
    }

    private fun getAppsByUserId(userId: Int) {
        viewModelScope.launch {
            state.value.context?.let { context ->
                val apps = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        SystemServiceApi.PackageManager.getInstalledApplications(0L, userId)
                    } else {
                        SystemServiceApi.PackageManager.getInstalledApplications(0, userId)
                    }.list.asSequence()
                    .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 && it.packageName != BuildConfig.APPLICATION_ID }
                    .map { AppInfoItem(it, appName = it.getAppName(context), appLabel = it.loadIcon(context.packageManager)) }.toList()
                if (userId == 0) {
                    setState { copy(get0UserApp = false, appFor0User = apps) }
                } else if (userId == 999) {
                    setState { copy(get999UserApp = false, appFor999User = apps) }
                }
            }
        }
    }

    private fun getUsers() {
        viewModelScope.launch {
            try {
                val users = SystemServiceApi.getUsers()
                if (users.any { it.id == 999 }) {
                    setState { copy(getUser = false, get0UserApp = true, get999UserApp = true) }
                    setAction(MainContract.Action.GetAllUserApps)
                } else {
                    setState {
                        copy(
                            getUser = false,
                            errorState = ErrorPageState(
                                "没有ID为999的用户空间，请去设置中双开任意应用",
                                "重试"
                            ) {
                                setState { copy(errorState = null, getShizukuPermission = true) }
                                setAction(MainContract.Action.GetShizukuPermission)
                            })
                    }
                }
            } catch (t: Throwable) {
                setState {
                    copy(
                        getUser = false,
                        errorState = ErrorPageState(
                            "${t.message.toString()} \n 请确保已启动Shizuku",
                            "重试"
                        ) {
                            setState { copy(errorState = null, getShizukuPermission = true) }
                            setAction(MainContract.Action.GetShizukuPermission)
                        })
                }
            }
        }
    }

    private fun installApp() {
        viewModelScope.launch {
            setState { copy(installing = true) }
            state.value.appFor0User?.filter { it.isSelect.value }?.forEach {
                SystemServiceApi.PackageManager.installExistingPackageAsUser(it.packageInfo.packageName, 999,
                    0x00000004 /*PackageManager.INSTALL_ALLOW_TEST*/or 0x00000002, 4, null)
            }
            setState { copy(installing = false, get999UserApp = true, get0UserApp = true) }
            setAction(MainContract.Action.GetAllUserApps)
        }
    }
}