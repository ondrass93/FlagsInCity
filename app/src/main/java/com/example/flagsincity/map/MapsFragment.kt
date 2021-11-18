package com.example.flagsincity.map



import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.flagsincity.Constants.LOC_PRESICION_METRES
import com.example.flagsincity.Constants.MIN_DISTANCE_METRES
import com.example.flagsincity.Constants.TAG
import com.example.flagsincity.R
import com.example.flagsincity.database.HistoryDatabase
import com.example.flagsincity.databinding.FragmentMapsBinding
import com.example.flagsincity.mapsViewModel.MapsViewModel
import com.example.flagsincity.mapsViewModel.MapsViewModelFactory
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34

class MapsFragment : Fragment(), GoogleMap.OnCircleClickListener {


    // Each location callback currentLat, currentLong change
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var googleMap: GoogleMap
    private lateinit var progressBar: ProgressBar

    private lateinit var buttonGetSupply: Button
    private lateinit var buttonGiveSupply: Button
    private lateinit var buttonRecordHome: Button
    private lateinit var buttonRecordFortress: Button
    //private lateinit var textSupplyAlert: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        Log.d("Navigation", "Navigated to Maps Fragment")

        val binding: FragmentMapsBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_maps, container, false)

        buttonRecordHome = binding.buttonRecordHome
        binding.buttonRecordHome.setOnClickListener {
            if (setHomeLocation()) {

                it.visibility = INVISIBLE
            }
        }
        buttonRecordFortress = binding.buttonRecordFortress
        binding.buttonRecordFortress.setOnClickListener {
            if (setFortressLocation()) {

                it.visibility = INVISIBLE
            }
        }



        buttonGetSupply = binding.buttonGetSupply
        buttonGiveSupply = binding.buttonGiveSupply


        /*
        binding.buttonGetSupply.setOnClickListener {
            it.visibility = INVISIBLE
        }

        binding.buttonGiveSupply.setOnClickListener {
            it.visibility = INVISIBLE
        }*/

        progressBar = binding.progressBar
        progressBar.visibility = VISIBLE





        val application = requireNotNull(this.activity).application

        // Create an instance of the ViewModel Factory.
        val dataSource = HistoryDatabase.getInstance(application).historyDatabaseDao

        val viewModelFactory = MapsViewModelFactory(dataSource, application)


        mapsViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(MapsViewModel::class.java)

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.mapsViewModel = mapsViewModel

        mapsViewModel.getButtonVisible.observe(viewLifecycleOwner, Observer {
            buttonGetSupply.isEnabled = it
        })

        mapsViewModel.giveButtonVisible.observe(viewLifecycleOwner, Observer {
            buttonGiveSupply.isEnabled = it
        })

        prepRequestLocationUpdates()

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        if (mapFragment == null) {
            Log.d("MapsFragment", "mapFragment is null")

        }
    }

    private fun prepRequestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE)
        }
                
    }

    private fun requestLocationUpdates() {
        mapsViewModel.getLocationLiveData().observe(viewLifecycleOwner, Observer {

            Log.d("LocationLiveDataObserve","Location data changed!")
            currentLatitude = it.latitude
            currentLongitude  = it.longitude


            // Get supply visibility
            if (mapsViewModel.isNearHome(currentLatitude, currentLongitude))  {
                buttonGetSupply.visibility = VISIBLE
            } else {
                buttonGetSupply.visibility = INVISIBLE
            }

            // Give supply to fortress visibility
            if (mapsViewModel.isNearFortress(currentLatitude, currentLongitude)) {
                buttonGiveSupply.visibility = VISIBLE
            } else {
                buttonGiveSupply.visibility = INVISIBLE
            }

            // Record Home clickable
            buttonRecordHome.isEnabled = buttonRecordHome.visibility == VISIBLE && mapsViewModel.isHomeFar(currentLatitude, currentLongitude)

            // Record Home clickable
            buttonRecordFortress.isEnabled = buttonRecordFortress.visibility == VISIBLE && mapsViewModel.isFortressFar(currentLatitude, currentLongitude)

        })
    }


    private fun setHomeLocation (): Boolean {
        return if (currentLatitude == 0.0 && currentLongitude == 0.0) {
            Toast.makeText(context, "Turn on GPS", Toast.LENGTH_SHORT).show()
            false
        } else {
            mapsViewModel.onSetHome(currentLatitude, currentLongitude)
            Toast.makeText(context, "Home set successfully!", Toast.LENGTH_SHORT).show()
            true
        }
    }


    private fun setFortressLocation (): Boolean {
        return if (currentLatitude == 0.0 && currentLongitude == 0.0) {
            Toast.makeText(context, "Turn on GPS", Toast.LENGTH_SHORT).show()
            false
        } else {
            mapsViewModel.onSetFortress(currentLatitude, currentLongitude, "Fortress")
            Toast.makeText(context, "Fortress built successfully!", Toast.LENGTH_SHORT).show()
            true
        }
    }



    override fun onCircleClick(p0: Circle) {
        TODO("Not yet implemented")
    }


    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { it ->
        googleMap = it


        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
            return@OnMapReadyCallback
        }
        googleMap.isMyLocationEnabled = true


        // new home marker
        if (mapsViewModel.homeLatitude != 0.0 && mapsViewModel.homeLongitude != 0.0) {
            val markerHome = LatLng(mapsViewModel.homeLatitude, mapsViewModel.homeLongitude)
            googleMap.addMarker(
                MarkerOptions()
                    .position(markerHome)
                    .title(mapsViewModel.userNickname)
                    .snippet("Home")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
            googleMap.addCircle(
                CircleOptions()
                    .center(markerHome)
                    .clickable(true)
                    .fillColor(0x60808080)
                    .visible(true)
                    .radius(MIN_DISTANCE_METRES)
            )
            googleMap.addCircle(
                CircleOptions().center(markerHome).clickable(true).visible(true).radius(LOC_PRESICION_METRES)
            )



            googleMap.moveCamera(CameraUpdateFactory.zoomTo(15.0F))

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(markerHome))


        }


  /*      *//**
         * Showing basic location markers on Map - iterating through livedata<list<LocationRecord>>
         *//*
        mapsViewModel.allRecords.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d("MapsObserver", "'allRecord' changed!")

                val markers = mapsViewModel.allRecords

                if (markers.value.isNullOrEmpty()) {
                    Log.d("MapsObserver", "Location DB is empty.")
                    Toast.makeText(context, "Database is empty!", Toast.LENGTH_SHORT)
                } else {
                    Log.d("MapsObserver", markers.value!!.toString())
                    val listIterator = markers.value!!.listIterator()
                    while (listIterator.hasNext()) {

                        val marker = markers.value!![listIterator.nextIndex()]

                        val latitude = marker.locationLatitude
                        val longitude = marker.locationLongitude
                        val title = "location no: " + marker.locationId.toString()
                        val info = marker.typeOfLocation

                        val markerLatLang = LatLng(latitude, longitude)
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(markerLatLang)
                                .title(title)
                                .snippet(info)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        )

                        Log.d("MapsObserver", latitude.toString()+longitude.toString())
                        Log.d("MapsObserver", listIterator.next().toString())
                    }
                }

            }
        })*/

        Log.d("MapsFragment",mapsViewModel.allRecords.toString())

        /**
         * Showing Fortress locations markers on Map - iterating through response.fortresses
         */

        mapsViewModel.getFortressLiveData().observe(viewLifecycleOwner, Observer {
            Log.d(TAG,"Realtime Database data changed!")

            val markers =  mapsViewModel.getFortressLiveData().value!!.fortresses
            if (markers == null) {

                Log.d(TAG, "Cannot read Realtime database.")
                Toast.makeText(context, "Database is empty!", Toast.LENGTH_SHORT)

            } else {

                Log.d(TAG,"For Fortress markers $markers")

                val fortressListIterator = markers.listIterator()

                while (fortressListIterator.hasNext()) {

                    val marker = markers[fortressListIterator.nextIndex()]

                    Log.d(TAG,"Actual marker: $marker")

                    val latitude = marker.latitude
                    val longitude = marker.longitude
                    val title = "Owner: " + marker.userName
                    //val info = marker.typeOfLocation
                    val markerLatLang = LatLng(latitude!!, longitude!!)
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(markerLatLang)
                            .title(title)
                            //.snippet(info)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                    )

                    // Iterating
                    fortressListIterator.next()
                    Log.d(TAG,"Iterating")
                }
            }


        })

        progressBar.visibility = INVISIBLE


    } // End of GoogleMap callback



}