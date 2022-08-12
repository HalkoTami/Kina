package com.example.tangochoupdated.ui.mainactivity

import android.content.ClipData
import android.view.View
import androidx.core.view.ContentInfoCompat
import androidx.core.view.OnReceiveContentListener


class MyReceiver : OnReceiveContentListener {
    companion object {
        val MIME_TYPES = arrayOf("image/*", "video/*")
    }

    override fun onReceiveContent(view: View, payload: ContentInfoCompat): ContentInfoCompat? {
        TODO("Not yet implemented")
    }
}