package com.example.myapplication.di

import androidx.room.Room
import com.example.myapplication.data.api.NominatimApi
import com.example.myapplication.data.api.OpenMeteoApi
import com.example.myapplication.data.db.AppDb
import com.example.myapplication.data.repo.BoardRepo
import com.example.myapplication.data.repo.ForecastRepo
import com.example.myapplication.data.repo.PlacesRepo
import com.example.myapplication.ui.vm.BoardVm
import com.example.myapplication.ui.vm.DetailsVm
import com.example.myapplication.ui.vm.SearchVm
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

val appModule = module {
    single { Room.databaseBuilder(get(), AppDb::class.java, "endproj.db").build() }
    single { get<AppDb>().dao() }

    single {
        val json = Json { ignoreUnknownKeys = true }
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .header("User-Agent", "EndProj/1.0 (student)")
                    .build()
                chain.proceed(req)
            }
            .build()

        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(NominatimApi::class.java)
    }

    single {
        val json = Json { ignoreUnknownKeys = true }
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/v1/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(OpenMeteoApi::class.java)
    }

    single { PlacesRepo(get(), get()) }
    single { ForecastRepo(get(), get()) }
    single { BoardRepo() }

    viewModel { SearchVm(get()) }
    viewModel { (lat: Double, lon: Double) -> DetailsVm(get(), lat, lon) }
    viewModel { BoardVm(get()) }
}
