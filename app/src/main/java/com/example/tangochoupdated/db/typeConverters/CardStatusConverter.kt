package com.example.tangochoupdated.db.typeConverters

import androidx.room.TypeConverter
import com.example.tangochoupdated.db.enumclass.CardStatus


class CardStatusConverter {
    @TypeConverter
    fun toCardStatus(value: Int): CardStatus = enumValues<CardStatus>() [value]

    @TypeConverter
    fun fromCardStatus(value: CardStatus): Int = value.ordinal
}