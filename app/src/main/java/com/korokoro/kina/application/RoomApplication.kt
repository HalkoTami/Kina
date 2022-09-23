package com.korokoro.kina.application

import android.app.Application
import com.korokoro.kina.db.MyRoomDatabase
import com.korokoro.kina.db.MyRoomRepository
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

class RoomApplication : Application() {
    private val myContext: CoroutineContext = SupervisorJob() + Dispatchers.Default
    private val myScope = CoroutineScope(myContext + CoroutineName("my name"))
    private val database by lazy { MyRoomDatabase.getDatabase(this, myScope) }
    val repository by lazy {
        MyRoomRepository(
            cardDao = database.cardDao(),
            activityDataDao = database.activityDataDao(),
            database.choiceDao(),
            database.fileDao(),
            database.markerDataDao(),
            database.userDao(),
            database.xRefDao(),
        )
    }
}