package com.example.tangochoupdated.ui.fragment.base_frag_con

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.CreateCardFragMainBinding
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.ui.viewmodel.customClasses.AnkiFragments
import com.example.tangochoupdated.ui.viewmodel.customClasses.LibraryFragment
import com.example.tangochoupdated.ui.viewmodel.customClasses.MainFragment
import com.example.tangochoupdated.ui.animation.makeToast
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

    private lateinit var mainNavCon:NavController

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

        mainNavCon = requireActivity().findViewById<FragmentContainerView>(R.id.frag_container_view).findNavController()
        createCardViewModel.apply {
            val parentFlashCardCoverId =  when(baseViewModel.returnFragmentStatus()?.before){
                MainFragment.Anki -> when(ankiFragViewModel.returnActiveFragment()) {
                    AnkiFragments.AnkiBox -> null
                    AnkiFragments.Flip -> flipBaseViewModel.returnParentCard()?.belongingFlashCardCoverId
                }
                MainFragment.Library -> when(libraryViewModel.returnActiveFragment()){
                    LibraryFragment.FlashCardCover, LibraryFragment.Folder -> libraryViewModel.returnParentFile()?.fileId
                    else -> null
                }
                else -> null
            }
            var lastCardOld: Card? = null
            var calledFirst:Boolean = true


            parentCard.observe(viewLifecycleOwner){
                makeToast(requireActivity(),"setParent STring Card")
                stringCardViewModel.setParentCard(it)
            }

            createCardViewModel.getSisterCards(parentFlashCardCoverId).observe(viewLifecycleOwner){
                if(calledFirst){
                    onClickEditCard(it.find { it.libOrder == returnParentPosition()  } ?:return@observe,cardNavCon)
                    calledFirst = false
                }
            }


        }




        val root: View = binding.root




        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {

                while( mainNavCon.backQueue[mainNavCon.backQueue.size-1].destination.label.toString()==getString(R.string.nav_label_main_create_card)){
                    mainNavCon.popBackStack()
                    makeToast(requireActivity(),"called")
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}