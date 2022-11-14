package com.korokoro.kina.actions

import androidx.navigation.NavController
import com.korokoro.kina.R

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

        val destinationPos = navCon.backQueue.size-times-2

        if(destinationPos in 0 .. navCon.backQueue.size){
            val destination = navCon.backQueue[destinationPos]
            val destinationSize = navCon.backQueue.indexOf(destination)
            while (navCon.backQueue.size != destinationSize){
                navCon.popBackStack()
            }
        }
    }
}