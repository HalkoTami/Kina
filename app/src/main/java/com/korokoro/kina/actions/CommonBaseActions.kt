package com.korokoro.kina.actions

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.navigation.NavController
import com.korokoro.kina.R
import java.text.SimpleDateFormat
import java.util.*


fun makeToast(context: Context, string: String){
    Toast.makeText(context,string, Toast.LENGTH_SHORT).show()
}
fun setXAndY(view: View, x:Float, y:Float){
    view.x = x
    view.y = y
}

fun getWindowDisplayHeightDiff(resources: Resources): Int {
    var statusBarHeight = 0
    val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        statusBarHeight = resources.getDimensionPixelSize(resourceId)
    }
    return statusBarHeight
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
fun changeViewIfRVEmpty(list: List<Any>, frameLayout: FrameLayout, emptyView: View){
    if(list.isEmpty()){
        frameLayout.addView(emptyView)
    } else {
        frameLayout.removeView(emptyView)
    }
}