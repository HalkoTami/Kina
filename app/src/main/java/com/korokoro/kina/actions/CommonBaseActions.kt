package com.korokoro.kina.actions

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast

fun makeToast(context: Context, string: String){
    Toast.makeText(context,string, Toast.LENGTH_SHORT).show()
}
fun setXAndY(view: View, x:Float, y:Float){
    view.x = x
    view.y = y
}
fun getWindowDisplayHeightDiff(windowFrame: View): Int {
    val a = Rect()
    windowFrame.getWindowVisibleDisplayFrame(a)
    val windowTop =  windowFrame.top
    val displayTop = a.top
    return displayTop - windowTop
}
fun doOnGlobalLayout(view: View,void: Void,remove:Boolean):ViewTreeObserver.OnGlobalLayoutListener{
    val a = ViewTreeObserver.OnGlobalLayoutListener{
        void
    }
    view.viewTreeObserver.addOnGlobalLayoutListener(a)
    return a
}
fun changeViewVisibility(view:View,visibility: Boolean){
    view.visibility = if(visibility) View.VISIBLE else View.GONE
}
fun showKeyBoard(editText: EditText,context: Context){
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(editText, 0)
}
fun hideKeyBoard(editText: EditText,context: Context){
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(editText.windowToken, 0 )
}
fun changeViewIfRVEmpty(list: List<Any>?, frameLayout: FrameLayout, emptyView: View){
    if(list.isNullOrEmpty()){
        frameLayout.addView(emptyView)
    } else {
        frameLayout.removeView(emptyView)
    }
}