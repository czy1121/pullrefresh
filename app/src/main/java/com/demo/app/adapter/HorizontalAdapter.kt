package com.demo.app.adapter

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HorizontalAdapter: RecyclerView.Adapter<HorizontalAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view)


    override fun getItemCount(): Int = 10

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TextView(parent.context).apply {
            val size = (100 * resources.displayMetrics.density).toInt()
            layoutParams = ViewGroup.LayoutParams(size, size)
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
        })
    }

    private val colors = arrayOf(Color.BLUE, Color.RED, Color.MAGENTA, Color.GREEN, Color.DKGRAY, Color.CYAN)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder.itemView as TextView).apply {
            text = "$position"
            setBackgroundColor(colors[position % colors.size])
        }
    }
}