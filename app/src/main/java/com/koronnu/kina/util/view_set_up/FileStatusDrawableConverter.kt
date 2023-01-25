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

class FileStatusDrawableConverter(val context: Context){
    private fun getDrawable(id:Int) = AppCompatResources.getDrawable(context,id)!!
    fun convertColoredFileStatusIcon(file: File?):Drawable{
        return if(file!=null){
            val colorStatus = file.colorStatus
            when(file.fileStatus){
                FileStatus.FOLDER -> getFolderIconByCol(colorStatus)
                FileStatus.FLASHCARD_COVER -> getFlashCardIconByCol(colorStatus)
                FileStatus.ANKI_BOX_FAVOURITE -> getAnkiBoxFavouriteIconByCol(colorStatus)
                FileStatus.TAG -> throw IllegalArgumentException("tag status drawable does not exist yet")
            }
        } else getDrawable(R.drawable.icon_inbox)
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

    private fun getFolderIconByCol(colorStatus: ColorStatus): Drawable {
        val drawable = getDrawable(R.drawable.icon_file_with_color) as LayerDrawable
        drawable.findDrawableByLayerId(R.id.file_paint).setTint(getColorFromColStatus(colorStatus))
        return  drawable

    }
    private fun getFlashCardIconByCol(colorStatus: ColorStatus ): Drawable {
        val drawable = getDrawable(R.drawable.icon_flashcard_with_col) as LayerDrawable
        drawable.findDrawableByLayerId(R.id.icon_flashcard_paint).setTint(getColorFromColStatus(colorStatus))
        return  drawable

    }
    private fun getAnkiBoxFavouriteIconByCol(colorStatus: ColorStatus): Drawable {
        val drawable = getDrawable(R.drawable.icon_heart) as VectorDrawable
        drawable.setTint(getColorFromColStatus(colorStatus))
        return  drawable

    }
}