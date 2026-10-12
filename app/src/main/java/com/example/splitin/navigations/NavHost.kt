package com.example.splitin.navigations

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.splitin.screens.addFriends.AddFriendsScreen
import com.example.splitin.screens.addItems.AddItemsScreen
import com.example.splitin.screens.home.HomeScreens
import com.example.splitin.screens.payment.PaymentScreen
import com.example.splitin.screens.result.ResultScreen
import com.example.splitin.screens.splitItems.SplitItemsScreen

@Composable
fun NavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ScreenSplitin.Home.route,
    ) {
        composable(route = ScreenSplitin.Home.route) {
            HomeScreens(
                navController = navController,
                onNext = { navController.navigate(ScreenSplitin.AddFriends.route) }
            )
        }
        composable(route = ScreenSplitin.AddFriends.route) {
            AddFriendsScreen(
                navController = navController,
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(ScreenSplitin.AddItems.route) }
            )
        }
        composable(route = ScreenSplitin.AddItems.route) {
            AddItemsScreen(
                navController = navController,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(ScreenSplitin.SplitItems.route) }
            )
        }
        composable(route = ScreenSplitin.SplitItems.route) {
            SplitItemsScreen(
                navController = navController,
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(ScreenSplitin.Payment.route) }
            )
        }
        composable(route = ScreenSplitin.Payment.route) {
            PaymentScreen(
                navController = navController,
                onBack = { navController.popBackStack() },
                onCalculateSplit = { navController.navigate(ScreenSplitin.Result.route) }
            )
        }
        composable(route = ScreenSplitin.Result.route) {
            ResultScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
