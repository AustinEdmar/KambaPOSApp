package com.austin.kambapos.di

import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.austin.kambapos.data.api.CoreUrl.HOST
import com.austin.kambapos.data.api.CoreUrl.PUSHER_APIKEY
import com.austin.kambapos.data.api.CoreUrl.PUSHER_PORT

@Module
@InstallIn(SingletonComponent::class)
object PusherModule {

    @Provides
    @Singleton
    fun providePusher(): Pusher {
        val options = PusherOptions().apply {
            setHost(HOST)
            setWsPort(PUSHER_PORT)
            isUseTLS = false
        }
        return Pusher(PUSHER_APIKEY, options)
    }
}