package com.gunishjain.locationtracker.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.gunishjain.locationtracker.utils.Constants.PROFILE_PIC_BUCKET
import com.gunishjain.locationtracker.utils.fileFromContentUri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.upload
import javax.inject.Inject

class AuthRepository @Inject constructor(private val storage: Storage,
                                         @ApplicationContext private val context: Context) {



    suspend fun uploadToSupaBaseStorage(imageByteArray: ByteArray) {
//        val file = fileFromContentUri(context = context, uri)
//        val path = file.name

        val fileName = "test.jpg"
//
//        val bucket = storage[PROFILE_PIC_BUCKET]
//            bucket.upload(fileName,imageByteArray,true)

    }


}