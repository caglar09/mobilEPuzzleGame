package com.mob.cameraxxx.data

data class Section(val id: String, val order: Int, val image: String, var textTr: String, var textEn: String,
                   var knowedTr: Boolean, var knowedEn: Boolean, var completed: Boolean, var puzzleCompleted: Boolean){
    constructor():this("0",0,"", "","",false,false,false,false){

    }
}