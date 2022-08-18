package com.example.tangochoupdated.ui.view_set_up

import android.app.ActionBar
import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ScaleDrawable
import android.graphics.drawable.ShapeDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.WrappedDrawable
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.AnkiBoxTab
import com.example.tangochoupdated.ui.fragment.anki_frag_con.AnkiFragmentAnkiBox
import com.example.tangochoupdated.ui.listadapter.AnkiBoxListAdapter
import com.example.tangochoupdated.ui.listener.AnkiBoxFragBaseCL
import com.example.tangochoupdated.ui.listener.menuBar.AnkiBoxTabChangeCL
import com.example.tangochoupdated.ui.listener.recyclerview.AnkiBoxFileRVCL
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel
import kotlin.math.floor

class AnkiBaseFragViewSetUp(val ankiBoxVM:AnkiBoxFragViewModel,
                            val context: Context,
                            val bindingBase:AnkiFragBaseBinding) {


    fun ankiSettingPopUpAddCL(){
        bindingBase.bindingSetting.apply {
            val content = bindingSettingContent
            bindingSettingContent.apply {
                arrayOf(
                    lineLayParentOrder,
                    txvFilterSetting,imvFilterOptionVisibility,
                    txvFilterTypedAnswerCorrect,
                    txvFilterTypedAnswerMissed,
                    txvFilterCardRemembered,
                    txvFilterCardNotRemembered,
                    txvFilterWithFlag,
                    txvFilterWithoutFlag,

                    )
            }
            arrayOf(imvCloseSetting,btnStartAnki,
            )
        }


    }


}