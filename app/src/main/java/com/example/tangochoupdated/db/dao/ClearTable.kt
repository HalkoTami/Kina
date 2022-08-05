package com.example.tangochoupdated.db.dao

import androidx.room.Query

interface ClearTable {
    @Query("DELETE FROM tbl_card")
    suspend fun clearTblCard()

    @Query("DELETE FROM tbl_activity_data")
    suspend fun clearTblActivity()
    @Query("DELETE FROM tbl_choice")
    abstract suspend fun clearTblChoice()

    @Query("DELETE FROM tbl_file")
    abstract suspend fun clearTblFile()

    @Query("DELETE FROM tbl_marker_data")
    abstract suspend fun clearTblMarkerData()

    @Query("DELETE FROM tbl_user")
    abstract suspend fun clearTblUser()

    @Query("DELETE FROM file_xref")
    abstract suspend fun clearTblFileXRef()


}