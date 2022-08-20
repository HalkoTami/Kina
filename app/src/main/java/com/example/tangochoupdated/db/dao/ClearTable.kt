package com.example.tangochoupdated.db.dao

import androidx.room.Query

interface ClearTable {
    @Query("DELETE FROM tbl_card")
    suspend fun clearTblCard()

    @Query("DELETE FROM tbl_activity_data")
    suspend fun clearTblActivity()
    @Query("DELETE FROM tbl_choice")
    suspend fun clearTblChoice()

    @Query("DELETE FROM tbl_file")
    suspend fun clearTblFile()

    @Query("DELETE FROM tbl_marker_data")
    suspend fun clearTblMarkerData()

    @Query("DELETE FROM tbl_user")
    suspend fun clearTblUser()

    @Query("DELETE FROM file_xref")
    suspend fun clearTblFileXRef()


}