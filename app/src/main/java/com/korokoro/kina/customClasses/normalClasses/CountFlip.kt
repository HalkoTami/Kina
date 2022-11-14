package com.korokoro.kina.customClasses.normalClasses

import com.korokoro.kina.customClasses.enumClasses.Count
import com.korokoro.kina.db.dataclass.Card

class CountFlip(
    var count: Count? = null,
    var countIfLongerThan:Int = 10,
    var flipSaved:Boolean = false,
    var countingCard: Card
)