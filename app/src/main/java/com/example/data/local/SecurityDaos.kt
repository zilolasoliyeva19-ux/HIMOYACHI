package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {
    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<ScanHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanHistoryEntity): Long

    @Query("DELETE FROM scan_history")
    suspend fun clearHistory()
}

@Dao
interface UrlScanDao {
    @Query("SELECT * FROM url_scan_history ORDER BY timestamp DESC LIMIT 50")
    fun getRecentUrlScans(): Flow<List<UrlScanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUrlScan(scan: UrlScanEntity): Long

    @Query("DELETE FROM url_scan_history")
    suspend fun clearUrlHistory()
}
