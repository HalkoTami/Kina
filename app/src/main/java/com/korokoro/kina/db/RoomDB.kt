package com.korokoro.kina.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import com.korokoro.kina.db.dao.*
import com.korokoro.kina.db.dataclass.*
import com.korokoro.kina.db.enumclass.*
import com.korokoro.kina.db.typeConverters.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [
    Card::class,
    User::class,
    File::class,
    MarkerData::class,
    Choice::class,
    ActivityData::class,
    XRef::class],
    version = 1, exportSchema = true,
)
@TypeConverters(
    ActivityStatusConverter::class,
    CardStatusConverter::class,
    ColorStatusConverter::class,
    FileStatusConverter::class,
    XRefTypeConverter::class,
    DBTableConverter::class
)
 abstract class MyRoomDatabase : RoomDatabase() {

    @RenameColumn(
        tableName = "tbl_x_ref",
        fromColumnName = "id1_token_tbl",
        toColumnName = "id1_token_x_ref",
    )
    @RenameColumn(
        tableName = "tbl_x_ref",
        fromColumnName = "id2_token_tbl",
        toColumnName = "id2_token_x_ref",
    )
    class MyAutoMigration : AutoMigrationSpec

    abstract fun cardDao(): CardDao
    abstract fun activityDataDao(): ActivityDataDao
    abstract fun choiceDao(): ChoiceDao
    abstract fun fileDao(): FileDao
    abstract fun markerDataDao(): MarkerDataDao
    abstract fun userDao(): UserDao
    abstract fun xRefDao(): XRefDao

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val fileDao = database.fileDao()
                    val cardDao = database.cardDao()
//                    val firstFile = File(
//                        fileId = 1,
//                        title = "フォルダ1",
//                        deleted = false,
//                        colorStatus=  ColorStatus.GRAY,
//                        fileStatus = FileStatus.FOLDER,
//                        fileBefore = null,
//                        parentFileId = null)
//                    val firstChildFile = File(
//                        fileId = 2,
//                        title = "子フォルダ",
//                        deleted = false,
//                        colorStatus=  ColorStatus.RED,
//                        fileStatus = FileStatus.FOLDER,
//                        fileBefore = null,
//                        parentFileId = 1
//                    )
//                    val firstFlashCardWithoutParent = File(
//                    fileId = 3,
//                    fileStatus = FileStatus.FLASHCARD_COVER,
//                    title = "単語帳1",
//                    )
//                    val firstChildFlashCard = File(
//                        fileId = 4,
//                        title = "単語帳2",
//                        deleted = false,
//                        colorStatus=  ColorStatus.BLUE,
//                        fileStatus = FileStatus.FLASHCARD_COVER,
//                        fileBefore = 2,
//                        parentFileId = 1
//                    )
//                    val firstCard = Card(
//                        id = 0,
//                        belongingFlashCardCoverId = 4,
//                        stringData = StringData(null,null,"こんにちは","hello!"),
//                        markerData = null,
//                        cardStatus = CardStatus.STRING,
//                    )
//
//                    fileDao.apply {
//                        insert(firstFile)
//                        insert(firstChildFile)
//                        insert(firstFlashCardWithoutParent)
//                        insert(firstChildFlashCard)
//                    }
//                    cardDao.insert(firstCard)
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
