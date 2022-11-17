package com.krakert.kinematics.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.ui.graphics.vector.ImageVector
import com.krakert.kinematics.R


sealed class KinematicsScreens(val route: String, @StringRes val labelResourceId: Int, val icon: ImageVector) {
    object Forward : KinematicsScreens("forward", R.string.forward, Icons.Rounded.ArrowForward)
    object Reverse : KinematicsScreens("reverse", R.string.reverse, Icons.Rounded.ArrowBack)
}