package com.example.tangochoupdated.db.enumclass

import androidx.room.TypeConverter


class ActivityStatusConverter {
    @TypeConverter
    fun toActivityStatus(value: Int): ActivityStatus = enumValues<ActivityStatus>()[value]

    @TypeConverter
    fun fromActivityStatus(value: ActivityStatus): Int = value.ordinal
}
enum class ActivityStatus(value: Int) {
    FILE_CREATED(11),
    FILE_TITLE_EDITED(12),
    FILE_DELETED(13),

    CARD_CREATED(21),
    CARD_EDITED(22),
    CARD_SAFE_DELETED(24),

    CARD_LOOKED(25),
    RIGHT_CHOICE_SELECTED(201),
    WRONG_CHOICE_SELECTED(202),
    RIGHT_BACK_TEXT_TYPED(203),
    WRONG_BACK_TEXT_TYPED(204),

    CARD_REMEMBERED(26),
    CARD_FORGOT(27),

    RIGHT_MARKER_WORD_TYPED(303),
    WRONG_MARKER_WORD_TYPED(304),
    MARKER_WORD_REMEMBERED(36),
    MARKER_WORD_FORGOT(37),

}
