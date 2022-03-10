package com.mob.adbmultiapp.base

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

const val LAUNCH_LISTENER = "LAUNCH_LISTENER"
/**
 * 页面所有状态
 */
interface ViewState

/**
 * 一次性事件
 */
interface ViewEvent

/**
 * 页面Intent,即用户的操作
 */
interface ViewAction

abstract class BaseViewModel<UiState : ViewState, Event : ViewEvent, Action : ViewAction> :
    ViewModel() {

    private val initialState: UiState by lazy { setInitialState() }
    abstract fun setInitialState(): UiState

    private val _state: MutableState<UiState> = mutableStateOf(initialState)
    val state: State<UiState> = _state

    private val _event: Channel<Event> = Channel()
    val event = _event.receiveAsFlow()

    private val _action :MutableSharedFlow<Action> = MutableSharedFlow()

    init {
        subscribeAllAction()
    }

    private fun subscribeAllAction() {
        viewModelScope.launch {
            _action.collect {
                handleAction(it)
            }
        }
    }

    fun setAction(action: Action) {
        viewModelScope.launch { _action.emit(action) }
    }

    abstract fun handleAction(action: Action)

    protected fun setState(reducer: UiState.() -> UiState) {
        val newState = state.value.reducer()
        _state.value = newState
    }

    protected fun setEvent(builder: () -> Event) {
        val value = builder()
        viewModelScope.launch { _event.send(value) }
    }

}