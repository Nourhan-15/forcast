package com.example.forcast247.DataBase1

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.forcast247.AlarmEntity
import com.example.forcast247.DataBase1.Remote.RetrofitInstance
import com.example.forcast247.FavouriteData
import com.example.forcast247.WeatherDatabase
import com.example.forcast247.WeatherResponse


public class Repository {

    public suspend fun retrofitWeatherCall(lat:String, lon:String,
                                           units:String="metric", long:String="en") =
        RetrofitInstance.api.getWeatherData(lat,lon,units,long)

    public suspend fun insertWeatherToRoom(context: Context, weather: WeatherResponse) {
        WeatherDatabase.getInstance(context).getWeatherDao().upsert(weather)
    }

    public suspend fun getWeatherFromRoom(context: Context) =
        WeatherDatabase.getInstance(context).getWeatherDao().getAllWeathers()

    /////////////////////////////////////////////////////////////////////////////////////////////////



    public suspend fun retrofitFavCall(lat:String, lon:String,
                                           units:String="metric", long:String="ar")=
        RetrofitInstance.api.getFavData(lat,lon,units,long)

    public suspend fun insertFavWeatherToRoom(context: Context, weather: FavouriteData) {
        WeatherDatabase.getInstance(context).getWeatherDao().insertFavWeatherData(weather)
    }

    public suspend fun getFavWeatherFromRoom(context: Context) =
        WeatherDatabase.getInstance(context).getWeatherDao().getFavWetherData()


    public suspend fun deleteFav(favData: FavouriteData, context: Context) =
        WeatherDatabase.getInstance(context).getWeatherDao().deleteFav(favData)

   //=======================================================================================

    //============================================================================================

    public suspend fun addAlarm(alertDatabase: AlarmEntity, context: Context) =
        WeatherDatabase.getInstance(context).getWeatherDao().insertAlarm(alertDatabase)


    public fun getAlarm(context: Context): LiveData<MutableList<AlarmEntity>> =
        WeatherDatabase.getInstance(context).getWeatherDao().getAlarm()

    public suspend fun deleteAlarm(alertDatabase: AlarmEntity, context: Context) =
        WeatherDatabase.getInstance(context).getWeatherDao().deleteAlarm(alertDatabase)
    }
