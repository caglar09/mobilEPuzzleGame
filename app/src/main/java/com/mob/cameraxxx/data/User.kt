package com.mob.cameraxxx.data

data class User(val userId:String,val userName:String,val email:String,var sections:MutableList<Section>){
    constructor():this("","","", mutableListOf<Section>()){

    }
}