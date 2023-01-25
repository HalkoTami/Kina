package com.koronnu.kina.util.view_set_up

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.VectorDrawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.koronnu.kina.R
import com.koronnu.kina.data.source.local.entity.File
import com.koronnu.kina.data.source.local.entity.enumclass.ColorStatus
import com.koronnu.kina.data.source.local.entity.enumclass.FileStatus

class DrawableConverter(val context: Context){
    fun getDrawable(id:Int):Drawable{
        return AppCompatResources.getDrawable(context,id)!!
    }
    fun convertColoredFileStatusIcon(file: File):Drawable{
        val colorStatus = file.colorStatus
        return when(file.fileStatus){
            FileStatus.FOLDER -> getFolderIconByCol(colorStatus)
            FileStatus.FLASHCARD_COVER -> getFlashCardIconByCol(colorStatus)
            FileStatus.ANKI_BOX_FAVOURITE -> getAnkiBoxFavouriteIconByCol(colorStatus)
            else -> getDrawable(R.drawable.imv_character)
        }
    }
    private fun getColorFromColStatus(colorStatus: ColorStatus): Int {
        val colResId = when(colorStatus) {
            ColorStatus.GRAY -> R.color.gray
            ColorStatus.BLUE -> R.color.blue
            ColorStatus.YELLOW -> R.color.yellow
            ColorStatus.RED -> R.color.red
        }
        return ContextCompat.getColor(context,colResId)
    }

    fun getFolderIconByCol(colorStatus: ColorStatus,): Drawable {
        val drawable = getDrawable(R.drawable.icon_file_with_color) as LayerDrawable
        drawable.findDrawableByLayerId(R.id.file_paint).setTint(getColorFromColStatus(colorStatus))
        return  drawable

    }
    fun getFlashCardIconByCol(colorStatus: ColorStatus, ): Drawable {
        val drawable = getDrawable(R.drawable.icon_flashcard_with_col) as LayerDrawable
        drawable.findDrawableByLayerId(R.id.icon_flashcard_paint).setTint(getColorFromColStatus(colorStatus))
        return  drawable

    }
    fun getAnkiBoxFavouriteIconByCol(colorStatus: ColorStatus,): Drawable {
        val drawable = getDrawable(R.drawable.icon_heart) as VectorDrawable
        drawable.setTint(getColorFromColStatus(colorStatus))
        return  drawable

    }
}