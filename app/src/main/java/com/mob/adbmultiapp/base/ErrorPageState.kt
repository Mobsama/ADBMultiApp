package com.mob.adbmultiapp.base

data class ErrorPageState(
    val errorMessage: String,
    val btnText: String,
    val btnAction: () -> Unit
)
