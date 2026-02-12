package com.example.myapplication.data.db

import androidx.room.*

@Entity(tableName = "place_cache")
data class PlaceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val savedAt: Long
)

@Entity(tableName = "forecast_cache")
data class ForecastEntity(
    @PrimaryKey val key: String,
    val lat: Double,
    val lon: Double,
    val tempC: Double,
    val wind: Double,
    val savedAt: Long
)

@Dao
interface CacheDao {
    @Query("SELECT * FROM place_cache WHERE name LIKE '%' || :q || '%' ORDER BY savedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun searchPlacesLocal(q: String, limit: Int, offset: Int): List<PlaceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPlaces(xs: List<PlaceEntity>)

    @Query("SELECT * FROM forecast_cache WHERE key=:key LIMIT 1")
    suspend fun getForecast(key: String): ForecastEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertForecast(x: ForecastEntity)
}

@Database(entities = [PlaceEntity::class, ForecastEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun dao(): CacheDao
}
