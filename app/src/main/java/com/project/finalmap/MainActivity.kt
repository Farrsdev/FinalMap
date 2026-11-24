package com.project.finalmap

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.project.finalmap.data.vm.AuthVm
import com.project.finalmap.ui.screen.HomeScreen
import com.project.finalmap.ui.screen.LoginScreen
import androidx.compose.ui.tooling.preview.Preview
import com.project.finalmap.ui.screen.MapScreen
import com.project.finalmap.ui.screen.ProfileScreen
import com.project.finalmap.ui.screen.RegisterScreen
import com.project.finalmap.ui.theme.FinalMapTheme
import com.project.finalmap.util.nav.BottomNavItem
import com.project.finalmap.util.security.PhoneUtils
import org.maplibre.android.MapLibre
import org.maplibre.android.maps.MapLibreMap

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MapLibre.getInstance(this)
        PhoneUtils.initialize(this)
        setContent {
            FinalMapTheme {
                AppRoot()
            }
        }
    }
}

private enum class AuthPage { LOGIN, REGISTER, MAIN }
@Composable
fun AppRoot(vm: AuthVm = viewModel()) {
    val context = LocalContext.current
    var page by remember {
        mutableStateOf(if (FirebaseAuth.getInstance().currentUser != null) AuthPage.MAIN else AuthPage.LOGIN)
    }

    LaunchedEffect(vm.successMsg, vm.errorMsg) {
        (vm.errorMsg ?: vm.successMsg)?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            vm.clearMsg()
        }
    }

    when (page) {
        AuthPage.LOGIN -> LoginScreen(onLoginSuccess = { page = AuthPage.MAIN }, onRegisterClick = { page = AuthPage.REGISTER })
        AuthPage.REGISTER -> RegisterScreen(onRegisterSuccess = { page = AuthPage.LOGIN }, onLoginClick = { page = AuthPage.LOGIN })
        AuthPage.MAIN -> {
            LaunchedEffect(Unit) { vm.getCurrentUser() }
            MainScreen(onLogout = { page = AuthPage.LOGIN })
        }
    }
}

@Composable
fun MainScreen(onLogout: () -> Unit, modifier: Modifier = Modifier) {
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
                    HomeScreen()
                }
                BottomNavItem.MAP -> {
                    MapScreen()
                }
                BottomNavItem.PROFILE -> {
                    ProfileScreen(onLogout = onLogout)
                }
            }
        }
    }
}
