package com.koronnu.kina.data.source.local.entity

import androidx.room.*
import com.koronnu.kina.data.source.local.entity.enumclass.CardStatus
import com.koronnu.kina.data.source.local.entity.enumclass.ColorStatus
import com.koronnu.kina.data.source.local.typeConverters.CardStatusConverter


@Entity(tableName = "tbl_card",
)
@TypeConverters(CardStatusConverter::class)
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    @ColumnInfo
    var belongingFlashCardCoverId:Int? = null,
    var cardStatus: CardStatus = CardStatus.STRING,
    var deleted:Boolean = false,
    var remembered: Boolean = false,
    var flag:Boolean = false,
    var cardBefore: Int? = null,
    var colorStatus: ColorStatus = ColorStatus.GRAY,
    var timesFlipped:Int = 0,
    var lastTypedAnswerCorrect:Boolean? = null,
    @Embedded
    var stringData: StringData? = null,
    @Embedded
    val markerData: MarkerPreviewData? = null,
    @Embedded
    val quizData: QuizData? = null,

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















