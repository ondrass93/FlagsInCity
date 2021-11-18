

package com.example.flagsincity.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


/**
 * Defines methods for using the LocationRecord class with Room.
 */
@Dao
interface HistoryDatabaseDao {

    @Insert
    suspend fun insert(historyAction: HistoryAction)


    @Update
    suspend  fun update(historyAction: HistoryAction)


    @Query("SELECT * from record_history_table WHERE actionId = :key")
    suspend fun get(key: Int): HistoryAction?


    @Query("DELETE FROM record_history_table")
    suspend fun clear()


    @Query("SELECT * FROM record_history_table ORDER BY actionId DESC")
    fun getAllActions(): LiveData<List<HistoryAction>>

    @Query("SELECT * FROM record_history_table ORDER BY actionId DESC LIMIT 1")
    suspend fun getCurrentAction(): HistoryAction?


    @Query("SELECT actionId FROM record_history_table ORDER BY actionId DESC LIMIT 1")
    fun getSuppliesCount(): Int?


}
