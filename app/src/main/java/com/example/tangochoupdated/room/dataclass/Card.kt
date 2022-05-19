package com.example.tangochoupdated.room.dataclass

import androidx.room.*
import com.example.tangochoupdated.room.DataAccessObject
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.CardStatusConverter


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
    @Embedded(prefix = "belonging" )
    val stringData: StringData?,
    val markerData: MarkerPreviewData?,
    val quizData: QuizData?,
    @ColumnInfo(name = "card_type")
    var cardStatus: CardStatus,
    @ColumnInfo(name = "card_deleted")
    val deleted:Boolean?,
    @ColumnInfo(name ="card_remembered")
    val remembered: Boolean?

)




data class StringData(
    @ColumnInfo
    val frontTitle:String?,
    val backTitle: String?,
    val frontText:String?,
    val backText:String?,

    )


data class QuizData(
    @ColumnInfo
    val question:String,
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
    @ColumnInfo(name= "answer_preview")
    val answerPreview:String?


)

data class MarkerPreviewData(
    @ColumnInfo(name ="marker_text_preview")
    val markerTextPreview:String?
)

@Dao abstract class CardDao: DataAccessObject<Card>
