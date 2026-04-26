package com.example.echoapplication.presentation

sealed class EchoDestination(val route: String) {
    data object Input : EchoDestination("input")
    data object Result : EchoDestination("result")
}
