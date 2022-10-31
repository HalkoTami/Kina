package com.korokoro.kina.actions

import com.korokoro.kina.db.dataclass.Card

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
    fun getSortedAndUpdatedCardList(list: List<Card>?):List<Card>{
        val sorted =  sortCards(list).toMutableList()
        sorted.onEach { eachCard ->
            val sameLibOrder =  sorted.filter { it.cardBefore == eachCard.cardBefore }
            if(sameLibOrder.size>1){
                val sameLibSorted = sameLibOrder.sortedBy { it.id }.reversed()
                sameLibOrder.onEach {
                    val parentIndex = sameLibSorted.indexOf(it)
                    if(parentIndex>0){
                        it.cardBefore = sameLibSorted[parentIndex-1].id
                    }
                }
            }

        }
        return sorted
    }
}
