package com.mob.cameraxxx

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mob.cameraxxx.adapters.BitmapRecyclerAdapters
import com.mob.cameraxxx.data.BitmapModel
import com.mob.cameraxxx.helpers.BitmapHelper
import com.mob.cameraxxx.service.DataAdapterService
import com.mob.cameraxxx.service.StartDragListener
import kotlinx.android.synthetic.main.activity_game.*
import java.util.*
import kotlin.collections.ArrayList

private var REQUEST_CODE_PERMISSIONS: Int = 10
private var REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.INTERNET)

class GameActivity : AppCompatActivity(), StartDragListener {
    var bitmapList: ArrayList<BitmapModel>? = null
    var orderBitmapList: IntArray? = null
    val row: Int = 3
    val count: Int = 3
    final lateinit var touchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        try {

            var grd_BitmapListView = findViewById(R.id.grd_BitmapListView) as RecyclerView
            grd_BitmapListView.minimumWidth = window.attributes.width
            grd_BitmapListView.minimumHeight = window.attributes.width
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
            var adapters = BitmapRecyclerAdapters(this, bitmapList!!, row, count, this)
            orjImg.setImageBitmap(bmp)

            var gridLayoutManager = GridLayoutManager(this, row, LinearLayoutManager.VERTICAL, false)

            grd_BitmapListView.layoutManager = gridLayoutManager
            grd_BitmapListView.adapter = adapters
            touchHelper = ItemTouchHelper(simpleCallback)
            touchHelper.attachToRecyclerView(grd_BitmapListView)


        } catch (ex: ExceptionInInitializerError) {
            Toast.makeText(this, ex.localizedMessage, Toast.LENGTH_LONG).show()
        }


    }

    var simpleCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END, 0) {
        @RequiresApi(Build.VERSION_CODES.O_MR1)
        override fun onMove(@NonNull recyclerView: RecyclerView, @NonNull viewHolder: RecyclerView.ViewHolder, @NonNull target: RecyclerView.ViewHolder): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            if (checkStepControl(fromPosition, toPosition)) {
                Collections.swap(bitmapList!!, fromPosition, toPosition)

                recyclerView.adapter = BitmapRecyclerAdapters(baseContext, bitmapList!!, row, count, this@GameActivity)
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

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            //super.onMoved(grd_BitmapListView,viewHolder!!,viewHolder!!.layoutPosition,viewHolder!!.itemView,)
            super.onSelectedChanged(viewHolder, actionState)
        }

        override fun canDropOver(recyclerView: RecyclerView, current: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            super.onMoved(recyclerView, current, current.adapterPosition, target, target.adapterPosition, recyclerView.scrollX, recyclerView.scrollY)
            return super.canDropOver(recyclerView, current, target)
        }

        override fun isLongPressDragEnabled(): Boolean {
            return true
        }


    }

    fun checkFinishControl(): Boolean {
        var maps = bitmapList!!.map { bitmapModel -> bitmapModel.id }.toIntArray()
        return orderBitmapList!! contentEquals maps
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

    override fun requestDrag(viewHolder: RecyclerView.ViewHolder?) {
        touchHelper.startDrag(viewHolder!!)
    }

}
