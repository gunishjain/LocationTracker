package com.gunishjain.locationtracker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gunishjain.locationtracker.ui.LoadingComponent
import com.gunishjain.locationtracker.ui.home.HomeScreen
import com.gunishjain.locationtracker.ui.home.Test
import com.gunishjain.locationtracker.ui.register.LoginScreen
import com.gunishjain.locationtracker.ui.register.RegisterScreen
import com.gunishjain.locationtracker.ui.register.SupaBaseAuthViewModel
import com.gunishjain.locationtracker.ui.theme.LocationTrackerTheme
import com.gunishjain.locationtracker.utils.UserState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocationTrackerTheme {
                AppNavHost()
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    viewModel: SupaBaseAuthViewModel = hiltViewModel()
) {
    val userState by viewModel.userState

    NavHost(navController = navController, startDestination = "loading") {
        composable("home") { HomeScreen(navController, viewModel) }
        composable("register") { RegisterScreen(navController, viewModel) }
        composable("login") { LoginScreen(navController, viewModel) }
        composable("test") { Test(navController,viewModel)}
        composable("loading") { LoadingComponent() }
    }

    LaunchedEffect(Unit) {
        viewModel.isUserLoggedIn()
    }
    when (userState) {
        is UserState.Loading -> {
            LoadingComponent()
        }

        is UserState.Success -> {
            val message = (userState as UserState.Success).message
            when (message) {
                "User is already logged In!" -> {
                    navController.navigate("home") {
                        popUpTo("loading") { inclusive = true }
                    }
                }
                "Registration successful" -> {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
                "Login successful" -> {
                    navController.navigate("test") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }

        is UserState.Error -> navController.navigate("login") {
            popUpTo("loading") { inclusive = true }
            Log.d("gunish","here in when")
        }
    }
}



