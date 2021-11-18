package com.example.flagsincity.mapsViewModel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*

@SuppressLint("MissingPermission")
class LocationLiveData(context: Context): LiveData<LocationDetails>() {

    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // UI has started to watch the data

    override fun onActive() {
        super.onActive()
       fusedLocationClient.lastLocation.addOnSuccessListener {
            location: Location? -> location.also {
           if (it != null) {
               setLocationData(it)
           }
            }
        }

        startLocationUpdates()
        Log.d("LocationLiveData", "Started Location updates")
    }

    override fun onInactive() {
        super.onInactive()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("LocationLiveData", "Ended Location updates")
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    /**
     * If we've received location update, this function will be called
     * */
    private fun setLocationData(location: Location) {
        value = LocationDetails(location.latitude, location.longitude)// setter for LocationLiveDataClass
        Log.d("LocationLiveData", value!!.latitude.toString())
        Log.d("LocationLiveData", value!!.longitude.toString())

    }


    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult?) {

            locationResult?: return

            for (location in locationResult.locations) {
                setLocationData(location)
            }
        }
    }

    companion object {
        private const val tenSeconds: Long = 10000
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = tenSeconds/2
            fastestInterval = tenSeconds/4
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            Log.d("LocationLiveData", "Location request created!")

        }
    }

}
