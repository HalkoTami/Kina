package com.koronnu.kina.actions

import androidx.navigation.NavController

class NavigationActions {
    private fun getDesLabelInPos(navCon: NavController,minus:Int):String{
        val stackSize = navCon.backQueue.size
        val desPos = stackSize-minus-1
        return if(desPos<0) "" else
            navCon.backQueue[desPos].destination.label.toString()

    }
    fun popBackStackToLabel(navCon: NavController, label:String){
        while( getDesLabelInPos(navCon,0)!=label){
            navCon.popBackStack()
        }
    }
    fun popBackStackUntilLabelIs(navCon: NavController,label: String){
        while( getDesLabelInPos(navCon,0)==label){
            navCon.popBackStack()
        }
    }
    fun popMultipleBackStack(navCon: NavController,times:Int){

        val destinationPos = navCon.backQueue.size-times-1
        while ((destinationPos in 0 until  navCon.backQueue.size)&&
            navCon.backQueue.size-1 != destinationPos){
            navCon.popBackStack()

        }

    }
}