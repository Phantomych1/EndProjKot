package com.example.myapplication.data.repo

import com.example.myapplication.data.api.NominatimApi
import com.example.myapplication.data.api.OpenMeteoApi
import com.example.myapplication.data.db.CacheDao
import com.example.myapplication.data.db.ForecastEntity
import com.example.myapplication.data.db.PlaceEntity
import com.example.myapplication.domain.model.Forecast
import com.example.myapplication.domain.model.Place

class PlacesRepo(
    private val api: NominatimApi,
    private val dao: CacheDao
) {
    suspend fun searchRemote(q: String, limit: Int, offset: Int): List<Place> {
        val res = api.search(q = q, limit = limit, offset = offset)
        val now = System.currentTimeMillis()

        val mapped = res.map {
            Place(
                id = it.placeId.toString(),
                name = it.displayName,
                lat = it.lat.toDoubleOrNull() ?: 0.0,
                lon = it.lon.toDoubleOrNull() ?: 0.0
            )
        }

        dao.upsertPlaces(mapped.map { PlaceEntity(it.id, it.name, it.lat, it.lon, now) })
        return mapped
    }

    suspend fun searchLocal(q: String, limit: Int, offset: Int): List<Place> {
        return dao.searchPlacesLocal(q, limit, offset).map {
            Place(it.id, it.name, it.lat, it.lon)
        }
    }
}

class ForecastRepo(
    private val api: OpenMeteoApi,
    private val dao: CacheDao
) {
    private fun key(lat: Double, lon: Double) = "$lat,$lon"

    suspend fun getForecast(lat: Double, lon: Double, forceRemote: Boolean): Forecast {
        val k = key(lat, lon)
        val cached = dao.getForecast(k)
        if (!forceRemote && cached != null) {
            return Forecast(lat, lon, cached.tempC, cached.wind)
        }

        val remote = api.forecast(lat = lat, lon = lon)
        val f = Forecast(lat, lon, remote.current.temp, remote.current.wind)
        dao.upsertForecast(ForecastEntity(k, lat, lon, f.tempC, f.wind, System.currentTimeMillis()))
        return f
    }
}
