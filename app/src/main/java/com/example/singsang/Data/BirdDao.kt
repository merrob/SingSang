package com.example.roomapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.singsang.Data.Vogel

@Dao
interface BirdDao {
    @Query("""SELECT * From Vogel_database  WHERE
        (BirdLife LIKE :birdLife) AND
            (VogelGruppe LIKE :familySelected) AND 
             (VogelName LIKE :query) ORDER BY VogelName ASC""")
    abstract fun readAllData(query: String,familySelected: String,birdLife:String): LiveData<List<Vogel>>

    @Query("""SELECT * From Vogel_database ORDER BY VogelName ASC""")
    abstract fun readAll(): List<Vogel>

    @RawQuery(observedEntities = arrayOf(Vogel::class))
    fun getFamilyNames(query: SupportSQLiteQuery): LiveData<List<Vogel>>
}