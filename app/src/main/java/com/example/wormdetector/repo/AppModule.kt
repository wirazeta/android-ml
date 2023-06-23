package com.example.wormdetector.repo

import com.example.wormdetector.data.remote.MLApi
import com.example.wormdetector.data.remote.MLService
import com.example.wormdetector.util.Constaint
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideMLRepo(
        service:MLService
    ): MLRepository = MLRepository(service)

    @Singleton
    @Provides
    fun provideMLService(
        api:MLApi
    ): MLService = MLService(api)

    @Singleton
    @Provides
    fun provideMLApi(): MLApi {
        val okHttpClient:OkHttpClient = OkHttpClient().newBuilder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .baseUrl(Constaint.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MLApi::class.java)
    }
    @Singleton
    @Provides
    fun provideFileRepository(api:MLApi): FileRepository = FileRepository(api)
}