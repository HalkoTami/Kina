package com.koronnu.kina.data.model.normalClasses

import com.koronnu.kina.data.model.enumClasses.Count
import com.koronnu.kina.data.source.local.entity.Card

class CountFlip(
    var count: Count? = null,
    var countIfLongerThan:Int = 10,
    var flipSaved:Boolean = false,
    var countingCard: Card
)