package com.example.tangochoupdated.room

import androidx.room.*


interface DataAccessObject <T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: T)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(entity: T)

    @Update suspend fun update(entity: T)

    @Delete suspend fun delete(entity: T)

}

