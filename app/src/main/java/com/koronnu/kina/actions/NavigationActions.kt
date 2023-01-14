package com.koronnu.kina.actions

import androidx.navigation.NavController

class NavigationActions {
    private fun getDesLabelInStackBefore(navCon: NavController):String{
        val stackSize = navCon.backQueue.size
        val desPos = stackSize-1
        return if(desPos<0) "" else
            navCon.backQueue[desPos].destination.label.toString()

    }
    fun popBackStackToLabel(navCon: NavController, label:String){
        while( getDesLabelInStackBefore(navCon)!=label){
            navCon.popBackStack()
        }
    }

}