package com.mob.adbmultiapp.main

import android.content.Context
import androidx.compose.material.SnackbarDuration
import com.mob.adbmultiapp.base.ErrorPageState
import com.mob.adbmultiapp.base.ViewAction
import com.mob.adbmultiapp.base.ViewEvent
import com.mob.adbmultiapp.base.ViewState
import com.mob.adbmultiapp.date.AppInfoItem

class MainContract {

    data class State(
        val errorState: ErrorPageState? = null,
        val context: Context? = null,
        val getUser: Boolean = false,
        val get0UserApp: Boolean = false,
        val get999UserApp: Boolean = false,
        val getShizukuPermission: Boolean = false,
        val installing: Boolean = false,
        val appFor0User: List<AppInfoItem>? = null,
        val appFor999User: List<AppInfoItem>? = null
    ): ViewState

    sealed class Event: ViewEvent {
        data class ShowSnackBar(val message: String, val duration: SnackbarDuration, val action: (() -> Unit)?): Event()
    }

    sealed class Action: ViewAction {
        data class Init(val context: Context): Action()
        object GetShizukuPermission: Action()
        object GetUsers: Action()
        object GetAllUserApps: Action()
        object InstallApp: Action()
        data class ShowSnackBar(val message: String): Action()
    }

}