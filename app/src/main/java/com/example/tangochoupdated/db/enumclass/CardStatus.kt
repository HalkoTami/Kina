package com.example.tangochoupdated.db.enumclass

import androidx.room.TypeConverter

enum class CardStatus(value: Int) {
    STRING(0),
    MARKER(1),
    CHOICE(2)
}


class CardStatusConverter {
    @TypeConverter
    fun toCardStatus(value: Int): CardStatus = enumValues<CardStatus>()[value]

    @TypeConverter
    fun fromCardStatus(value: CardStatus): Int = value.ordinal
}