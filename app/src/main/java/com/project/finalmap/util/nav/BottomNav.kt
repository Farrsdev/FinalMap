package com.project.finalmap.util.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavItem (val label:String, val icon: ImageVector){
    HOME("Home", Icons.Default.Home),
    MAP("Map", Icons.Default.Map),
    PROFILE("Profile", Icons.Default.Person)
}