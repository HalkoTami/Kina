package com.example.tangochoupdated.ui.fragment.base_frag_con

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.example.tangochoupdated.databinding.CreateCardFragMainBinding
import com.example.tangochoupdated.db.enumclass.AnkiFragments
import com.example.tangochoupdated.db.enumclass.LibraryFragment
import com.example.tangochoupdated.db.enumclass.MainFragment
import com.example.tangochoupdated.ui.viewmodel.*


class CreateCardFragmentBase  : Fragment() {

    private var _binding: CreateCardFragMainBinding? = null
    private val baseViewModel: BaseViewModel by activityViewModels()
    private val ankiSettingVM: AnkiSettingPopUpViewModel by activityViewModels()
    private val ankiFragViewModel : AnkiFragBaseViewModel by activityViewModels()
    private val ankiBoxViewModel: AnkiBoxFragViewModel by activityViewModels()
    private val flipBaseViewModel: AnkiFlipFragViewModel by activityViewModels()
    private val createCardViewModel:CreateCardViewModel by activityViewModels()
    private val stringCardViewModel : StringCardViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding =  CreateCardFragMainBinding.inflate(inflater, container, false)
//
        val a = childFragmentManager.findFragmentById(binding.createCardFragCon.id) as NavHostFragment
        val cardNavCon = a.navController
        baseViewModel.setChildFragmentStatus(MainFragment.EditCard)

        createCardViewModel.apply {
            val parentFlashCardCoverId =  when(baseViewModel.returnFragmentStatus()?.before){
                MainFragment.Anki -> when(ankiFragViewModel.returnActiveFragment()) {
                    AnkiFragments.AnkiBox -> null
                    AnkiFragments.Flip -> flipBaseViewModel.returnParentCard()?.belongingFlashCardCoverId
                }
                MainFragment.Library -> when(libraryViewModel.returnActiveFragment()){
                    LibraryFragment.FlashCardCover,LibraryFragment.Folder -> libraryViewModel.returnParentFile()?.fileId
                    else -> null
                }
                else -> null
            }
            lastInsertedCard(parentFlashCardCoverId).observe(viewLifecycleOwner) {
                if (returnOpenEditCard()&&it!=null) {
                    setOpenEditCard(false)
                    onClickEditCard(it, cardNavCon)
                }
            }
            var calledFirst:Boolean = true


            createCardViewModel.getSisterCards(parentFlashCardCoverId).observe(viewLifecycleOwner){
                if(calledFirst){
                    calledFirst = false
                    if(it.isEmpty()){
                        saveEmptyCard(1,parentFlashCardCoverId)
                    } else {
                        if(returnStartingPosition()!=null){
                            val sorted = it.sortedBy { it.libOrder }
                            onClickEditCard(sorted[returnStartingPosition()!!],cardNavCon)
                            Toast.makeText(requireActivity(),"position ${ returnStartingPosition()}",Toast.LENGTH_SHORT).show()
                            setStartingPosition(null)
                        }

                    }
                }
            }
            parentCard.observe(viewLifecycleOwner){
                stringCardViewModel.setParentCard(it)
            }



        }




        val root: View = binding.root




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}