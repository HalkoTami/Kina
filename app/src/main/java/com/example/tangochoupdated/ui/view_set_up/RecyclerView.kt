package com.example.tangochoupdated.ui.view_set_up

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.ui.listadapter.AnkiBoxListAdapter

class ViewSetUp {

    fun setUpAnkiBoxRVListAdapter(recyclerView: RecyclerView,
                                  context: Context):AnkiBoxListAdapter{
        val adapter = AnkiBoxListAdapter(context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.isNestedScrollingEnabled = false
        return adapter
    }
}