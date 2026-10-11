package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CachedGitHubItemDao
import com.example.data.local.dao.CachedJiraTicketDao
import com.example.data.local.dao.TaskDao
import com.example.data.local.entity.CachedGitHubItemEntity
import com.example.data.local.entity.CachedJiraTicketEntity
import com.example.data.local.entity.TaskEntity

@Database(
    entities = [
        CachedGitHubItemEntity::class,
        CachedJiraTicketEntity::class,
        TaskEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class SyncFlowDatabase : RoomDatabase() {

    abstract fun gitHubItemDao(): CachedGitHubItemDao
    abstract fun jiraTicketDao(): CachedJiraTicketDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: SyncFlowDatabase? = null

        fun getDatabase(context: Context): SyncFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SyncFlowDatabase::class.java,
                    "syncflow_offline.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
