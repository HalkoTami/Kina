package com.example.tangochoupdated.ui.library.fragment

import LibraryPopUpConfirmDeleteClickListener
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.LibraryFragBinding
import com.example.tangochoupdated.databinding.LibraryFragInboxBaseBinding
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.db.rvclasses.LibRVViewType
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import com.example.tangochoupdated.ui.library.ConfirmMode
import com.example.tangochoupdated.ui.library.LibraryTopBarMode
import com.example.tangochoupdated.ui.library.LibraryViewModel
import com.example.tangochoupdated.ui.mainactivity.Animation


class LibraryFragmentBase : Fragment(){

    private lateinit var myNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private lateinit var adapter: LibraryListAdapter
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()

    private var _binding: LibraryFragBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LibraryFragBinding.inflate(inflater, container, false)

        libraryViewModel.confirmPopUp.observe(viewLifecycleOwner){
            when(it.confirmMode){
                ConfirmMode.DeleteOnlyParent-> {
                    binding.confirmDeletePopUpBinding.root.visibility = if(it.visible) View.VISIBLE else View.GONE
                }
                ConfirmMode.DeleteWithChildren -> binding.confirmDeleteChildrenPopUpBinding.root.visibility = if(it.visible) View.VISIBLE else View.GONE
            }
        }

        val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.frag_container_view) as NavHostFragment
        myNavCon = navHostFragment.navController
        libraryViewModel.action.observe(viewLifecycleOwner){
            val a = childFragmentManager.findFragmentById(binding.libFragConView.id) as NavHostFragment
            a.navController.navigate(it)
            Toast.makeText(requireActivity(), "action called ", Toast.LENGTH_SHORT).show()
        }

        binding.confirmDeletePopUpBinding.root.visibility  = View.VISIBLE
        addConfirmDeletePopUp()
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                libraryViewModel.onClickBack()
                myNavCon.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }

    fun addConfirmDeletePopUp(){
        val onlyP = binding.confirmDeletePopUpBinding
        val deleteAllC = binding.confirmDeleteChildrenPopUpBinding
        arrayOf(
            onlyP.btnCloseConfirmDeleteOnlyParentPopup,
            onlyP.btnCommitDeleteOnlyParent,
            onlyP.btnDenyDeleteOnlyParent,
            deleteAllC.btnCloseConfirmDeleteOnlyParentPopup,
            deleteAllC.btnCommitDeleteAllChildren,
            deleteAllC.btnDenyDeleteAllChildren
        )   .onEach {
            it.setOnClickListener(LibraryPopUpConfirmDeleteClickListener(binding,libraryViewModel))
        }
    }
}


