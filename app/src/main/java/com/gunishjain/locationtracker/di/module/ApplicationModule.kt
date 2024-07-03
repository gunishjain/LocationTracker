package com.gunishjain.locationtracker.di.module

import com.gunishjain.locationtracker.utils.Constants.API_KEY
import com.gunishjain.locationtracker.utils.Constants.BASE_URL
import com.gunishjain.locationtracker.utils.Constants.HOST_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.FlowType
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideSupaBaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BASE_URL,
            supabaseKey = API_KEY
        ) {
            install(Postgrest)
            install(Auth)
            install(Storage)
        }
    }

    @Provides
    @Singleton
    fun provideSupaBaseDatabase(client: SupabaseClient): Postgrest {
        return client.postgrest
    }

    @Provides
    @Singleton
    fun provideSupaBaseAuth(client: SupabaseClient): Auth {
        return client.auth
    }


    @Provides
    @Singleton
    fun provideSupaBaseStorage(client: SupabaseClient): Storage {
        return client.storage
    }


}