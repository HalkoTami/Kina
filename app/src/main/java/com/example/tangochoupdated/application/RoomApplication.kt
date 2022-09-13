package com.example.tangochoupdated.application

import android.app.Application
import com.example.tangochoupdated.db.MyRoomDatabase
import com.example.tangochoupdated.db.MyRoomRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class RoomApplication : Application() {
    private val myContext: CoroutineContext = SupervisorJob() + Dispatchers.Default
    private val myScope = CoroutineScope(myContext + CoroutineName("my name"))
    private val database by lazy { MyRoomDatabase.getDatabase(this, myScope) }
    val repository by lazy { MyRoomRepository(
        cardDao = database.cardDao(),
        activityDataDao= database.activityDataDao(),
        database.choiceDao(),
        database.fileDao(),
        database.markerDataDao(),
        database.userDao(),
        database.cardAndTagXRefDao(),
        database.fileXRefDao()
    ) }
}