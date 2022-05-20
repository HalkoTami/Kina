package com.example.tangochoupdated

import androidx.annotation.WorkerThread
import com.example.tangochoupdated.room.dataclass.*
import kotlinx.coroutines.flow.Flow

/// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class MyRoomRepository(private val cardDao: CardDao,
                       private val fileDao: FileDao,
                       private val userDao: UserDao,
                       private val markerDao: MarkerDataDao,
                       private val activityDao: ActivityDataDao,
                       private val choiceDao: ChoiceDao) {



//TODO each tableね

    val fileWithNoParents: Flow<List<File>> = fileDao.getFileWithoutParent()




    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertCard(card:Card) {
        cardDao.insert(card)
    }
    suspend fun updateCard(card:Card){
        cardDao.update(card)

    }


//

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
//    @Suppress("RedundantSuspendModifier")
//    @WorkerThread
//
//
//    suspend fun insertCard(card:Card) {
//        cardDao.insert(card)
//    }
//    suspend fun updateCard(card:Card){
//        cardDao.update(card)
//
//    }

}