package com.example.flagsincity.mapsViewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flagsincity.database.HistoryDatabaseDao
import java.lang.IllegalArgumentException


class MapsViewModelFactory(
    private val dataSource: HistoryDatabaseDao,
    private val application: Application): ViewModelProvider.Factory {
    @Suppress ("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("ViewModel class not found.")
    }
}