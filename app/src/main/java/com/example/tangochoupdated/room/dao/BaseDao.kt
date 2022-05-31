package com.example.tangochoupdated.room

import androidx.room.*


interface BaseDao <T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(entity: List<T>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(entity: T)

    @Update suspend fun update(entity: T)


    @Update suspend fun updateMultiple(entity:List<T>)

    @Delete suspend fun delete(entity: T)

    @Delete suspend fun deleteMultiple(entity: List<T>)


}

