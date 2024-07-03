package com.gunishjain.locationtracker.ui.register

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gunishjain.locationtracker.data.repository.AuthRepository
import com.gunishjain.locationtracker.utils.Constants.PROFILE_PIC_BUCKET
import com.gunishjain.locationtracker.utils.SharedPreferenceHelper
import com.gunishjain.locationtracker.utils.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class SupaBaseAuthViewModel @Inject constructor(
    private val client: SupabaseClient
) : ViewModel() {

    private val _userState = mutableStateOf<UserState>(UserState.Loading)
    val userState: State<UserState> = _userState

    fun registerUser(
        context : Context,
        username: String,
        useremail: String,
        userpassword: String,
        imageByteArray: ByteArray,
        phone: String
    ) {
        viewModelScope.launch {
            try {
                val filename = username
                val bucket = client.storage[PROFILE_PIC_BUCKET]
                bucket.upload("$filename.jpg",imageByteArray)
                val url = bucket.createSignedUrl("$filename.jpg", expiresIn = 17520.hours)

                Log.d("imageurl:",url)

               val data = client.auth.signUpWith(Email) {
                    email =useremail
                    password = userpassword
                    data = buildJsonObject {
                        put("name",username)
                        put("profile_pic",url)
                        put("user_caste","test")
                        put("phone",phone)

                    }
                }


                saveToken(context)
                Log.d("metadata",client.auth.currentAccessTokenOrNull().toString())


            } catch (e : Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
                Log.d("vm",e.message.toString())

            }
        }
    }


    private fun saveToken(context: Context) {

        viewModelScope.launch {
            val accessToken = client.auth.currentAccessTokenOrNull()
            val sharedPref = SharedPreferenceHelper(context)
            sharedPref.savedStringData("accessToken",accessToken)
        }

    }

    private fun getToken(context: Context): String? {
        val sharedPref = SharedPreferenceHelper(context)
        return sharedPref.getStringData("accessToken")
    }


    fun loginUser(
        context: Context,
        userEmail: String,
        userPassword: String
    ) {
        viewModelScope.launch {
            try {

                client.auth.signInWith(Email) {
                    email=userEmail
                    password=userPassword
                }
                saveToken(context)

            } catch (e: Exception){

            }
        }
    }

    fun logout(context: Context) {

        val sharedPref = SharedPreferenceHelper(context)
        viewModelScope.launch {
            _userState.value = UserState.Loading
            try {
                client.auth.signOut()
                sharedPref.clearPreference()
                _userState.value = UserState.Success("Logged Out Successfully!")

            } catch (e: Exception){
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }


    fun isUserLoggedIn(context: Context) {
        viewModelScope.launch {
            try {

                val token = getToken(context)
                if(token.isNullOrEmpty()){
                    _userState.value = UserState.Error("User is not Logged In!")
                } else {
                    client.auth.retrieveUser(token)
                    client.auth.refreshCurrentSession()
                    saveToken(context)
                    _userState.value = UserState.Success("User is already logged In!")

                }


            } catch (e: Exception){
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }

}