package com.demo.app

import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.reezy.cosmo.pullrefresh.PullRefreshLayout
import kotlin.random.Random

class ListViewActivity : AppCompatActivity(R.layout.activity_list_view) {


    val refresh by lazy { findViewById<PullRefreshLayout>(R.id.refresh) }

    val list by lazy { findViewById<ListView>(R.id.list) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = (1..100).map { mapOf("text" to it.toString()) }.toList()
        list.adapter = SimpleAdapter(this, data, android.R.layout.simple_list_item_1, arrayOf("text"), intArrayOf(android.R.id.text1))

        refresh.setOnRefreshListener { 
            lifecycleScope.launch {
                delay(2000)
                refresh.finish(Random(System.currentTimeMillis()).nextBoolean())
            }
        }
        lifecycleScope.launch {
            delay(2000)
//            refresh.refresh()
        }
    }
}