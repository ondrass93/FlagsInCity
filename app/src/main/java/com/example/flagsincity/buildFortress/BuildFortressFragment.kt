package com.example.flagsincity.buildFortress

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.flagsincity.R
import com.example.flagsincity.databinding.FragmentBuildFortressBinding



class BuildFortressFragment : Fragment() {

    private lateinit var buildFortressViewModel: BuildFortressViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment


        val binding: FragmentBuildFortressBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_build_fortress, container, false)

        val application = requireNotNull(this.activity).application

        val viewModelFactory = BuildFortressViewModelFactory(application)


        buildFortressViewModel = ViewModelProvider(this, viewModelFactory).get(
            BuildFortressViewModel::class.java)

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.buildFortressViewModel = buildFortressViewModel

        if (buildFortressViewModel.userNickname != null) {
            binding.editTextNickname.hint = buildFortressViewModel.userNickname
        }


        drawableUpdate(binding.homeLocationState, buildFortressViewModel.homeIsSet)
        drawableUpdate(binding.fortressLocationState, buildFortressViewModel.fortressIsSet)


        // Set a click listener for button widget
        binding.buttonChangeHome.setOnClickListener{
            // Initialize a new instance of
            val builder = AlertDialog.Builder(activity)

            // Set the alert dialog title
            builder.setTitle("Delete home location")

            // Display a message on alert dialog
            builder.setMessage("Are you sure you want to change your home's location?")

            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton("YES"){dialog, which ->
                // Do something when user press the positive button
                Toast.makeText(activity,"Go to map to set new home location!",Toast.LENGTH_SHORT).show()

                // Change the app background color
                buildFortressViewModel.prepareToChangeHome()
                drawableUpdate(binding.homeLocationState, buildFortressViewModel.homeIsSet)

            }

            // Display a negative button on alert dialog
            builder.setNegativeButton("No"){dialog,which ->
                Toast.makeText(activity,"No changes.",Toast.LENGTH_LONG).show()
            }

            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()

            // Display the alert dialog on app interface
            dialog.show()
        }

        // Set a click listener for button widget
        binding.buttonChangeFortress.setOnClickListener{
            // Initialize a new instance of
            val builder = AlertDialog.Builder(activity)

            // Set the alert dialog title
            builder.setTitle("Delete Fortress location")

            // Display a message on alert dialog
            builder.setMessage("Are you sure you want to change your fortress's location?")

            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton("YES"){dialog, which ->
                // Do something when user press the positive button
                Toast.makeText(activity,"Go to map to set new Fortress location!",Toast.LENGTH_SHORT).show()

                // Change the app background color
                buildFortressViewModel.prepareToChangeFortress()
                drawableUpdate(binding.fortressLocationState, buildFortressViewModel.fortressIsSet)
            }

            // Display a negative button on alert dialog
            builder.setNegativeButton("No"){dialog,which ->
                Toast.makeText(activity,"No changes.",Toast.LENGTH_LONG).show()
            }

            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()

            // Display the alert dialog on app interface
            dialog.show()
        }



        binding.buttonSaveNickname.setOnClickListener {
            val userNickname = binding.editTextNickname.text.toString()

            // Storing new nickname to shared preferences
            buildFortressViewModel.editNickname(userNickname)

            Log.d("userNameSharedPref", userNickname)
            findNavController().navigate(R.id.action_build_to_fortress)
        }

        return binding.root
    }



    private fun drawableUpdate (imageView: ImageView, isSet: Boolean) {
        if (isSet) {
            imageView.setImageResource(R.drawable.location)
        }else {
            imageView.setImageResource(R.drawable.ic_baseline_warning_24)
        }
    }
}