package com.example.forcast247
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WeatherDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(weather: WeatherResponse)

    @Query("SELECT * FROM weatherData")
    fun getAllWeathers(): WeatherResponse


    ////////////////////////////////////////////////////////////
    //Favourite
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavWeatherData(fav : FavouriteData)

    @Query("SELECT * FROM favouriteData")
    fun getFavWetherData(): List<FavouriteData>

    @Query("select * from favouriteData where id in (:num)")
   fun getDataById(num: Int): FavouriteData

    @Delete
    fun deleteFav(Fav: FavouriteData)

   /////////////////////////////////////////////////////////////



//=====================================================================
    //Alarm
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alertDatabase: AlarmEntity)

    @Query("select * from alarm_table")
    fun getAlarm(): LiveData<MutableList<AlarmEntity>>

    @Delete
    fun deleteAlarm(alertDatabase: AlarmEntity)

}