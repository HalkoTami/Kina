package com.example.tangochoupdated.ui.library

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.rgb
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tangochoupdated.*

import com.example.tangochoupdated.databinding.FragmentLibraryHomeBinding
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.CardAndTags
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.planner.CreateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*

class HomeFragment : Fragment(),DataClickListener{

    lateinit var adapter:LibraryListAdapter

    private val viewModel: LibraryRVViewModel by lazy { ViewModelProvider(this, ViewModelFactory(
        (requireActivity().application as RoomApplication).repository
    )).get(LibraryRVViewModel::class.java) }
    private var _binding: FragmentLibraryHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLibraryHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root



        val recyclerView = _binding?.vocabCardRV
        adapter = LibraryListAdapter(this)
        recyclerView?.adapter = adapter


        viewModel.parentList.observe(requireActivity()){
            adapter.submitList(makeLibRVList(it,null))
        }

        recyclerView?.layoutManager = LinearLayoutManager(context)

        val bnv :BottomNavigationView = requireActivity().findViewById(R.id.my_bnv)
        bnv.menu.getItem(0).setIcon(R.drawable.icon_library_active)


        return root


    }
    fun makeLibRVList(filelist:List<File>?,cardlist:List<CardAndTags>?):List<LibraryRV>{
        val a = mutableListOf<LibraryRV>()
        filelist?.onEach { a.add(convertFileToLibraryRV(it)) }
        cardlist?.onEach { a.add(convertCardToLibraryRV(it)) }
        return a


    }

    fun convertFileToLibraryRV(file: File?): LibraryRV {

        when (file!!.fileStatus) {

            FileStatus.FOLDER -> {
                return LibraryRV(
                    type = LibRVViewType.Folder,
                    file = file,
                    tag = null,
                    card = null,
                    position = file.libOrder
                )

            }

            FileStatus.TANGO_CHO_COVER ->
                return LibraryRV(
                    type = LibRVViewType.FlashCardCover,
                    file = file,
                    tag = null,
                    card = null,
                    position = file.libOrder
                )


            else -> return LibraryRV(LibRVViewType.Folder, 0, null, null, null)
        }

    }
    fun convertCardToLibraryRV(card: CardAndTags?): LibraryRV {
        when (card?.card?.cardStatus) {
            CardStatus.STRING -> return LibraryRV(
                type = LibRVViewType.StringCard,
                file = null,
                tag = card.tags,
                card = card.card,
                position = card.card.libOrder
            )
            CardStatus.CHOICE -> return LibraryRV(
                type = LibRVViewType.ChoiceCard,
                file = null,
                tag = card.tags,
                card = card.card,
                position = card.card.libOrder
            )

            CardStatus.MARKER -> return LibraryRV(
                type = LibRVViewType.MarkerCard,
                file = null,
                tag = card.tags,
                card = card.card,
                position = card.card.libOrder
            )

            else -> return LibraryRV(LibRVViewType.Folder, 0, null, null, null)
        }
    }

    override fun onTouchWhole() {
        TODO("Not yet implemented")
    }

    override fun onTouchMain() {
        TODO("Not yet implemented")
    }

    override fun onLongClickMain() {
        TODO("Not yet implemented")
    }

    override fun onClickEdit() {
        TODO("Not yet implemented")
    }

    override fun onClickTags() {
        TODO("Not yet implemented")
    }

    override fun oncClickSelect() {
        TODO("Not yet implemented")
    }

    override fun onClickAdd() {
        TODO("Not yet implemented")
    }

    override fun onTouchDelete() {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}