package com.example.flagsincity.fortress


import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.flagsincity.R
import com.example.flagsincity.database.HistoryDatabase
import com.example.flagsincity.databinding.FragmentFortressBinding
import com.example.flagsincity.mapsViewModel.MapsViewModel
import com.example.flagsincity.mapsViewModel.MapsViewModelFactory

class FortressFragment : Fragment() {

    private lateinit var fortressViewModel: FortressViewModel
    private lateinit var progressBar: ProgressBar
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {



        val binding: FragmentFortressBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_fortress, container, false)

        Log.d("Navigation", "Navigated to fortress Fragment")



        binding.buttonChangeNickname.setOnClickListener { changeNickname() }
        progressBar = binding.progressBar// .setProgress(50, true)

/*        binding.userNickname.text = userNickNameSharedPref

        if (homeLatitude == 0.0 && homeLongitude == 0.0) {
                Toast.makeText(context, "Home is not set", Toast.LENGTH_SHORT).show()
            } else {
                binding.yourHomeLocation.text = userHomeLocation
        }*/

        val application = requireNotNull(this.activity).application
        // Create an instance of the ViewModel Factory.
        val dataSource = HistoryDatabase.getInstance(application).historyDatabaseDao
        val viewModelFactory = FortressViewModelFactory(dataSource ,application)

        fortressViewModel = ViewModelProvider(this, viewModelFactory).get(FortressViewModel::class.java)

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.fortressViewModel = fortressViewModel

/*        if (fortressViewModel.lives == null) {
            progressBar.progress = 0
        } else {
            progressBar.progress = fortressViewModel.lives!!
        }*/


        progressBar.progress = 50


        // Image for fortress
        val fortressDrawableResource = R.drawable.tower
        binding.fortressView.setImageResource(fortressDrawableResource)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // user just installed this app
        if (fortressViewModel.userNickname == null) {
            findNavController().navigate(R.id.action_fortress_to_build)
        }
    }

    private fun changeNickname() {
        findNavController().navigate(R.id.action_fortress_to_build)
    }

}