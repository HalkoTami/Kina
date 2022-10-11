package com.korokoro.kina.ui.view_set_up

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.korokoro.kina.db.dataclass.File

class CommonViewSetUp() {
    fun setUpEachAncestor(linLay: LinearLayoutCompat, txv: TextView, imv: ImageView, file: File?){
        val getDraw =  GetCustomDrawables(linLay.context)
        if(file==null)linLay.visibility = View.GONE
        else {
            linLay.visibility = View.VISIBLE
            txv.text = file.title
            imv.setImageDrawable(getDraw.getFileIconByFile(file))
        }
    }
}