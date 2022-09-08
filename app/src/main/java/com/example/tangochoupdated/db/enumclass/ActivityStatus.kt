package com.example.tangochoupdated.db.enumclass

import androidx.room.TypeConverter


class ActivityStatusConverter {
    @TypeConverter
    fun toActivityStatus(value: Int): ActivityStatus = enumValues<ActivityStatus>()[value]

    @TypeConverter
    fun fromActivityStatus(value: ActivityStatus): Int = value.ordinal
}
enum class ActivityStatus(value: Int) {
    FILE_CREATED(0),
    FILE_TITLE_EDITED(1),
    FILE_DELETED(2),

    CARD_CREATED(3),
    CARD_EDITED(4),
    CARD_SAFE_DELETED(5),

    CARD_LOOKED(6),
    RIGHT_CHOICE_SELECTED(7),
    WRONG_CHOICE_SELECTED(8),
    RIGHT_FRONT_CONTENT_TYPED(9),
    WRONG_FRONT_CONTENT_TYPED(10),
    RIGHT_BACK_CONTENT_TYPED(11),
    WRONG_BACK_CONTENT_TYPED(12),

    CARD_REMEMBERED(13),
    CARD_FORGOT(14),

    RIGHT_MARKER_WORD_TYPED(15),
    WRONG_MARKER_WORD_TYPED(16),
    MARKER_WORD_REMEMBERED(17),
    MARKER_WORD_FORGOT(18),

}
