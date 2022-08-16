package com.example.tangochoupdated.ui.view_set_up

import com.example.tangochoupdated.databinding.AnkiHomeFragRvItemFileBinding
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.ui.fragment.anki_frag_con.AnkiFragmentAnkiBox
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel

class AnkiBoxViewSetUp(val ankiBoxVM:AnkiBoxFragViewModel,
                       val ankiBoxFragment: AnkiFragmentAnkiBox,
                       val getDraw:GetCustomDrawables) {
    fun setUpRVFileBinding(binding:AnkiHomeFragRvItemFileBinding,
                           file: File){
        binding.apply {
            imvFileType.setImageDrawable(
                getDraw.getFileIconByFile(file)
            )
            txvFileTitle.text = file.title
            ankiBoxGraphBinding.txvPercentage

        }


    }
}