package com.example.tangochoupdated.db.dataclass

import androidx.room.*
import com.example.tangochoupdated.db.enumclass.CardStatus
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.db.typeConverters.CardStatusConverter


@Entity(tableName = "tbl_card",
)
@TypeConverters(CardStatusConverter::class)
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    @ColumnInfo
    var belongingFlashCardCoverId:Int? = null,

    @Embedded
    var stringData: StringData? = null,
    @Embedded
    val markerData: MarkerPreviewData? = null,
    @Embedded
    val quizData: QuizData? = null,
    @ColumnInfo
    var cardStatus: CardStatus = CardStatus.STRING,
    @ColumnInfo
    var deleted:Boolean = false,
    var remembered: Boolean = false,
    var flag:Boolean = false,
    @ColumnInfo
    var libOrder: Int = 0,
    var colorStatus: ColorStatus = ColorStatus.GRAY,
    var timesFlipped:Int = 0,
    var lastTypedAnswerCorrect:Boolean? = null
    )


//Todo val と var の正しい振り分け


data class StringData(
    var frontTitle:String? = null,
    var backTitle: String? = null,
    var frontText:String? = null,
    var backText:String? =null,

    )


data class QuizData(
    val answerChoiceId:Int?,
    val answerPreview:String?,
    val question:String,

)

data class MarkerPreviewData(
    val markerTextPreview:String?
)




//class CardAndTags (
//    @Embedded
//    val card: Card,
//
//    @Embedded
//    val tags: List<File>
//)















