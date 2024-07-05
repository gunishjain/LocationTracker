package com.gunishjain.locationtracker.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gunishjain.locationtracker.ui.register.SupaBaseAuthViewModel
import com.gunishjain.locationtracker.utils.UserState
import com.gunishjain.locationtracker.utils.uriToByteArray

@Composable
fun HomeScreen(navController: NavHostController, viewModel: SupaBaseAuthViewModel = hiltViewModel()) {

    val userState by viewModel.userState
    val ctx = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Welcome to the Home Screen")

        Button(onClick = {
            viewModel.logout()

        }) {
            Text(text = "Logout")
        }
        LaunchedEffect(userState) {
            when (userState) {
                is UserState.Success -> {
                    if ((userState as UserState.Success).message == "Logged Out Successfully!") {
                        Toast.makeText(ctx, (userState as UserState.Success).message, Toast.LENGTH_LONG).show()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                }
                is UserState.Error -> {
                    Toast.makeText(ctx, (userState as UserState.Error).message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }

    }
}

