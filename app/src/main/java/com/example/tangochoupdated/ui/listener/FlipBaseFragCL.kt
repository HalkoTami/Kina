package com.example.tangochoupdated.ui.listener

import android.view.View
import com.example.tangochoupdated.databinding.AnkiFlipFragBaseBinding


class FlipBaseFragCL(val binding:AnkiFlipFragBaseBinding): View.OnClickListener {

    override fun onClick(v: View?) {
        binding.apply {
            topBinding.apply {
                when(v){
                    imvBack -> TODO()
                    imvAnkiSetting -> TODO()
                    imvAnkiSetting -> TODO()
                    btnSetFlag -> TODO()
                    btnRemembered -> TODO()
                    btnFlipNext -> TODO()
                    btnFlipPrevious -> TODO()
                    btnAddCard -> TODO()
                }
            }
        }
    }
}