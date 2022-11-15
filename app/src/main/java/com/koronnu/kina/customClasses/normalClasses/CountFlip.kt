package com.koronnu.kina.customClasses.normalClasses

import com.koronnu.kina.customClasses.enumClasses.Count
import com.koronnu.kina.db.dataclass.Card

class CountFlip(
    var count: Count? = null,
    var countIfLongerThan:Int = 10,
    var flipSaved:Boolean = false,
    var countingCard: Card
)