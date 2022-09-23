package com.korokoro.kina.ui.view_set_up

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.VectorDrawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.korokoro.kina.R
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.db.enumclass.ColorStatus
import com.korokoro.kina.db.enumclass.FileStatus

class
GetCustomDrawables(val context: Context){
    fun getDrawable(id:Int):Drawable{
        return AppCompatResources.getDrawable(context,id)!!
    }
    fun getFileIconByFile(file: File):Drawable{
        return when(file.fileStatus){
            FileStatus.FOLDER -> getFolderIconByCol(file.colorStatus)
            FileStatus.FLASHCARD_COVER -> getFlashCardIconByCol(file.colorStatus)
            FileStatus.ANKI_BOX_FAVOURITE -> getAnkiBoxFavouriteIconByCol(file.colorStatus)
            else -> getDrawable(R.drawable.icon_eye_opened)
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