package com.korokoro.kina.db.typeConverters

import androidx.room.TypeConverter
import com.korokoro.kina.db.enumclass.CardStatus


class CardStatusConverter {
    @TypeConverter
    fun toCardStatus(value: Int): CardStatus = enumValues<CardStatus>() [value]

    @TypeConverter
    fun fromCardStatus(value: CardStatus): Int = value.ordinal
}