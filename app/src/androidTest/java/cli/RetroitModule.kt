package cli
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.example.wormdetector.data.remote.MLApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.wormdetector.util.Constaint.Companion.BASE_URL
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetroitModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideMLApi(retrofit: Retrofit): MLApi {
        return retrofit.create(MLApi::class.java)
    }


}