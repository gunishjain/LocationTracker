package com.gunishjain.locationtracker.ui.home

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.gunishjain.locationtracker.ui.register.SupaBaseAuthViewModel
import com.gunishjain.locationtracker.utils.UserState
import com.gunishjain.locationtracker.utils.uriToByteArray

@Composable
fun Test(navController: NavHostController, viewModel: SupaBaseAuthViewModel = hiltViewModel()) {
    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val userState by viewModel.userState

    val ctx = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            selectedImageUri = it
        })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        Button(onClick = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }) {
            Text(text = "Pick Image from Gallery")
        }

        AsyncImage(
            modifier = Modifier
                .width(150.dp)
                .height(100.dp),
            model = selectedImageUri,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Button(onClick = {
            val imageByteArray = selectedImageUri?.uriToByteArray(ctx)
            imageByteArray?.let { it ->
                viewModel.uploadImage(it)
            }
        }) {
            Text(text = "Upload")
        }


    }

    LaunchedEffect(userState) {
        when (userState) {
            is UserState.Success -> {

                val message = (userState as UserState.Success).message
                if (message == "Image Uploaded Successfully!") {
                    Toast.makeText(ctx, (userState as UserState.Success).message, Toast.LENGTH_LONG).show()
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                   Log.d("Test","Else block in success")
                }

            }
            is UserState.Error -> {
                Toast.makeText(ctx, (userState as UserState.Error).message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }


}