package com.example.tangochoupdated.db.enumclass
enum class Mode{
    New, Edit
}
enum class AnkiOrder{
    Library, Random,MostMissed
}
class AnkiFilter(var correctAnswerTyped:Boolean = false,
                 var answerTypedFilterActive:Boolean = false,
                 var remembered:Boolean = false,
                 var rememberedFilterActive:Boolean = false,
                 var flag:Boolean = true,
                 var flagFilterActive :Boolean = false)


class AutoFlip(
    var active:Boolean = false,
    var seconds:Int = 20
)
