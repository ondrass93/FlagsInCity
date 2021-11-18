package com.example.flagsincity.mapsViewModel

import android.app.Application
import android.content.Context
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.flagsincity.Constants.FORTRESSES_REF
import com.example.flagsincity.Constants.LOC_PRESICION
import com.example.flagsincity.Constants.MIN_DISTANCE
import com.example.flagsincity.data.Fortress
import com.example.flagsincity.data.FortressLiveData
import com.example.flagsincity.database.HistoryAction
import com.example.flagsincity.database.HistoryDatabaseDao
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlin.math.pow

class MapsViewModel(dataSource: HistoryDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val applicationRef = application


    private var sharedPref = applicationRef.getSharedPreferences("myPref", Context.MODE_PRIVATE)!!
    // Get user info from Shared preferences
    var userNickname = sharedPref.getString("userNickname", null)!!
    // userName = RealtimeDB key = user email address taken from Google authentication
    var userName = sharedPref.getString("userName", null).toString()

    // Coordinates of Home
    var homeLatitude = sharedPref.getFloat("homeLatitude", 0.0F).toDouble()
    var homeLongitude = sharedPref.getFloat("homeLongitude", 0.0F).toDouble()
    var fortressLatitude = sharedPref.getFloat("fortressLatitude", 0.0F).toDouble()
    var fortressLongitude = sharedPref.getFloat("fortressLongitude", 0.0F).toDouble()


    val setHomeVisibility = when(sharedPref.getBoolean("HomeIsSet", true)) {
        false -> View.VISIBLE
        true -> View.INVISIBLE
    }


    val setFortressVisibility = when(sharedPref.getBoolean("FortressIsSet", true)) {
        false -> View.VISIBLE
        true -> View.INVISIBLE
    }





    fun isNearHome(currentLatitude: Double, currentLongitude: Double): Boolean {
        Log.d("Supply", currentLatitude.toString()+"  "+currentLongitude.toString())
        Log.d("Supply", homeLatitude.toString()+"  "+homeLongitude.toString())
        val latitudeDiff = currentLatitude.minus(homeLatitude).pow(2)
        val longitudeDiff = currentLongitude.minus(homeLongitude).pow(2)
        val distance =  latitudeDiff.plus(longitudeDiff).pow(0.5)
        if (distance < LOC_PRESICION) {
            Log.d("Supply", "Near supply!!!")
        }
        Log.d("Supply", latitudeDiff.toString()+"  "+longitudeDiff.toString())
        Log.d("Supply", "Demanded diff:  "+ LOC_PRESICION.toString())

        return distance < LOC_PRESICION
    }



    fun isNearFortress(currentLatitude: Double, currentLongitude: Double): Boolean {
        val latitudeDiff = currentLatitude.minus(fortressLatitude).pow(2)
        val longitudeDiff = currentLongitude.minus(fortressLongitude).pow(2)
        val distance =  latitudeDiff.plus(longitudeDiff).pow(0.5)
        if (latitudeDiff < LOC_PRESICION && longitudeDiff < LOC_PRESICION) {
            Log.d("Supply", "Near supply!!!")
        }
        Log.d("Supply", latitudeDiff.toString()+"  "+longitudeDiff.toString())
        Log.d("Supply", "Demanded diff:  "+ LOC_PRESICION.toString())

        return distance < LOC_PRESICION
    }

    fun isHomeFar(currentLatitude: Double, currentLongitude: Double): Boolean {

        val latitudeDiff = currentLatitude.minus(homeLatitude).pow(2)
        val longitudeDiff = currentLongitude.minus(homeLongitude).pow(2)
        val distance =  latitudeDiff.plus(longitudeDiff).pow(0.5)
        if (distance > MIN_DISTANCE ) {
            Log.d("Build", "Home is far enough")
        }
        Log.d("Build", distance.toString()+" > "+ MIN_DISTANCE.toString())
        return distance > MIN_DISTANCE
    }


    fun isFortressFar(currentLatitude: Double, currentLongitude: Double): Boolean {
        val latitudeDiff = currentLatitude.minus(fortressLatitude).pow(2)
        val longitudeDiff = currentLongitude.minus(fortressLongitude).pow(2)
        val distance =  latitudeDiff.plus(longitudeDiff).pow(0.5)
        if (distance > MIN_DISTANCE ) {
            Log.d("Build", "Fortress is far enough")
        }
        Log.d("Build", distance.toString()+" > "+ MIN_DISTANCE.toString())
        return distance > MIN_DISTANCE
    }


    // location live data
    private val locationLiveData = LocationLiveData(application)
    fun getLocationLiveData() = locationLiveData

    // fortress live data
    private val fortressLiveData = FortressLiveData()
    fun getFortressLiveData() = fortressLiveData


    // Room DB
    private var currentAction = MutableLiveData<HistoryAction?>()
    val database = dataSource
    val allRecords = database.getAllActions()




    // Realtime DB
    private var realTimeDatabase: DatabaseReference =
        Firebase.database("https://flags-in-city-default-rtdb.europe-west1.firebasedatabase.app/").reference

    /**
     * This function is called whenever user wants to record his location. (by clicking buttons)
     */
    fun onSetHome(currentLatitude: Double, currentLongitude: Double) {

        val homeLatitude = currentLatitude.toFloat()
        val homeLongitude = currentLongitude.toFloat()

        val editor = sharedPref.edit()

        editor.apply {
            putFloat("homeLatitude", homeLatitude)
            putFloat("homeLongitude", homeLongitude)
            putBoolean("HomeIsSet", true)
            apply()

        }
        Log.d("userNameSharedPref", userName)

    }

    /**
     * This function is called whenever user wants to record his location. (by clicking buttons)
     */
    fun onSetFortress(currentLatitude: Double, currentLongitude: Double, typeOfLocation: String = "Supply") {


        // Writing to Realtime Database
        writeNewFortress(userName, userNickname, currentLatitude, currentLongitude, 0)


        val fortressLatitude = currentLatitude.toFloat()
        val fortressLongitude = currentLongitude.toFloat()

        val editor = sharedPref.edit()

        editor.apply {
            putFloat("fortressLatitude", fortressLatitude)
            putFloat("fortressLongitude", fortressLongitude)
            putBoolean("FortressIsSet", true)
            apply()

        }

    }


    private fun writeNewFortress(userName: String, userNickname: String, latitude: Double, longitude: Double, lives: Int) {
        val fortress = Fortress(userNickname, latitude, longitude, lives)

        realTimeDatabase.child(FORTRESSES_REF).child(userName).setValue(fortress)
    }


    init {
        initializeCurrentAction()
    }

    /**
     * If currentAction has not been set, then the GetSupply button should be visible.
     */
    val getButtonVisible = Transformations.map(currentAction) {
        null == it
    }

    val giveButtonVisible = Transformations.map(currentAction) {
        null != it
    }



    private fun initializeCurrentAction() {
        viewModelScope.launch {
            currentAction.value = getCurrentActionFromDatabase()
        }
    }

    /**
     *  Handling the case of the stopped app or forgotten recording,
     *  the start and end times will be the same.j
     *
     *  If the start time and end time are not the same, then we do not have an unfinished
     *  recording.
     */
    private suspend fun getCurrentActionFromDatabase(): HistoryAction? {
        var action = database.getCurrentAction()
        if (action?.endTimeMilli != action?.startTimeMilli) {
            action = null
        }
        return action
    }


    fun onGetSupply() {

        Log.d("RoomDatabase", "Almost inserting!")
        viewModelScope.launch {
            val newAction = HistoryAction()

            Log.d("RoomDatabase", "Inserting!")

            insert(newAction)

            currentAction.value = getCurrentActionFromDatabase()
        }



    }

    fun onGiveSupply() {

        Log.d("RoomDatabase", "Almost inserting!")
        viewModelScope.launch {
            // In Kotlin, the return@label syntax is used for specifying which function among
            // several nested ones this statement returns from.
            // In this case, we are specifying to return from launch().
            val oldAction = currentAction.value ?: return@launch

            // Update the night in the database to add the end time.
            oldAction.endTimeMilli = System.currentTimeMillis()

            update(oldAction)
            currentAction.value = getCurrentActionFromDatabase()
        }



    }



    // Room DB functions

    private suspend fun insert(action: HistoryAction) {
        database.insert(action)
    }

    fun onClearAll () {
        viewModelScope.launch() {
            clear()

            // And clear tonight since it's no longer in the database
            currentAction.value = null
        }
    }


    private suspend fun clear () {
        database.clear()

    }

    private suspend fun update(action: HistoryAction) {
        database.update(action)
    }




}