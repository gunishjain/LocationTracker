package com.gunishjain.locationtracker.ui.register

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gunishjain.locationtracker.data.model.User
import com.gunishjain.locationtracker.utils.Constants.PROFILE_PIC_BUCKET
import com.gunishjain.locationtracker.utils.SharedPreferenceHelper
import com.gunishjain.locationtracker.utils.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.json.JSONObject
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

@HiltViewModel
class SupaBaseAuthViewModel @Inject constructor(
    private val client: SupabaseClient,
    private val sharedPreferenceHelper: SharedPreferenceHelper
) : ViewModel() {

    private val _userState = mutableStateOf<UserState>(UserState.Loading)
    val userState: State<UserState> = _userState

    fun registerUser(
        username: String,
        userEmail: String,
        userPassword: String,
        caste: String,
//        imageByteArray: ByteArray,
        phone: String
    ) {
        viewModelScope.launch {
            try {

                val data = client.auth.signUpWith(Email) {
                    email = userEmail
                    password = userPassword
                    data = buildJsonObject {
                        put("name", username)
                        put("user_caste", caste)
                        put("profile_pic", "default")
                        put("phone", phone)

                    }
                }

                saveToken()

                _userState.value = UserState.Success("Registration successful")

            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
                Log.d("vm-register", e.message.toString())

            }
        }
    }


    private fun saveToken() {

        viewModelScope.launch {
            val accessToken = client.auth.currentAccessTokenOrNull()
            val sharedPref = sharedPreferenceHelper
            sharedPref.savedStringData("accessToken", accessToken)
        }

    }

    private fun getToken(): String? {
        val sharedPref = sharedPreferenceHelper
        return sharedPref.getStringData("accessToken")
    }


    fun loginUser(
        userEmail: String,
        userPassword: String
    ) {
        viewModelScope.launch {
            try {

                client.auth.signInWith(Email) {
                    email = userEmail
                    password = userPassword
                }
                saveToken()
                _userState.value = UserState.Success("Login successful")

            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }

    fun logout() {

        viewModelScope.launch {
            _userState.value = UserState.Loading
            try {
                client.auth.signOut()
                sharedPreferenceHelper.clearPreference()
                _userState.value = UserState.Success("Logged Out Successfully!")

            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }


    fun isUserLoggedIn() {
        viewModelScope.launch {
            try {

                val token = getToken()
                if (token.isNullOrEmpty()) {
                    _userState.value = UserState.Error("User is not Logged In!")
                    Log.d("gunish", "here")
                } else {
                    client.auth.retrieveUser(token)
                    client.auth.refreshCurrentSession()
                    saveToken()
                    _userState.value = UserState.Success("User is already logged In!")

                }

            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }

    fun uploadImage(imageByteArray: ByteArray) {

        viewModelScope.launch {

            try {
                val user = client.auth.retrieveUserForCurrentSession()
                val metadata = user.userMetadata
                Log.d("metadata,", metadata.toString())

                val filename = metadata?.get("phone").toString()
                val withoutQuote = filename.replace("\"", "")
                Log.d("filename",withoutQuote)

                val bucket = client.storage[PROFILE_PIC_BUCKET]
                bucket.upload("$withoutQuote.jpg", imageByteArray)
                val url = bucket.createSignedUrl("$withoutQuote.jpg", expiresIn = 17520.hours)

                Log.d("image url:", url)

                val uuid= getToken()?.let { client.auth.retrieveUser(it) }?.id
                Log.d("user uuid",uuid.toString())


                client.postgrest["registeredusers"].update(
                    {

                        User::profile_pic setTo url

                    }
                ) {
                    filter {
                        User::id eq uuid
                    }
                }

                _userState.value = UserState.Success("Image Uploaded Successfully!")

            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
                Log.d("Error in upload",e.message.toString())
            }

        }

    }


}