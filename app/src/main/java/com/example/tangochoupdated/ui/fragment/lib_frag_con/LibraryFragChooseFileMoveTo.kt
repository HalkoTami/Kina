package com.example.tangochoupdated.ui.fragment.lib_frag_con

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
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.LibraryFragSelectFileMoveToBaseBinding
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.ui.viewmodel.CreateCardViewModel
import com.example.tangochoupdated.ui.viewmodel.CreateFileViewModel
import com.example.tangochoupdated.ui.view_set_up.LibrarySetUpFragment
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel
import com.example.tangochoupdated.ui.listadapter.LibFragChooseFileRVListAdapter
import com.example.tangochoupdated.ui.viewmodel.DeletePopUpViewModel


class LibraryFragChooseFileMoveTo  : Fragment(){
    private val args: LibraryFragChooseFileMoveToArgs by navArgs()

    private lateinit var myNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()
    private val deletePopUpViewModel:DeletePopUpViewModel by activityViewModels()

    private var _binding: LibraryFragSelectFileMoveToBaseBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        myNavCon =  requireActivity().findViewById<FragmentContainerView>(R.id.lib_frag_con_view).findNavController()
        _binding = LibraryFragSelectFileMoveToBaseBinding.inflate(inflater, container, false)
        recyclerView = binding.vocabCardRV
        val adapter = LibFragChooseFileRVListAdapter(createFileViewModel,libraryViewModel,requireActivity(),deletePopUpViewModel,myNavCon)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.isNestedScrollingEnabled = false
        libraryViewModel.apply {
            val flashcard = returnParentFile()?.fileStatus == FileStatus.TANGO_CHO_COVER ||
                    returnModeInBox()==true
            if(args.fileId != null){
                parentFileFromDB(args.fileId?.single()).observe(viewLifecycleOwner){
                    setParentFileFromDB(it)
                }
            }
            childFilesFromDB(args.fileId?.single()).observe(viewLifecycleOwner) {
                setParentRVItems(it)
                val list = if(flashcard){
                    it.filter { it.fileStatus == FileStatus.TANGO_CHO_COVER }
                } else it.filter { it.fileStatus == FileStatus.FOLDER && returnSelectedItems().contains(it).not()}
                adapter.submitList(list)
            }
            parentFileAncestorsFromDB(args.fileId?.single()).observe(viewLifecycleOwner){
                createFileViewModel.filterBottomMenuWhenInChooseFileMoveTo(
                    flashCard = flashcard, args.fileId == null,it
                )
            }

            binding.topBarChooseFileMoveToBinding.imvFileMoveToStatus.setImageDrawable(
                AppCompatResources.getDrawable(requireActivity(),
                    if(flashcard) R.drawable.icon_move_to_flashcard_cover else R.drawable.icon_move_to_folder
                )
            )
            binding.topBarChooseFileMoveToBinding.txvChooseFileMoveTo.text =
                    if(flashcard) "${returnSelectedItems().size} 個のアイテムを単語帳に移動" else
                        "${returnSelectedItems().size}個のアイテムをフォルダに移動"

            chooseFileMoveToMode.observe(viewLifecycleOwner){
                if(it == false){
                    setMultipleSelectMode(false)
                    myNavCon.popBackStack()
                }
            }
        }
//        LibrarySetUpFragment(libraryViewModel,deletePopUpViewModel).setUpFragLibChooseFileMoveTo(binding,myNavCon,requireActivity())

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

