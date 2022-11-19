package com.koronnu.kina.customClasses.normalClasses

import com.koronnu.kina.customClasses.enumClasses.BorderAttributes
import com.koronnu.kina.customClasses.enumClasses.MyHorizontalOrientation
import com.koronnu.kina.customClasses.enumClasses.MyVerticalOrientation

class MyOrientationSet(
    var verticalOrientation: MyVerticalOrientation = MyVerticalOrientation.MIDDLE,
    var horizontalOrientation: MyHorizontalOrientation = MyHorizontalOrientation.MIDDLE,
    var borderAttributes: BorderAttributes = BorderAttributes.FillIfOutOfBorder

)