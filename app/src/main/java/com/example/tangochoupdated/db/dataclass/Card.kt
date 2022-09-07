package com.example.tangochoupdated.db.dataclass

import androidx.room.*
import com.example.tangochoupdated.db.enumclass.CardStatus
import com.example.tangochoupdated.db.enumclass.CardStatusConverter
import com.example.tangochoupdated.db.enumclass.ColorStatus


@Entity(tableName = "tbl_card",
)
@TypeConverters(CardStatusConverter::class)
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    @ColumnInfo
    var belongingFlashCardCoverId:Int? = null,

    @Embedded
    var stringData: StringData?,
    @Embedded
    val markerData: MarkerPreviewData?,
    @Embedded
    val quizData: QuizData?,
    @ColumnInfo
    var cardStatus: CardStatus,
    @ColumnInfo
    var deleted:Boolean = false,
    var remembered: Boolean = false,
    var flag:Boolean = false,
    @ColumnInfo
    var libOrder: Int = 0,
    var colorStatus: ColorStatus = ColorStatus.GRAY,
    var timesFlipped:Int = 0
    )


//Todo val と var の正しい振り分け


data class StringData(
    var frontTitle:String?,
    var backTitle: String?,
    var frontText:String?,
    var backText:String?,

    )


data class QuizData(
    val answerChoiceId:Int?,
    val answerPreview:String?,
    val question:String,

)

data class MarkerPreviewData(
    val markerTextPreview:String?
)


@Entity(tableName = "tbl_card_file_x_ref"
)
data class CardAndTagXRef(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var cardFileXRefCardId: Int,
    var cardFileXRefFileId: Int,
)


//class CardAndTags (
//    @Embedded
//    val card: Card,
//
//    @Embedded
//    val tags: List<File>
//)















