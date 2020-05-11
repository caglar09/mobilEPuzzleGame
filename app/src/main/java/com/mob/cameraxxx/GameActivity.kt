package com.mob.cameraxxx

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mob.cameraxxx.adapters.BitmapRecyclerAdapters
import com.mob.cameraxxx.data.BitmapModel
import com.mob.cameraxxx.helpers.BitmapHelper
import java.util.*
import kotlin.collections.ArrayList

private var REQUEST_CODE_PERMISSIONS: Int = 10
private var REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.INTERNET)

class GameActivity : AppCompatActivity() {
    var bitmapList: ArrayList<BitmapModel> = arrayListOf()
    var orderBitmapList = intArrayOf()
    val row: Int = 3
    val count: Int = 3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        var grd_BitmapListView = findViewById(R.id.grd_BitmapListView) as RecyclerView
        var orjImg = findViewById(R.id.img_Orji) as ImageView
        var bmp = BitmapFactory.decodeResource(resources, R.drawable.naturals)
        //var bmp = BitmapFactory.decodeResource(resources,R.drawable.space2)
        var bitmaps = BitmapHelper.splitBitmap(bmp, row, count)
        orderBitmapList = bitmaps.map { bitmapModel -> bitmapModel.id }.toIntArray()
        var e = arrayListOf<BitmapModel>()
        var suffleArr = bitmaps.shuffled()
        e.addAll(suffleArr)
        bitmapList = e
        //var adapters = BitmapAdapters(this, bitmaps,row,count)
        var adapters = BitmapRecyclerAdapters(this, bitmapList, row, count)
        orjImg.setImageBitmap(bmp)

        var gridLayoutManager = GridLayoutManager(this, row, LinearLayoutManager.VERTICAL, false)

        grd_BitmapListView.layoutManager = gridLayoutManager
        grd_BitmapListView.adapter = adapters
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(grd_BitmapListView)


    }

    var simpleCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END, 0) {
                @RequiresApi(Build.VERSION_CODES.O_MR1)
                override fun onMove(@NonNull recyclerView: RecyclerView, @NonNull viewHolder: RecyclerView.ViewHolder, @NonNull target: RecyclerView.ViewHolder): Boolean {
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition = target.adapterPosition
                    if (checkStepControl(fromPosition, toPosition)) {
                        Collections.swap(bitmapList, fromPosition, toPosition)

                        recyclerView.adapter = BitmapRecyclerAdapters(baseContext, bitmapList, row, count)
                        if (checkFinishControl()) {
                            var dialog = AlertDialog.Builder(this@GameActivity)
                            dialog.setTitle("Puzzle Oyunu")
                            dialog.setMessage("Tebrikler! puzzle'ı tamamladınız")
                            dialog.show()
                        }
                    }
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }

            }

    fun checkFinishControl(): Boolean {
        var maps = bitmapList.map { bitmapModel -> bitmapModel.id }.toIntArray()
        return orderBitmapList contentEquals maps
    }

    fun checkStepControl(_from: Int, _to: Int): Boolean {
        if (_from + 1 == _to) {
            return true
        } else if (_from - 1 == _to) {
            return true
        } else if (_from + row == _to) {
            return true
        } else if (_from - row == _to) {
            return true
        } else return false
    }


}
