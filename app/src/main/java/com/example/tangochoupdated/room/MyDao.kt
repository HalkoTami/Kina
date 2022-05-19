package com.example.tangochoupdated.room

import androidx.room.*
import com.example.tangochoupdated.UserAndAllData

@Dao
interface MyDao{
    @Transaction
    @Query("select * from user")
    fun loadAllData(): List<UserAndAllData>

    @Insert
    fun insertFile(vararg files: File)

    @Insert
    fun insertCard(vararg cards: Card)

    @Delete
    fun deleteFile(vararg files: File)

    @Query("DELETE FROM word_table")
    suspend fun deleteAll()

    @Transaction
    @Query("select * from file")
    fun loadFileAndCard(): List<FileAndCard>

    @Transaction
    @Query("select * from card")
    fun loadCardsandData(): List<CardAndData>
}

