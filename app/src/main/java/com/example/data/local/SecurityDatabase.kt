package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ScanHistoryEntity::class, UrlScanEntity::class], version = 1, exportSchema = false)
abstract class SecurityDatabase : RoomDatabase() {
    abstract fun scanHistoryDao(): ScanHistoryDao
    abstract fun urlScanDao(): UrlScanDao

    companion object {
        @Volatile
        private var INSTANCE: SecurityDatabase? = null

        fun getDatabase(context: Context): SecurityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SecurityDatabase::class.java,
                    "himoyachi_security_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
