package com.koronnu.kina.data.source.local.typeConverters

import androidx.room.TypeConverter
import com.koronnu.kina.data.source.local.entity.enumclass.CardStatus


class CardStatusConverter {
    @TypeConverter
    fun toCardStatus(value: Int): CardStatus = enumValues<CardStatus>() [value]

    @TypeConverter
    fun fromCardStatus(value: CardStatus): Int = value.ordinal
}