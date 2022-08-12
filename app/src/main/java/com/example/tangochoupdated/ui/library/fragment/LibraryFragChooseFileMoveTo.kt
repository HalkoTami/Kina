package com.example.tangochoupdated.ui.library.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.LibraryFragSelectFileMoveToBaseBinding
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import com.example.tangochoupdated.ui.library.LibraryAddClickListeners
import com.example.tangochoupdated.ui.library.LibraryViewModel
import com.example.tangochoupdated.ui.library.listadapter.LibFragChooseFileRVListAdapter
import com.example.tangochoupdated.ui.library.listadapter.LibFragFileRVListAdapter



class LibraryFragChooseFileMoveTo  : Fragment(){
    private val args: LibraryFragChooseFileMoveToArgs by navArgs()

    private lateinit var myNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()

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
        val adapter = LibFragChooseFileRVListAdapter(createFileViewModel,libraryViewModel,requireActivity())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.isNestedScrollingEnabled = false
        libraryViewModel.apply {
            val flashcard = returnParentFile()?.fileStatus == FileStatus.TANGO_CHO_COVER ||
                    returnModeInBox()==true
            childFilesFromDB(args.folderId?.single()).observe(viewLifecycleOwner) {
                setChildFilesFromDB(it)
                val list = if(flashcard){
                    it.filter { it.fileStatus == FileStatus.TANGO_CHO_COVER }
                } else it.filter { it.fileStatus == FileStatus.FOLDER && returnSelectedFiles()!!.contains(it).not()}
                adapter.submitList(list)
            }

            binding.topBarChooseFileMoveToBinding.imvCloseChooseFileMoveTo.setImageDrawable(
                AppCompatResources.getDrawable(requireActivity(),
                    if(flashcard) R.drawable.icon_move_to_flashcard_cover else R.drawable.icon_move_to_folder
                )
            )

            selectedFiles.observe(viewLifecycleOwner){
                if(it.size == 0){
                    libraryViewModel.setMultipleSelectMode(false)
                    myNavCon.popBackStack()
                }
                binding.topBarChooseFileMoveToBinding.txvChooseFileMoveTo.text =
                    if(flashcard) "${it?.size} 個のアイテムを単語帳に移動" else
                        "${it?.size}個のアイテムをフォルダに移動"
            }

        }
        LibraryAddClickListeners().fragLibChooseFileMoveToAddCL(binding,libraryViewModel,myNavCon,requireActivity())

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

