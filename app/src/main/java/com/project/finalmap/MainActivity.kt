package com.project.finalmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.project.finalmap.ui.screen.MapScreen
import com.project.finalmap.ui.theme.FinalMapTheme
import com.project.finalmap.util.nav.BottomNavItem
import org.maplibre.android.MapLibre
import org.maplibre.android.maps.MapLibreMap

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MapLibre.getInstance(this)
        setContent {
            FinalMapTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var currentTab by remember { mutableStateOf(BottomNavItem.HOME) }
    var titleScreen by remember { mutableStateOf(BottomNavItem.HOME.label) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomNavItem.entries.forEach { i->
                    NavigationBarItem(
                        selected = currentTab == i,
                        onClick = {
                            currentTab = i
                            titleScreen = currentTab.label
                        },
                        label = {
                            Text(i.label)
                        },
                        icon = {
                            Icon(i.icon, i.label )
                        }
                    )
                }

            }
        }
    ) { p->
        Box(
            modifier = Modifier.fillMaxSize().padding(p)
        ){
            when(currentTab){
                BottomNavItem.HOME -> {
                    Text("Home")
                }
                BottomNavItem.MAP -> {
                    MapScreen()
                }
                BottomNavItem.PROFILE -> {
                    Text("Profile")
                }
            }
        }
    }
}
