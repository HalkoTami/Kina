package com.example.tangochoupdated.ui.view_set_up
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.ui.listener.FlipBaseFragCL

class AnkiFlipFragViewSetUp(private val bindingBase:AnkiFlipFragBaseBinding) {
    fun setUpCL(){
        bindingBase.apply {
            topBinding.apply {
                arrayOf(
                    imvBack,
                    imvAnkiSetting,
                    imvAnkiSetting,
                    btnSetFlag,
                    btnRemembered,
                    btnFlipNext,
                    btnFlipPrevious,
                    btnAddCard).onEach {
                        it.setOnClickListener(FlipBaseFragCL(bindingBase))
                }
            }

        }
    }


}