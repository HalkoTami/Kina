package com.koronnu.kina.actions

import com.koronnu.kina.db.dataclass.Card

class SortActions(){
    fun sortCards(list: List<Card>?):List<Card>{
        val final = mutableListOf<Card>()
        if(list!=null){
            fun getNextCard(cardBefore: Card?){
                val nextList = list.filter { it.cardBefore == cardBefore?.id }
                if(nextList.size==1){
                    final.addAll(nextList)
                    getNextCard(nextList.single())
                } else if(nextList.size>1){
                    val sorted = nextList.sortedBy { it.id }.reversed()
                    getNextCard(sorted.last())
                }
            }
            getNextCard(null)

        }
        return  final
    }


}