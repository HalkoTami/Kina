package com.example.tangochoupdated
package com.example.myapplication

import androidx.room.*
import java.time.LocalDateTime

class room {

    @Entity
    data class User(
        @PrimaryKey(autoGenerate = true) val uid: Long,
        @Embedded val file: File?,
        @Embedded val card:Card?,
        @Embedded val choice: Choice?,
        @Embedded val activity:ActivityData?


    )
    @Entity(tableName = "library")
    data class Library(
        @Embedded(prefix = "lib_file") val file: File?,
        @Embedded(prefix = "lib_card") val card:Card?
    )

    interface LibraryCover{
        val type:Int
        val filetitle:String?
        val id: Int
        val containingCards:Int?
        val containingFiles:Int?
        val fronttext:String?
        val backtext:String?
        val wholetext:String?
        val question:String?
        val answer:String?

    }
    @Entity(tableName = "tbl_file")
    @TypeConverters(ColorStatusConverter::class)
    data class File(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "file_id") val fileid:Int,
        @ColumnInfo(name = "file_belonging_file") val belongingFileId: Int,
        @ColumnInfo val  title: String?,
        @ColumnInfo val deleted: Boolean?,
        @ColumnInfo var colorStatus: ColorStatus,
        @Embedded val file:File?,
        @Embedded val card: Card?

    )



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

    @Entity(tableName = "tbl_card")
    @TypeConverters(CardStatusConverter::class)
    data class Card(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name ="card_id") val id:Int,
        @ColumnInfo(name = "card_belonging_file" ) val belongingFileId: Int?,
        @ColumnInfo(name = "card_type") var cardStatus: CardStatus,
        @Embedded val stringData: StringData?,
        @Embedded val markerData: MarkerData?,
        @Embedded val quizData: QuizData?,
        val deleted:Boolean?,
        val remembered: Boolean?

    )

    @Entity(tableName = "string_data")
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
    @Entity(tableName = "marker_data")
    data class MarkerData(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "marker_data_id")
        val id: Int,
        @ColumnInfo(name = "marker_belonging_card_id")
        val cardid: Int,
        @ColumnInfo
        val text:String?,
        val markered:Boolean,
        var markercolor:ColorStatus

    )

    @Entity(tableName = "quiz_data")
    data class QuizData(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "quiz_data_id")
        val id: Int,
        @ColumnInfo(name = "quiz_belonging_card_id")
        val cardid: Int,
        @ColumnInfo
        val question:String,
        @Embedded
        val choice: Choice?,
        @ColumnInfo
        val answerchoiceid:Int?

    )
    @Entity(tableName = "tbl_choice")
    data class Choice(
        @PrimaryKey(autoGenerate = true)
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


    @Dao
    interface LibraryDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insertcard(vararg card: Card)


        @Query("""SELECT * FROM tbl_card a INNER JOIN string_data ON string_data_id = a.string_data_id 
            INNER JOIN marker_data ON marker_data_id = a.marker_data_id
            INNER JOIN quiz_data b ON quiz_data_id = a.quiz_data_id
            INNER JOIN tbl_choice ON choice_id = a.choice_id""",
        )
        fun getcard ():List<Card>

    }
}