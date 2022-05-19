package com.example.tangochoupdated.room.dataclass

import androidx.room.*
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.CardStatusConverter


@Entity(tableName = "tbl_card",
    foreignKeys = [ForeignKey(entity = File::class,
        parentColumns = arrayOf("file_id"),
        childColumns = arrayOf("belonging_file_id"),
        onDelete = ForeignKey.CASCADE
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
    val markerData: MarkerData?,
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
    @ColumnInfo
    val answerChoiceId:Int?,
    @ColumnInfo(name= "answer_preview")
    val answerPreview:String?


)
