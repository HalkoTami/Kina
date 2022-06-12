package com.example.tangochoupdated.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tangochoupdated.*

import com.example.tangochoupdated.databinding.FragmentLibraryHomeBinding
import com.example.tangochoupdated.room.dataclass.CardAndTags
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import com.google.android.material.bottomnavigation.BottomNavigationView

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
        val rvList = mutableListOf<LibraryRV>()


        viewModel.parentList.observe(requireActivity()){
           rvList.addAll(viewModel.getListData(it,null))
        }
        adapter.submitList(rvList)


        recyclerView?.layoutManager = LinearLayoutManager(context)

        val bnv :BottomNavigationView = requireActivity().findViewById(R.id.my_bnv)
        bnv.menu.getItem(0).setIcon(R.drawable.icon_library_active)


        return root


    }
    fun makeLibRVList(filelist:List<File>?,cardlist:List<CardAndTags>?):List<LibraryRV>{
        val a = mutableListOf<LibraryRV>()
        filelist?.onEach { a.add(convertFileToLibraryRV(it)!!) }
        cardlist?.onEach { a.add(convertCardToLibraryRV(it)!!) }
        return a


    }
    fun convertFileToLibraryRV(file: File?): LibraryRV? {

        when (file!!.fileStatus) {

            FileStatus.FOLDER -> {
                return LibraryRV(
                    type = LibRVViewType.Folder,
                    position = file.libOrder,
                    file = file,
                    card = null,
                    tag = null,
                    id = file.fileId
                )

            }

            FileStatus.TANGO_CHO_COVER ->
                return LibraryRV(
                    type = LibRVViewType.FlashCardCover,
                    position = file.libOrder,
                    file = file,
                    card = null,
                    tag = null,
                    id = file.fileId
                )


            else -> return null
        }

    }
    fun convertCardToLibraryRV(card: CardAndTags?): LibraryRV? {
        when (card?.card?.cardStatus) {
            CardStatus.STRING -> return LibraryRV(
                type = LibRVViewType.StringCard,
                position = card.card.libOrder,
                file = null,
                card = card.card,
                tag = card.tags,
                id = card.card.id
            )
            CardStatus.CHOICE -> return LibraryRV(
                type = LibRVViewType.ChoiceCard,
                position = card.card.libOrder,
                file = null,
                card = card.card,
                tag = card.tags,
                id = card.card.id
            )

            CardStatus.MARKER -> return LibraryRV(
                type = LibRVViewType.MarkerCard,
                position = card.card.libOrder,
                file = null,
                card = card.card,
                tag = card.tags,
                id = card.card.id
            )

            else -> return null
        }
    }

    override fun onLongClickMain(type: LibRVViewType, id: Int) {
        TODO("Not yet implemented")
    }

    override fun onClickEdit(id: Int, viewId: Int) {
        TODO("Not yet implemented")
    }

    override fun onClickAdd(type: LibRVViewType, id: Int) {
        TODO("Not yet implemented")
    }

    override fun onClickDelete(type: LibRVViewType, id: Int) {
        TODO("Not yet implemented")
    }

    override fun onClickMain(type: LibRVViewType, id: Int) {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}