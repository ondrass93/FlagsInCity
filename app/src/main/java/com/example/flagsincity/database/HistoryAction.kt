

package com.example.flagsincity.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "record_history_table")
data class HistoryAction(
        @PrimaryKey(autoGenerate = true)
        var actionId: Int = 0,

        @ColumnInfo(name = "start_time")
        val startTimeMilli: Long = System.currentTimeMillis(),

        @ColumnInfo(name = "end_time")
        var endTimeMilli: Long = startTimeMilli,

        @ColumnInfo(name = "finished")
        var finished: Boolean = false,

)