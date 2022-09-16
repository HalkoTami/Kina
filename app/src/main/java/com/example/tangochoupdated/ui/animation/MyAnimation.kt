package com.example.tangochoupdated.ui.animation

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
fun makeToast(context:Context,string: String){

    Toast.makeText(context,"$string", Toast.LENGTH_SHORT).show()
}

