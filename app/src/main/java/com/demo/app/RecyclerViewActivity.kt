package com.demo.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.demo.app.adapter.VerticalAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.reezy.cosmo.pullrefresh.PullRefreshLayout
import kotlin.random.Random

class RecyclerViewActivity : AppCompatActivity() {

    val refresh by lazy { findViewById<PullRefreshLayout>(R.id.refresh) }

    val rv by lazy { findViewById<RecyclerView>(R.id.rv) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)

        rv.adapter = VerticalAdapter()

        refresh.setOnRefreshListener {
            lifecycleScope.launch {
                delay(2000)
                refresh.finish(Random(System.currentTimeMillis()).nextBoolean())
            }
        }
    }
}