package com.demo.app.adapter

import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VerticalAdapter: RecyclerView.Adapter<VerticalAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view)



    override fun getItemCount(): Int = 61

    var count = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.e("OoO", "create => ${count++}")
        return ViewHolder(TextView(parent.context).apply {
            val h = (50 * resources.displayMetrics.density).toInt()
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, h)
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
        })
    }

    private val colors = arrayOf(Color.BLUE, Color.RED, Color.MAGENTA, Color.GREEN, Color.DKGRAY, Color.CYAN, Color.YELLOW)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder.itemView as TextView).apply {
            text = "rv item $position"
            setBackgroundColor(colors[position % colors.size])

            val dp = if (position % 5 == 3) 80 else 50
            val h = (dp * resources.displayMetrics.density).toInt()
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, h)
        }
    }
}