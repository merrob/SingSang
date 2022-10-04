package com.example.singsang.Data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "Vogel_database")
data class Vogel(@PrimaryKey(autoGenerate = false) @ColumnInfo(name = "VogelName") val word: String,
                 @ColumnInfo(name = "VogelGruppe") val secondWord: String,
                 @ColumnInfo(name = "BirdLife") val birdLife: String)