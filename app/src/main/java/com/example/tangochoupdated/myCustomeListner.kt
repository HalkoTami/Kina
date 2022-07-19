package com.example.tangochoupdated

import com.example.tangochoupdated.databinding.ColorPaletBinding
import com.example.tangochoupdated.room.enumclass.ColorStatus

interface ColPalletListener {
    fun changeColorPalletCol(colorStatus: ColorStatus, selected:Boolean, colorPaletBinding: ColorPaletBinding)
    fun makeAllColPalletUnselected(colorPalletBinding: ColorPaletBinding)
}