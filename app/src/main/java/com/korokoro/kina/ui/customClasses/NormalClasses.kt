package com.korokoro.kina.ui.customClasses

import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.db.enumclass.ColorStatus

class ColorPalletStatus(
    var colNow: ColorStatus,
    var before: ColorStatus?
)
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



class CountFlip(
    var count: Count? = null,
    var countIfLongerThan:Int = 10,
    var flipSaved:Boolean = false,
    var countingCard:Card
)

class AnimationController(
    var attributes: AnimationAttributes? = null

)
class Progress(
    var now:Int,
    var all:Int
)
class MakeToastFromVM(
    var text:String,
    var show:Boolean
)
class AnkiBoxTabData(
    var currentTab: AnkiBoxFragments,
    var before: AnkiBoxFragments?
)
class MySize(
    val width:Int,
    val height:Int
)
class MyPosition(
    val start:Int,
    val top:Int,

    )
class ParentFileAncestors(
    val gGrandPFile: File?,
    val gParentFile: File?,
    val ParentFile: File?,
)