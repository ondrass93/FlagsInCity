package com.example.flagsincity.fortress

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flagsincity.database.HistoryDatabase
import com.example.flagsincity.database.HistoryDatabaseDao
import java.lang.IllegalArgumentException


class FortressViewModelFactory(
    private val database: HistoryDatabaseDao,
    private val application: Application): ViewModelProvider.Factory {
    @Suppress ("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FortressViewModel::class.java)) {
            return FortressViewModel(database ,application) as T
        }
        throw IllegalArgumentException("ViewModel class not found.")
    }
}