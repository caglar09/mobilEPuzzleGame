package com.mob.cameraxxx.adapters

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.mob.cameraxxx.R
import com.mob.cameraxxx.data.BitmapModel
import kotlinx.android.synthetic.main.bitmap_partial.view.*

class BitmapAdapters : BaseAdapter {
    var _bMpas = arrayListOf<Bitmap>()
    var _context: Context
    var row: Int = 0
    private var count: Int = 0

    constructor(ctx: Context, bMaps: ArrayList<Bitmap>, _row: Int, _count: Int) {
        var e = arrayListOf<Bitmap>()
        var suffleArr = bMaps.shuffled()
        e.addAll(suffleArr)
        _bMpas = e
        _context = ctx
        row = _row
        count = _count
    }

    private fun printBoundary(a: ArrayList<ArrayList<Int>>, m: Int,
                              n: Int): ArrayList<Int> {
        var cornerNumbers = arrayListOf<Int>()
        for (i in 0 until m) {
            for (j in 0 until n) {
                if (i == 0)
                    cornerNumbers.add(a[i][j])
                else if (i == m - 1)
                    cornerNumbers.add(a[i][j])
                else if (j == 0)
                    cornerNumbers.add(a[i][j])
                else if (j == n - 1)
                    cornerNumbers.add(a[i][j])
            }

        }
        return cornerNumbers

    }

    public fun getStepControl(_f: Int, _to: Int): Boolean {
        var from = _f
        var to = _to
        var directNumbers: ArrayList<Int>? = arrayListOf()
        var cornerNumbers: ArrayList<Int>? = arrayListOf()
        var edgeNumbers: ArrayList<Int>? = arrayListOf()

        var objs = ArrayList<ArrayList<Int>>()
        var _index: Int = 0
        var obj = ArrayList<Int>()
        for (e in 0 until count * row) {
            obj.add(e)
            _index++
            if (_index % row == 0) {
                objs.add(obj)
                obj = ArrayList<Int>()
                _index = 0
            }
        }
        cornerNumbers = printBoundary(objs, row, count)
        return checkStepControl(_from = from, _to = to)
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var item = _bMpas!![position]
        var inflator = _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = inflator.inflate(R.layout.bitmap_partial, null)
        view.txtItemId.setText(position.toString())
        view.img.setImageBitmap(item)
        view.tag = position.toString()
        view.setOnLongClickListener { v ->
            val item = ClipData.Item(v.tag as? CharSequence)
            val dragData = ClipData(v.tag as? CharSequence, arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)
            val myShadow = MyDragShadowBuilder(view)

            v.startDragAndDrop(dragData, myShadow, null, 0)
        }
        view.setOnDragListener { v: View, event: DragEvent ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    // Determines if this View can accept the dragged data
                    if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        // As an example of what your application might do,
                        // applies a blue color tint to the View to indicate that it can accept
                        // data.
                        // (v.img as? ImageView)?.setColorFilter(Color.BLUE)

                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate()

                        // returns true to indicate that the View can accept the dragged data.
                        true
                    } else {
                        // Returns false. During the current drag and drop operation, this View will
                        // not receive events again until ACTION_DRAG_ENDED is sent.
                        false
                    }
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    // Applies a green tint to the View. Return true; the return value is ignored.
                    (v.img as? ImageView)?.setColorFilter(Color.GREEN)
                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate()
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION ->
                    // Ignore the event
                    true
                DragEvent.ACTION_DRAG_EXITED -> {
                    // Re-sets the color tint to blue. Returns true; the return value is ignored.
                    (v.img as? ImageView)?.clearColorFilter()
                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate()
                    true
                }
                DragEvent.ACTION_DROP -> {
                    // Gets the item containing the dragged data
                    val item: ClipData.Item = event.clipData.getItemAt(0)

                    // Gets the text data from the item.
                    val dragData = item.text.toString().toInt()

                    // Displays a message containing the dragged data.
                    Toast.makeText(_context, "Dragged data is " + dragData, Toast.LENGTH_LONG).show()
                    var vValue = v.txtItemId.text.toString().toInt()

                    var result = getStepControl(dragData, vValue)
                    if (result) {
                        var i = _bMpas[dragData]
                        var t = _bMpas[vValue]
                        if (i !== null && t !== null) {
                            v.img.setImageBitmap(i)

                            _bMpas[dragData] = t
                            _bMpas[vValue] = i
                        }
                    }

                    (v.img as? ImageView)?.clearColorFilter()
                    // Invalidates the view to force a redraw
                    v.invalidate()

                    // Returns true. DragEvent.getResult() will return true.
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    (v.img as? ImageView)?.clearColorFilter()
                    // Invalidates the view to force a redraw
                    v.invalidate()

                    // Does a getResult(), and displays what happened.
                    when (event.result) {
                        true -> {
                            var data = v.txtItemId.text
                            Toast.makeText(_context, "The drop was handled.", Toast.LENGTH_LONG)
                        }
                        else ->
                            Toast.makeText(_context, "The drop didn't work.", Toast.LENGTH_LONG)
                    }.show()

                    // returns true; the value is ignored.
                    true
                }
                else -> {
                    false
                }
            }
        }
        return view
    }

    override fun getItem(position: Int): Any {
        return _bMpas!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return _bMpas!!.size
    }

}

private class MyDragShadowBuilder(v: View) : View.DragShadowBuilder(v) {

    private val shadow = ColorDrawable(Color.LTGRAY)

    // Defines a callback that sends the drag shadow dimensions and touch point back to the
    // system.
    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        // Sets the width of the shadow to half the width of the original View
        val width: Int = view.width / 2

        // Sets the height of the shadow to half the height of the original View
        val height: Int = view.height / 2

        // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
        // Canvas that the system will provide. As a result, the drag shadow will fill the
        // Canvas.
        shadow.setBounds(0, 0, width, height)

        // Sets the size parameter's width and height values. These get back to the system
        // through the size parameter.
        size.set(width, height)

        // Sets the touch point's position to be in the middle of the drag shadow
        touch.set(width / 2, height / 2)
    }

    // Defines a callback that draws the drag shadow in a Canvas that the system constructs
    // from the dimensions passed in onProvideShadowMetrics().
    override fun onDrawShadow(canvas: Canvas) {
        // Draws the ColorDrawable in the Canvas passed in from the system.
        shadow.draw(canvas)
    }
}

class BitmapRecyclerAdapters : RecyclerView.Adapter<BitmapRecyclerAdapters.ViewHolder> {
    var _bMpas: ArrayList<BitmapModel>
    var _context: Context
    var row: Int = 0
    private var count: Int = 0

    constructor(ctx: Context, bMaps: ArrayList<BitmapModel>, _row: Int, _count: Int) {

        _bMpas = bMaps
        _context = ctx
        row = _row
        count = _count
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.bitmap_partial, parent, false)
        view.tag = viewType.toString()
        var v = ViewHolder(view)

        /* view.setOnTouchListener { v: View, event: MotionEvent ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                startDragging(c)
            }
            return@setOnTouchListener true
        }*/
        return v
    }

    /*    fun startDragging(viewHolder: RecyclerView.ViewHolder) {

       // itemTouchHelper.startDrag(viewHolder)
    }*/
    override fun getItemCount(): Int {
        return _bMpas.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.image?.setImageBitmap(_bMpas[position].image)
        holder?.txtId?.setText(_bMpas[position].id.toString())
    }


    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.img)
        val txtId = itemView.findViewById<TextView>(R.id.txtItemId)
    }


}