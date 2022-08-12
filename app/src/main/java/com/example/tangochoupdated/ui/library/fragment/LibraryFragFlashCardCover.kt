package com.example.tangochoupdated.ui.library.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.LibraryFragOpenFlashCardCoverBaseBinding
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import com.example.tangochoupdated.ui.library.LibraryAddClickListeners
import com.example.tangochoupdated.ui.library.LibraryViewModel
import com.example.tangochoupdated.ui.library.listadapter.LibFragCardRVListAdapter


class LibraryFragFlashCardCover  : Fragment(){
    private val args: LibraryFragFlashCardCoverArgs by navArgs()

    private lateinit var myNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()

    private var _binding: LibraryFragOpenFlashCardCoverBaseBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        myNavCon = requireActivity().findViewById<FragmentContainerView>(R.id.lib_frag_con_view).findNavController()
        _binding = LibraryFragOpenFlashCardCoverBaseBinding.inflate(inflater, container, false)
        recyclerView = binding.vocabCardRV
        val adapter = LibFragCardRVListAdapter(createCardViewModel,libraryViewModel,requireActivity())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.isNestedScrollingEnabled = false

        binding.topBarFileBinding.imvFileType.setImageDrawable(AppCompatResources.getDrawable(requireActivity(),R.drawable.icon_flashcard))

        libraryViewModel.apply {
            clearFinalList()
            parentFileFromDB(args.flashCardCoverId.single()).observe(viewLifecycleOwner){
                setParentFileFromDB(it)
                binding.topBarFileBinding.txvFileTitle.text = it?.title ?:"タイトルなし"
                binding.topBarFileBinding.imvFileType.setImageDrawable(
                    GetCustomDrawables().getFlashCardIconByCol(it?.colorStatus ?:ColorStatus.GRAY,requireContext())
                )
                createCardViewModel.setParentFlashCardCover(it)
            }
            parentFileAncestorsFromDB(args.flashCardCoverId.single()).observe(viewLifecycleOwner){
                setParentFileAncestorsFromDB(it)
            }
            childCardsFromDB(args.flashCardCoverId.single()).observe(viewLifecycleOwner) {
                setChildCardsFromDB(it)
                if(returnParentFile()?.childCardsAmount!=it?.size){
                    upDateContainingCardAmount(it?.size ?:0)
                }
                adapter.submitList(it)
                binding.emptyBinding.root.visibility =
                    if (it!=null &&it.isEmpty()) View.VISIBLE else View.GONE
                createCardViewModel.setSisterCards(it)
            }
            myFinalList.observe(viewLifecycleOwner) {
//                adapter.submitList(it)
//                binding.emptyBinding.root.visibility =
//                    if (it.isEmpty()) View.VISIBLE else View.GONE
            }
            multipleSelectMode.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.root.visibility =if(it) View.VISIBLE else View.GONE
                LibraryFragmentBase().changeLibRVSelectBtnVisibility(recyclerView,it)
                LibraryFragmentBase().changeStringBtnVisibility(recyclerView,it)
            }
            makeAllUnSwiped.observe(viewLifecycleOwner){
                if(it) LibraryFragmentBase().makeLibRVUnSwiped(recyclerView)
            }
            parentFileAncestors.observe(viewLifecycleOwner){
                binding.topBarFileBinding.apply {
                    txvGGrandParentFileTitle.text = it.gGrandPFile?.title
                    txvGrandParentFileTitle.text = it.gParentFile?.title
                    txvParentFileTitle.text = it.ParentFile?.title
                    lineLayGGFile.visibility =
                        if(it.gGrandPFile != null) View.VISIBLE else View.GONE
                    lineLayGPFile.visibility =
                        if(it.gParentFile != null) View.VISIBLE else View.GONE
                    lineLayParentFile.visibility =
                        if(it.ParentFile != null) View.VISIBLE else View.GONE
                }
            }
            selectedItems.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.txvSelectingStatus.text = "${it.size}個　選択中"
            }

            LibraryAddClickListeners().fragLibFlashCardCoverAddCL(binding,libraryViewModel,myNavCon,requireActivity())
        }
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}