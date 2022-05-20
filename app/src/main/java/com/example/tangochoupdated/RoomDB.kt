package com.example.tangochoupdated

import android.content.Context
import android.service.autofill.UserData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tangochoupdated.room.DataAccessObject
import com.example.tangochoupdated.room.dataclass.*
import com.example.tangochoupdated.room.enumclass.ColorStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [
    Card::class, User::class,
    File::class, MarkerData::class, Choice::class, ActivityData::class],
    version = 1, exportSchema = false)
public abstract class MyRoomDatabase : RoomDatabase() {

    abstract fun cardDao(): CardDao

    abstract fun fileDao(): FileDao

    abstract fun userDao(): UserDao

    abstract fun markerDao(): MarkerDataDao

    abstract fun activityDao(): ActivityDataDao

    abstract fun choiceDao():ChoiceDao

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var cardDao = database.cardDao()
                    var fileDao = database.fileDao()
                    var userDao = database.userDao()
                    var markerDao = database.markerDao()
                    var activityDao = database.activityDao()

                    // Delete all content here.
                    cardDao.clearTblCard()
                    fileDao.clearTblFile()
                    userDao.clearTblUser()
                    markerDao.clearTblMarkerData()
                    activityDao.clearTblActivity()

                    // Add sample words.
                    var file = File(0,null,"タイトルなし",false, ColorStatus.RED,)
                    fileDao.insert(file)

                }
            }
        }
    }
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: MyRoomDatabase? = null

        fun getDatabase(
                        context: Context,
                        scope: CoroutineScope
        ): MyRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyRoomDatabase::class.java,
                    "my_database"
                ).addCallback(WordDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}