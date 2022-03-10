package com.mob.adbmultiapp.main

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.SnackbarResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.mob.adbmultiapp.base.ErrorPageState
import com.mob.adbmultiapp.base.LAUNCH_LISTENER
import com.mob.adbmultiapp.date.AppInfo
import com.mob.adbmultiapp.date.AppInfoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    state: MainContract.State,
    eventFlow: Flow<MainContract.Event>,
    onActionSent: (action: MainContract.Action) -> Unit
) {

    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec)
    }
    val (searchText, onInputChange) = remember { mutableStateOf("") }

    LaunchedEffect(LAUNCH_LISTENER) {
        onActionSent(MainContract.Action.Init(context))
        onActionSent(MainContract.Action.GetShizukuPermission)
        eventFlow.onEach { event ->
            when (event) {
                is MainContract.Event.ShowSnackBar -> {
                    when (scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration,
                        actionLabel = if (event.action != null) "GO" else null
                    )) {
                        SnackbarResult.ActionPerformed -> {
                            event.action?.invoke()
                        }
                        else -> {}
                    }
                }
            }
        }.collect()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxWidth()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        floatingActionButton = {
            if (state.appFor0User?.any { it.isSelect.value } == true) {
                ExtendedFloatingActionButton(
                    onClick = { onActionSent(MainContract.Action.InstallApp) },
                    icon = { Icon(Icons.Filled.Add, "add") },
                    text = { Text(text = "添加") },
                )
            }
        },
        topBar = {
            if (!state.get0UserApp && state.appFor0User != null && !state.get999UserApp && state.appFor999User != null) {
                SmallTopAppBar(
                    title = {
                        OutlinedTextField(
                            value = searchText,
                            placeholder = { Text("搜索", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 18.sp) },
                            onValueChange = onInputChange,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                focusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                cursorColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(30.dp),
                            modifier = Modifier.padding(start = 20.dp),
                            singleLine = true,
                            textStyle = TextStyle(fontSize = 18.sp)
                        )
                    },
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                )
            }
        }
    ) {
        if (state.getShizukuPermission || state.getUser || state.get0UserApp || state.get999UserApp) {
            LoadingPage()
        } else {
            if (state.errorState != null) {
                ErrorPage(state.errorState)
            } else {
                if (!state.get0UserApp && state.appFor0User != null && !state.get999UserApp && state.appFor999User != null) {
                    MainAppPage(
                        searchText = searchText,
                        appFor0User = state.appFor0User,
                        appFor999User = state.appFor999User
                    )
                }
            }
        }

        if (state.installing) {
            Dialog(onDismissRequest = {}) {
                Card(modifier = Modifier.size(width = 200.dp, height = 150.dp)) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(15.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }

}

@Composable
private fun LoadingPage() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(10) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 20.dp)
            ) {
                Box(
                    Modifier
                        .size(60.dp)
                        .placeholder(
                            visible = true,
                            color = Color.Gray,
                            shape = RoundedCornerShape(4.dp),
                            highlight = PlaceholderHighlight.fade(Color.LightGray)
                        )
                        .align(Alignment.CenterVertically)
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.Top)
                        .weight(1f)
                        .padding(5.dp)
                ) {
                    Box(
                        Modifier
                            .width(120.dp)
                            .height(30.dp)
                            .padding(5.dp)
                            .placeholder(
                                visible = true,
                                color = Color.Gray,
                                shape = RoundedCornerShape(4.dp),
                                highlight = PlaceholderHighlight.fade(Color.LightGray)
                            )
                    )
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(25.dp)
                            .padding(5.dp)
                            .placeholder(
                                visible = true,
                                color = Color.Gray,
                                shape = RoundedCornerShape(4.dp),
                                highlight = PlaceholderHighlight.fade(Color.LightGray)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorPage(errorState: ErrorPageState) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = errorState.errorMessage, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(3.dp))
            Button(onClick = errorState.btnAction) {
                Text(text = errorState.btnText)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainAppPage(
    searchText: String,
    appFor0User: List<AppInfoItem>,
    appFor999User: List<AppInfoItem>
) {
    val list = mutableListOf<AppInfo>()
    if (searchText.isBlank()) {
        list.add(AppInfo(true, appFor999User))
    }
    list.add(
        AppInfo(
            false,
            appFor0User
                .filter {
                    appFor999User.none { it1 -> it1.packageInfo.packageName == it.packageInfo.packageName } && ( if (searchText.isNotBlank()) {
                        it.packageInfo.packageName.contains(
                            searchText, true
                        ) || it.appName.contains(searchText, true)
                    } else true )
                }
                .sortedWith(Comparator sort@{ o1: AppInfoItem, o2: AppInfoItem ->
                    return@sort o1.appName
                        .compareTo(o2.appName, ignoreCase = true)
                })
        )
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        list.forEachIndexed { _, mainPackageInfo ->
            stickyHeader {
                ItemHeader(text = if (mainPackageInfo.installed) "已安装" else "未安装")
            }
            items(mainPackageInfo.list) {
                ItemView(it, mainPackageInfo.installed)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemHeader(text: String) {
    ElevatedCard(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 2.dp, bottom = 2.dp, end = 9.dp)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemView(item: AppInfoItem, installed: Boolean) {
    ElevatedCard(modifier = Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberDrawablePainter(
                    drawable = item.appLabel
                ), contentDescription = "icon", modifier = Modifier
                    .size(60.dp)
                    .padding(5.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.appName, color = Color.Black, maxLines = 1
                )
                Text(text = item.packageInfo.packageName, color = Color.Black)
            }
            if (!installed) {
                Checkbox(
                    checked = item.isSelect.value,
                    onCheckedChange = { item.isSelect.value = it },
                    enabled = true
                )
            }
        }
    }
}