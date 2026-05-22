package com.example.data.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var instance: ThalaDatabase? = null

    fun getDatabase(context: Context): ThalaDatabase {
        return instance ?: synchronized(this) {
            val db = Room.databaseBuilder(
                context.applicationContext,
                ThalaDatabase::class.java,
                "thala_database_sqlite"
            )
                .fallbackToDestructiveMigration()
                .build()
            instance = db
            db
        }
    }
}
