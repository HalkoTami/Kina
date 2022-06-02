package com.example.tangochoupdated.room.enumclass

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter

enum class CardStatus(value: Int) {
    STRING(0),
    MARKER(1),
    CHOICE(2)
}

@ProvidedTypeConverter
class CardStatusConverter {
    @TypeConverter
    fun toCardStatus(value: Int): CardStatus = enumValues<CardStatus>()[value]

    @TypeConverter
    fun fromCardStatus(value: CardStatus): Int = value.ordinal
}