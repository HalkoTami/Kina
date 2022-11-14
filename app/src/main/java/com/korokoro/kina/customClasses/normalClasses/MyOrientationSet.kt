package com.korokoro.kina.customClasses.normalClasses

import com.korokoro.kina.customClasses.enumClasses.BorderAttributes
import com.korokoro.kina.customClasses.enumClasses.MyHorizontalOrientation
import com.korokoro.kina.customClasses.enumClasses.MyVerticalOrientation

class MyOrientationSet(
    var verticalOrientation: MyVerticalOrientation = MyVerticalOrientation.MIDDLE,
    var horizontalOrientation: MyHorizontalOrientation = MyHorizontalOrientation.MIDDLE,
    var borderAttributes: BorderAttributes = BorderAttributes.FillIfOutOfBorder

)