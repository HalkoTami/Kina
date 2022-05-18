package com.example.tangochoupdated


import android.service.autofill.UserData
import androidx.room.*
import java.time.LocalDateTime
import java.util.concurrent.Flow


    @Entity
    data class User(
        @PrimaryKey(autoGenerate = true) val uid: Long,



    )
class UserAndAllData{
    @Embedded
    lateinit var user: User

    @Relation(parentColumn = "uid", entityColumn = "userid")
    lateinit var files: List<File>
}



    @Entity(tableName = "tbl_file",
        foreignKeys = arrayOf(ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("uid"),
            childColumns = arrayOf("user_id"),
            onDelete = ForeignKey.CASCADE
        ))
    )
    @TypeConverters(ColorStatusConverter::class)
    data class File(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "file_id") val fileid:Int,
        @ColumnInfo(name = "user_id") val userid:Int,
        @Embedded(prefix = "belonging",)
        val belongingFile: File?,
        @ColumnInfo(defaultValue = "title") val  title: String?,
        @ColumnInfo val deleted: Boolean?,
        @ColumnInfo var colorStatus: ColorStatus?,


    )
@Dao
interface MyDao{
    @Transaction
    @Query("select * from user")
    fun loadAllData(): List<UserAndAllData>

    @Insert
    fun insertFile(vararg files: File)

    @Insert
    fun insertCard(vararg cards: Card)

    @Delete
    fun deleteFile(vararg files: File)

    @Query("DELETE FROM word_table")
    suspend fun deleteAll()

    @Transaction
    @Query("select * from file")
    fun loadFileAndCard(): List<FileAndCard>

    @Transaction
    @Query("select * from card")
    fun loadCardsandData(): List<CardAndData>
}
class FileAndCard{
    @Embedded
    lateinit var file: File

    @Relation(parentColumn = "file_id",
        entityColumn = "belonging_file_id")
    lateinit var cards: List<CardAndData>
}

class CardAndData{
    @Embedded
    lateinit var card: Card

    @Relation(parentColumn = "card_id",
        entityColumn = "marker_data_belonging_card_id")
    lateinit var markerwords: List<MarkerData>

    @Relation(parentColumn = "card_id",
        entityColumn = "choice_belonging_card_id")
    lateinit var choices: List<Choice>


}


    enum class ColorStatus(value: Int) {
        GRAY(0),
        RED(1),
        BLUE(2),
        YELLOW(3)
    }
    @ProvidedTypeConverter
    class ColorStatusConverter {
        @TypeConverter
        fun toColorStatus(value: Int): ColorStatus = enumValues<ColorStatus>()[value]

        @TypeConverter
        fun fromColorStatus(value: ColorStatus): Int = value.ordinal
    }
    enum class CardStatus(value: Int) {
        STRING(0),
        MARKER(1),
        QUIZ(2)
    }

    @ProvidedTypeConverter
    class CardStatusConverter {
        @TypeConverter
        fun toCardStatus(value: Int): CardStatus = enumValues<CardStatus>()[value]

        @TypeConverter
        fun fromCardStatus(value: CardStatus): Int = value.ordinal
    }

    @ProvidedTypeConverter
    class ActyvityStatusConverter {
        @TypeConverter
        fun toActivityStatus(value: Int): ActivityStatus = enumValues<ActivityStatus>()[value]

        @TypeConverter
        fun fromActivityStatus(value: ActivityStatus): Int = value.ordinal
    }
    enum class ActivityStatus(value: Int) {
        CREATED(0),
        EDITED(1),
        DELETED(2),
        LOOKED(3),
        RIGHTANSWERSELECTED(4),
        WRONGANSWERSELECTED(5),
        REMEMBERED(6),
        FORGOT(7)
    }

    @Entity(tableName = "tbl_card",
        foreignKeys = arrayOf(ForeignKey(entity = File::class,
            parentColumns = arrayOf("file_id"),
            childColumns = arrayOf("belonging_file_id"),
            onDelete = ForeignKey.CASCADE
        ))
    )
    @TypeConverters(CardStatusConverter::class)
    data class Card(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name ="card_id") val id:Int,
        @ColumnInfo(name="belonging_file_id")
        val belongingFileId:Int,
        @Embedded(prefix = "belonging" )
        val stringData: StringData?,
        val markerData: MarkerData?,
        val quizData: QuizData?,
        @ColumnInfo(name = "card_type") var cardStatus: CardStatus,
        val deleted:Boolean?,
        val remembered: Boolean?

    )




    data class StringData(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "string_data_id")
        val id: Int,
        @ColumnInfo(name = "string_belonging_card_id")
        val cardid: Int,
        @ColumnInfo
        val fronttext:String?,
        val backtext:String?,

        )
    @Entity(tableName = "marker_data",
    foreignKeys = arrayOf(ForeignKey(entity = Card::class,
    parentColumns = arrayOf("card_id"),
    childColumns = arrayOf("marker_data_belonging_card_id"),
    onDelete = ForeignKey.CASCADE)))
    data class MarkerData(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "marker_data_id")
        val id: Int,
        @ColumnInfo(name = "marker_data_belonging_card_id")
        val cardid: Int,
        @ColumnInfo
        val text:String?,
        val markered:Boolean,
        var markercolor:ColorStatus

    )


    data class QuizData(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "quiz_data_id")
        val id: Int,
        @ColumnInfo(name = "quiz_belonging_card_id")
        val cardid: Int,
        @ColumnInfo
        val question:String,
        @ColumnInfo
        val answerchoiceid:Int?

    )
    @Entity(tableName = "tbl_choice",
    foreignKeys = arrayOf(ForeignKey(
        entity = Card::class,
        parentColumns = arrayOf("card_id"),
        childColumns = arrayOf("choice_belonging_card_id"),
        onDelete = ForeignKey.CASCADE
    )))
    data class Choice(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "choice_belonging_card_id")
        val belongingcardId: Int?,
        @ColumnInfo(name = "choice_id")
        val id: Int,
        @ColumnInfo
        val text:String?,

        )
    @Entity(tableName = "tbl_ankidata")
    data class ActivityData(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        @ColumnInfo(name = "belongingcard_id")
        val belongingcardId:Int?,
        @ColumnInfo(name = "belonging_markerdata_id")
        val belongingMarkerDataId:Int?,
        @ColumnInfo
        var activityStatus: ActivityStatus,
        val dateTime: LocalDateTime
    )



