package com.example.tangochoupdated.room.dataclass

import androidx.room.*
import com.example.tangochoupdated.room.DataAccessObject
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.CardStatusConverter
import kotlinx.coroutines.flow.Flow


@Entity(tableName = "tbl_card",
    foreignKeys = [ForeignKey(entity = File::class,
        parentColumns = arrayOf("file_id"),
        childColumns = arrayOf("belonging_file_id"),
        onDelete = ForeignKey.SET_NULL,
        onUpdate = ForeignKey.CASCADE
    ),ForeignKey(entity = Choice::class,
        parentColumns = arrayOf("choice_id"),
        childColumns = arrayOf("belonging_choice_id_one",
            "belonging_choice_id_two",
            "belonging_choice_id_three",
            "belonging_choice_id_four"),
        onDelete = ForeignKey.SET_NULL,
        onUpdate = ForeignKey.CASCADE
    )]
)
@TypeConverters(CardStatusConverter::class)
data class Card(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name ="card_id") val id:Int,
    @ColumnInfo(name="belonging_file_id")
    val belongingFileId:Int,
    @Embedded(prefix = "belonging_" )
    val stringData: StringData?,
    @Embedded(prefix = "belonging_")
    val markerData: MarkerPreviewData?,
    @Embedded(prefix = "belonging_")
    val quizData: QuizData?,
    @ColumnInfo(name = "card_type")
    var cardStatus: CardStatus,
    @ColumnInfo(name = "card_deleted")
    val deleted:Boolean?,
    @ColumnInfo(name ="card_remembered")
    val remembered: Boolean?

)




data class StringData(
    @ColumnInfo(name = "string_data")
    val frontTitle:String?,
    val backTitle: String?,
    val frontText:String?,
    val backText:String?,

    )


data class QuizData(
    @ColumnInfo(name = "choice_id_one")
    val choiceIdOne:Int?,
    @ColumnInfo(name = "choice_id_two")
    val choiceIdTwo:Int?,
    @ColumnInfo(name = "choice_id_three")
    val choiceIdThree:Int?,
    @ColumnInfo(name = "choice_id_four")
    val choiceIdFour:Int?,
    @ColumnInfo
    val answerChoiceId:Int?,
    @ColumnInfo(name= "quiz_cover_preview")
    val answerPreview:String?,
    val question:String,

)

data class MarkerPreviewData(
    @ColumnInfo(name ="marker_text_preview")
    val markerTextPreview:String?
)

@Dao abstract class CardDao: DataAccessObject<Card>{
    @Query("DELETE FROM tbl_card")
    abstract suspend fun clearTblCard()


    @Query("select * from tbl_card where NOT card_deleted AND belonging_file_id = :belongingFileId ")
    abstract fun getCardsByFileId(belongingFileId: Int ): Flow<List<Card>>

    @Query("select * from tbl_card where NOT card_deleted AND belonging_string_data OR " +
            "belonging_marker_text_preview OR belonging_quiz_cover_preview LIKE '%' || :search || '%' " +
            "ORDER BY card_id DESC")
    abstract fun searchCardsByWords(search:String):Flow<List<Card>>

}













