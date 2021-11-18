package com.example.flagsincity.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.flagsincity.database.HistoryAction
import com.example.flagsincity.database.HistoryDatabaseDao


class HistoryViewModel(dataSource: HistoryDatabaseDao, application: Application) : AndroidViewModel(application) {

    val database = dataSource

    private var currentAction = MutableLiveData<HistoryAction?>()

    val actions = database.getAllActions()

}