package com.example.roomapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.singsang.Data.Vogel
import com.example.singsang.Hoeren

@Database(entities = [Vogel::class], version = 1, exportSchema = true)
abstract class BirdDatabase : RoomDatabase() {

    abstract fun birdDao(): BirdDao

    companion object {
        @Volatile
        private var INSTANCE: BirdDatabase? = null

        fun getDatabase(context: Context): BirdDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    BirdDatabase::class.java,
                    "bird_database"
                ).createFromAsset("Database/db_FileBirdLife.db")
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}