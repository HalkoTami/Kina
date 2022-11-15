package com.koronnu.kina.ui.view_set_up

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.koronnu.kina.R
import com.koronnu.kina.databinding.ItemColorPalletBinding
import com.koronnu.kina.db.enumclass.ColorStatus

class ColorPalletViewSetUp {
    fun changeColPalletCol(context: Context, colorStatus: ColorStatus?, selected:Boolean?, colorPalletBinding: ItemColorPalletBinding){
        val imageView: ImageView
        val colId:Int
        val color:Int
        colorPalletBinding.apply {
            when(colorStatus) {
                ColorStatus.GRAY -> {
                    colId = R.color.gray
                    imageView = this.imvColGray
                }
                ColorStatus.BLUE -> {
                    colId = R.color.blue
                    imageView = this.imvColBlue
                }
                ColorStatus.YELLOW -> {
                    colId = R.color.yellow
                    imageView = this.imvColYellow
                }
                ColorStatus.RED -> {
                    colId =  R.color.red
                    imageView = this.imvColRed
                }
                else -> return
            }
            color = ContextCompat.getColor(context,colId)
        }
        val a = imageView.drawable as GradientDrawable
        a.mutate()

        when(selected){
            true -> {
                a.setStroke(5, ContextCompat.getColor(context, R.color.black))
                a.setColor(color)
                imageView.alpha = 1f
//                imageView.background = a
                imageView.elevation = 10f
            }
            false -> {
                a.setStroke(5, ContextCompat.getColor(context, R.color.of_white))
                a.setColor(color)
                imageView.alpha = 0.4f
//                imageView.elevation = 0f
            }
            else -> return
        }
        imageView.setImageDrawable(a)

    }
    fun makeAllColPalletUnselected(context: Context, colorPalletBinding: ItemColorPalletBinding){
        changeColPalletCol(context, ColorStatus.RED,false,colorPalletBinding)
        changeColPalletCol(context, ColorStatus.YELLOW,false,colorPalletBinding)
        changeColPalletCol(context, ColorStatus.BLUE,false,colorPalletBinding)
        changeColPalletCol(context, ColorStatus.GRAY,false,colorPalletBinding)
    }
}