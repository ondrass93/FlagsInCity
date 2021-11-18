package com.example.flagsincity.data


import android.util.Log
import androidx.lifecycle.LiveData
import com.example.flagsincity.Constants
import com.example.flagsincity.Constants.TAG
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FortressLiveData: LiveData<Response>() {

    private var realTimeDatabase: DatabaseReference =
        Firebase.database("https://flags-in-city-default-rtdb.europe-west1.firebasedatabase.app/").reference


    init {

        val fortressListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val response = Response()

                dataSnapshot.let {
                    response.fortresses = dataSnapshot.children.map { snapShot ->
                        snapShot.getValue(Fortress::class.java)!!
                    }
                    setFortressesData(response)
                    Log.d(TAG, "Content of Realtime DB: ${response.fortresses}")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }

        }


        realTimeDatabase.child(Constants.FORTRESSES_REF).addValueEventListener(fortressListener)
        Log.d(TAG,"LiveData initialized")
    }


    /**
     * If we've received update, this function will be called
     * */
    private fun setFortressesData(response: Response) {
        value = response // setter for FortressLiveDataClass


    }
}
