package com.example.tangochoupdated.ui.view_set_up

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.example.tangochoupdated.R
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.db.enumclass.FileStatus

class GetCustomDrawables(val context: Context){
    fun appCon(id:Int):Drawable{
        return AppCompatResources.getDrawable(context,id)!!
    }
    fun getFileIconByFile(file: File):Drawable{
        return when(file.fileStatus){
            FileStatus.FOLDER -> getFolderIconByCol(file.colorStatus)
            FileStatus.TANGO_CHO_COVER -> getFlashCardIconByCol(file.colorStatus)
            else -> appCon(R.drawable.icon_eye_opened)
        }
    }

    fun getFolderIconByCol(colorStatus: ColorStatus,): Drawable {
        val drawable = appCon(R.drawable.icon_file_with_color) as LayerDrawable
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
    fun getFlashCardIconByCol(colorStatus: ColorStatus, ): Drawable {
        val drawable = appCon(R.drawable.icon_flashcard_with_col) as LayerDrawable
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