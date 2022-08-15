package com.example.tangochoupdated.application

import android.app.Application
import com.example.tangochoupdated.MyRoomDatabase
import com.example.tangochoupdated.db.MyRoomRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class RoomApplication : Application() {
    val myContext: CoroutineContext = SupervisorJob() + Dispatchers.Default
    val myScope = CoroutineScope(myContext + CoroutineName("my name"))

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    // ]
    val database by lazy { MyRoomDatabase.getDatabase(this, myScope) }
    val repository by lazy { MyRoomRepository(
        cardDao = database.cardDao(),
        activityDataDao= database.activityDataDao(),
        database.choiceDao(),
        database.fileDao(),
        database.markerDataDao(),
        database.userDao(),
        database.clearTable(),
        database.libraryDao(),
        database.cardAndTagXRefDao(),
        database.fileXRefDao()
    ) }
}