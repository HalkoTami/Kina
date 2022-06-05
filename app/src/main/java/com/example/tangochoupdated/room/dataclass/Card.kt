package com.example.tangochoupdated.room.dataclass

import androidx.room.*
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.CardStatusConverter
import com.example.tangochoupdated.room.enumclass.ColorStatus


@Entity(tableName = "tbl_card",
    foreignKeys = [ForeignKey(entity = File::class,
        parentColumns = arrayOf("file_id"),
        childColumns = arrayOf("belonging_file_id"),
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
    val remembered: Boolean?,
    @ColumnInfo(name= "library_order")
    val libOrder: Int,
    @ColumnInfo(name ="card_color")
    val colorStatus: ColorStatus?,

)




data class StringData(
    @ColumnInfo(name = "string_data")
    val frontTitle:String?,
    val backTitle: String?,
    val frontText:String?,
    val backText:String?,

    )


data class QuizData(
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


@Entity(
    primaryKeys = ["_list_id", "song_id"],
    foreignKeys = [
        ForeignKey(
            entity = File::class,
            parentColumns = arrayOf("file_id"),
            childColumns = arrayOf("belonging_tag_id")
        ),
        ForeignKey(
            entity = Card::class,
            parentColumns = arrayOf("card_id"),
            childColumns = arrayOf("belonging_card_id")
        )
    ]
)
data class CardAndTagXRef(
    @ColumnInfo(name = "belonging_card_id")
    val cardId: Int,
    @ColumnInfo(name = "belonging_tag_id")
    val tagId: Int,
)


class CardAndTags {
    @Embedded
    lateinit var card: Card

    @Relation(
        entity = File::class,
        parentColumn = "card_id",
        entityColumn = "file_id",
        associateBy = Junction(
            value = CardAndTagXRef::class,
            parentColumn = "belonging_card_id",
            entityColumn = "belonging_tag_id"
        )
    )
    lateinit var tags: List<File>
}









