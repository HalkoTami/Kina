package com.example.tangochoupdated.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tangochoupdated.db.dao.*
import com.example.tangochoupdated.db.dataclass.*
import com.example.tangochoupdated.db.dataclass.ChoiceDao
import com.example.tangochoupdated.db.enumclass.*
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



    abstract fun cardDao(): CardDao
    abstract fun activityDataDao(): ActivityDataDao
    abstract fun choiceDao(): ChoiceDao
    abstract fun fileDao(): FileDao
    abstract fun markerDataDao(): MarkerDataDao
    abstract fun userDao(): UserDao
    abstract fun cardAndTagXRefDao(): CardAndTagXRefDao
    abstract fun fileXRefDao(): FileXRefDao

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var fileDao = database.fileDao()


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
                        childData = ChildData(),
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
