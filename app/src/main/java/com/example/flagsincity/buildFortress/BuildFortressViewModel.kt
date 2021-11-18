package com.example.flagsincity.buildFortress

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel


class BuildFortressViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPref: SharedPreferences = application.getSharedPreferences("myPref", Context.MODE_PRIVATE)!!


    private val editor = sharedPref.edit()


    private val homeLatitude = sharedPref.getFloat("homeLatitude", 0.0F).toDouble()
    private val homeLongitude = sharedPref.getFloat("homeLongitude", 0.0F).toDouble()

    var homeIsSet = sharedPref.getBoolean("HomeIsSet", true)
    var fortressIsSet = sharedPref.getBoolean("FortressIsSet", true)


    // user's nick visible to other users
    val userNickname = sharedPref.getString("userNickname", null)


    fun editNickname(newNickname: String) {
        editor?.apply {
            putString("userNickname", newNickname)
            apply()

        }
    }

    fun prepareToChangeHome () {
        editor?.apply {
            putBoolean("HomeIsSet", false)
            commit()
            homeIsSet = sharedPref.getBoolean("HomeIsSet", true)
        }
    }

    fun prepareToChangeFortress () {
        editor?.apply {
            putBoolean("FortressIsSet", false)
            commit()
            fortressIsSet = sharedPref.getBoolean("FortressIsSet", true)
        }
    }

}