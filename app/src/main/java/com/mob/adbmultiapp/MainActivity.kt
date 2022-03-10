package com.mob.adbmultiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mob.adbmultiapp.main.MainScreen
import com.mob.adbmultiapp.main.MainViewModel
import com.mob.adbmultiapp.ui.theme.ADBMultiAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = MaterialTheme.colors.isLight
            SideEffect {
                systemUiController.setStatusBarColor(
                    color = Color.Transparent,
                    darkIcons = useDarkIcons
                )
            }
            ADBMultiAppTheme {
                Surface(color = MaterialTheme.colors.background) {
                    MainDestination()
                }
            }
        }
    }
}

@Composable
private fun MainDestination() {
    val viewModel: MainViewModel = viewModel()
    val state = viewModel.state.value
    MainScreen(
        state = state,
        eventFlow = viewModel.event,
        onActionSent = { viewModel.setAction(it) })
}




