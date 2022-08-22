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

    @Embedded(prefix = "belonging_" )
    var stringData: StringData?,
    @Embedded(prefix = "belonging_")
    val markerData: MarkerPreviewData?,
    @Embedded(prefix = "belonging_")
    val quizData: QuizData?,
    @ColumnInfo(name = "card_type")
    var cardStatus: CardStatus,
    @ColumnInfo(name = "card_deleted")
    var deleted:Boolean = false,
    var remembered: Boolean = false,
    @ColumnInfo(name= "library_order")
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


@Entity(
    indices = [Index("cardId", unique = true),
              Index("tagId", unique = true)],

    foreignKeys = [
        ForeignKey(
            entity = File::class,
            parentColumns = arrayOf("fileId"),
            childColumns = arrayOf("tagId")
        ),
        ForeignKey(
            entity = Card::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("cardId")
        )
    ]
)
data class CardAndTagXRef(
    @PrimaryKey
    val cardId: Int,
    val tagId: Int,
)


class CardAndTags (
    @Embedded
    val card: Card,

    @Relation(
        entity = File::class,
        parentColumn = "id",
        entityColumn = "fileId",
        associateBy = Junction(
            value = CardAndTagXRef::class,
            parentColumn = "cardId",
            entityColumn = "tagId"
        )
    )
    val tags: List<File>
)















