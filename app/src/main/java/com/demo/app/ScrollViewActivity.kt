package com.demo.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.reezy.cosmo.pullrefresh.PullRefreshLayout
import me.reezy.cosmo.pullrefresh.simple.SimpleHeader
import kotlin.random.Random

class ScrollViewActivity : AppCompatActivity() {


    val refresh by lazy { findViewById<PullRefreshLayout>(R.id.refresh) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scroll_view)


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