package com.example.tangochoupdated

import android.app.Application
import androidx.room.RoomDatabase
import com.example.tangochoupdated.MyRoomDatabase.Companion.getDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RoomApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { getDatabase(this,applicationScope) }
    val repository by lazy { MyRoomRepository(database.cardDao()) }
}