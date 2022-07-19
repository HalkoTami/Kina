package com.example.tangochoupdated.room.dataclass

import androidx.room.*
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.CardStatusConverter
import com.example.tangochoupdated.room.enumclass.ColorStatus


@Entity(tableName = "tbl_card",
//    indices = [Index("id", unique = true),
//              Index("belongingFileId", unique = true, )],
//    foreignKeys = [ForeignKey(entity = File::class,
//        parentColumns = arrayOf("fileId"),
//        childColumns = arrayOf("belongingFileId"),
//        onDelete = ForeignKey.SET_NULL,
//        onUpdate = ForeignKey.CASCADE
//    )]
)
@TypeConverters(CardStatusConverter::class)
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    @ColumnInfo
    val belongingFileId:Int?,

    @Embedded(prefix = "belonging_" )
    var stringData: StringData?,
    @Embedded(prefix = "belonging_")
    val markerData: MarkerPreviewData?,
    @Embedded(prefix = "belonging_")
    val quizData: QuizData?,
    @ColumnInfo(name = "card_type")
    var cardStatus: CardStatus,
    @ColumnInfo(name = "card_deleted")
    var deleted:Boolean?,
    @ColumnInfo(name ="card_remembered")
    val remembered: Boolean?,
    @ColumnInfo(name= "library_order")
    var libOrder: Int,
    @ColumnInfo(name ="card_color")
    val colorStatus: ColorStatus?,

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


class CardAndTags {
    @Embedded
    lateinit var card: Card

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
    lateinit var tags: List<File>
}









