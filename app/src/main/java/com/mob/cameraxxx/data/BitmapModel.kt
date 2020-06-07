package com.mob.cameraxxx.data


import android.graphics.Bitmap

class BitmapModel {
    var id:Int
    var image:Bitmap
    constructor(_id:Int,bitmap:Bitmap){
        id=_id
        image=bitmap
    }
}