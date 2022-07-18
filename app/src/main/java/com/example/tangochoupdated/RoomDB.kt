package com.example.tangochoupdated

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tangochoupdated.room.BaseDao
import com.example.tangochoupdated.room.MyDao
import com.example.tangochoupdated.room.dataclass.*
import com.example.tangochoupdated.room.enumclass.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [
    Card::class,
    User::class,
    File::class,
    MarkerData::class,
    Choice::class,
    ActivityData::class,
    CardAndTagXRef::class,
    FileXRef::class],
    version = 1, exportSchema = true)
@TypeConverters(
    ActivityStatusConverter::class,
    CardStatusConverter::class,
    ColorStatusConverter::class,
    FileStatusConverter::class,
)
public abstract class MyRoomDatabase : RoomDatabase() {



    abstract fun cardDao(): MyDao.CardDao
    abstract fun activityDataDao(): MyDao.ActivityDataDao
    abstract fun choiceDao(): MyDao.ChoiceDao
    abstract fun fileDao(): MyDao.FileDao
    abstract fun markerDataDao(): MyDao.MarkerDataDao
    abstract fun userDao(): MyDao.UserDao
    abstract fun clearTable(): MyDao.ClearTable
    abstract fun libraryDao(): MyDao.LibraryDao
    abstract fun cardAndTagXRefDao(): MyDao.CardAndTagXRefDao
    abstract fun fileXRefDao(): MyDao.FileXRefDao

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var fileDao = database.fileDao()
                    var clearTable = database.clearTable()

                    // Delete all content here.
                    clearTable.clearTblCard()
                    clearTable.clearTblActivity()
                    clearTable.clearTblChoice()
                    clearTable.clearTblUser()
                    clearTable.clearTblFile()
                    clearTable.clearTblMarkerData()
                    clearTable.clearTblFileXRef()


                    // Add sample words.
                    var file = File(
                        fileId = 0,
                        title = "タイトルなし",
                        deleted = false,
                        colorStatus=  ColorStatus.RED,
                        fileStatus = FileStatus.FOLDER,
                        hasChild = false,
                        hasParent = false,
                        libOrder = 0,
                        childFoldersAmount = 0,
                        childCardsAmount = 0,
                        childFlashCardCoversAmount = 0,
                        parentFileId = null)
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
