package com.example.flagsincity.fortress

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.flagsincity.database.HistoryDatabaseDao


class FortressViewModel(dataSource: HistoryDatabaseDao, application: Application) : AndroidViewModel(application) {

    private var sharedPref: SharedPreferences = application.getSharedPreferences("myPref", Context.MODE_PRIVATE)!!
    var userNickname: String? = sharedPref.getString("userNickname", null)

    private val homeLatitude = sharedPref.getFloat("homeLatitude", 0.0F).toDouble()
    private val homeLongitude = sharedPref.getFloat("homeLongitude", 0.0F).toDouble()
    val userHomeLocation: String = homeLatitude.toString() + "  " + homeLongitude.toString()

    // identifier for Realtime DB
    val userName = sharedPref.getString("userName", null)

    // user's nick visible to other users
    val userNickName = sharedPref.getString("userNickname", null)

    val database = dataSource

 //   val lives = database.getSuppliesCount()



}