package com.example.tangochoupdated.ui.fragment.lib_frag_con

import android.os.Bundle
import android.view.*
import android.widget.Toast
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
import com.example.tangochoupdated.ui.view_set_up.LibrarySetUpFragment
import com.example.tangochoupdated.ui.fragment.base_frag_con.LibraryFragmentBase
import com.example.tangochoupdated.ui.listadapter.LibFragPlaneRVListAdapter
import com.example.tangochoupdated.ui.view_set_up.GetCustomDrawables
import com.example.tangochoupdated.ui.viewmodel.*


class LibraryFragFlashCardCover  : Fragment(){
    private val args: LibraryFragFlashCardCoverArgs by navArgs()

    private lateinit var myNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val stringCardViewModel: StringCardViewModel by activityViewModels()
    private val deletePopUpViewModel: DeletePopUpViewModel by activityViewModels()
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
        val adapter = LibFragPlaneRVListAdapter(
            createFileViewModel  = createFileViewModel,
            libraryViewModel  = libraryViewModel,
            context  = requireActivity(),
            stringCardViewModel  = stringCardViewModel,
            createCardViewModel  = createCardViewModel,deletePopUpViewModel)
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
                    GetCustomDrawables(requireContext()).getFlashCardIconByCol(it?.colorStatus ?:ColorStatus.GRAY,)
                )
                createCardViewModel.setParentFlashCardCover(it)
                Toast.makeText(requireActivity(),"${it.childData.childCardsAmount}",Toast.LENGTH_SHORT).show()
                Toast.makeText(requireActivity(),"${it.descendantsData.descendantsCardsAmount}",Toast.LENGTH_SHORT).show()

            }
            parentFileAncestorsFromDB(args.flashCardCoverId.single()).observe(viewLifecycleOwner){
                setParentFileAncestorsFromDB(it)

                createFileViewModel.filterBottomMenuByAncestors(it,returnParentFile()!!)
            }
            childCardsFromDB(args.flashCardCoverId.single()).observe(viewLifecycleOwner) {
                setParentRVItems(it ?: mutableListOf())
                adapter.submitList(it)
                binding.emptyBinding.root.visibility =
                    if (it!=null &&it.isEmpty()) View.VISIBLE else View.GONE
                createCardViewModel.setSisterCards(it)
            }
            multipleSelectMode.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.root.visibility =if(it) View.VISIBLE else View.GONE
                binding.topBarFileBinding.topPart.visibility = if(!it) View.VISIBLE else View.INVISIBLE
                LibraryFragmentBase().changeLibRVSelectBtnVisibility(recyclerView,it)
                LibraryFragmentBase().changeStringBtnVisibility(recyclerView,it)
            }
            makeAllUnSwiped.observe(viewLifecycleOwner){
                if(it) LibraryFragmentBase().makeLibRVUnSwiped(recyclerView)
            }
            changeAllRVSelectedStatus.observe(viewLifecycleOwner){
                LibraryFragmentBase().changeLibRVAllSelectedState(recyclerView,it)
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

            LibrarySetUpFragment(libraryViewModel,deletePopUpViewModel).setUpFragLibFlashCardCover(binding,myNavCon,requireActivity())
        }
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}