package com.koronnu.kina.util

import android.content.Context
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast


fun makeToast(context: Context, string: String){
    Toast.makeText(context,string, Toast.LENGTH_SHORT).show()
}


fun setClickListeners(views: Array<View>,clickListener: OnClickListener){
    views.onEach { it.setOnClickListener (clickListener) }
}


fun changeViewVisibility(view:View,visibility: Boolean){
    view.visibility = if(visibility) View.VISIBLE else View.GONE
}
fun changeMulVisibility( views : Array<View>,visibility: Boolean){
    views.onEach { changeViewVisibility(it,visibility) }
}
fun showKeyBoard(editText: EditText,context: Context){
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(editText, 0)
}
fun hideKeyBoard(editText: EditText){
    val imm =  editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(editText.windowToken, 0 )
}
fun changeViewIfRVEmpty(list: List<Any>, frameLayout: FrameLayout, emptyView: View){
    if(list.isEmpty()){
        frameLayout.removeAllViews()
        frameLayout.addView(emptyView)
    } else {
        frameLayout.removeView(emptyView)
    }
}