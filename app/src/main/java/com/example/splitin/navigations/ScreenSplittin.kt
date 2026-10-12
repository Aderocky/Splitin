package com.example.splitin.navigations

sealed class ScreenSplitin (val route: String) {
    object Home : ScreenSplitin("home")
    object AddFriends : ScreenSplitin("addFriends")
    object AddItems : ScreenSplitin("addItems")
    object SplitItems : ScreenSplitin("splitItems")
    object Review : ScreenSplitin("review")
    object Payment : ScreenSplitin("payment")
    object Result : ScreenSplitin("result")
}
