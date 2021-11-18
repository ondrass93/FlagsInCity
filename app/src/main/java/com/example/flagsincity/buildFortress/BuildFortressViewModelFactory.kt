package com.example.flagsincity.buildFortress

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException


class BuildFortressViewModelFactory(
    private val application: Application): ViewModelProvider.Factory {
    @Suppress ("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BuildFortressViewModel::class.java)) {
            return BuildFortressViewModel(application) as T
        }
        throw IllegalArgumentException("ViewModel class not found.")
    }
}