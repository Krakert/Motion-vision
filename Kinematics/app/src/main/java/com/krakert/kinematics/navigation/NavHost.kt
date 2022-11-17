package com.krakert.kinematics.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.krakert.kinematics.ui.screens.Forward
import com.krakert.kinematics.ui.screens.Reverse
import com.krakert.kinematics.ui.viewmodel.ForwardViewModel
import com.krakert.kinematics.ui.viewmodel.ReverseViewModel


@Composable
fun KinematicsNavHost(navController: NavHostController, innerPadding: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = KinematicsScreens.Reverse.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(KinematicsScreens.Forward.route) {
            Forward(viewModel = ForwardViewModel())
        }
        composable(KinematicsScreens.Reverse.route) {
            Reverse(viewModel = ReverseViewModel())
        }
    }
}


