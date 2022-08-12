package com.example.tangochoupdated

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import com.example.tangochoupdated.db.enumclass.ColorStatus

class GetCustomDrawables{
    fun getFileIconByCol(colorStatus: ColorStatus, context: Context): Drawable {
        val drawable = AppCompatResources.getDrawable(context,R.drawable.icon_file_with_color) as LayerDrawable
        val col = ContextCompat.getColor(context,
            when(colorStatus) {
                ColorStatus.GRAY -> R.color.gray
                ColorStatus.BLUE -> R.color.blue
                ColorStatus.YELLOW -> R.color.yellow
                ColorStatus.RED -> R.color.red
                else -> R.color.gray
            }
        )
        drawable.findDrawableByLayerId(R.id.file_paint).setTint(col)
        return  drawable

    }
    fun getFlashCardIconByCol(colorStatus: ColorStatus, context: Context): Drawable {
        val drawable = AppCompatResources.getDrawable(context,R.drawable.icon_flashcard_with_col) as LayerDrawable
        val col = ContextCompat.getColor(context,
            when(colorStatus) {
                ColorStatus.GRAY -> R.color.gray
                ColorStatus.BLUE -> R.color.blue
                ColorStatus.YELLOW -> R.color.yellow
                ColorStatus.RED -> R.color.red
                else -> R.color.gray
            }
        )
        drawable.findDrawableByLayerId(R.id.icon_flashcard_paint).setTint(col)
        return  drawable

    }
}