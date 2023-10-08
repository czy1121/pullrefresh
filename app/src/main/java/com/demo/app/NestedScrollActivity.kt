package com.demo.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.demo.app.adapter.VerticalAdapter
import com.demo.app.databinding.ActivityNestedScrollBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class NestedScrollActivity : AppCompatActivity() {

    val binding by lazy { ActivityNestedScrollBinding.bind(findViewById<ViewGroup>(android.R.id.content).getChildAt(0)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nested_scroll)

        binding.rv.adapter = VerticalAdapter()

        binding.rv.setRecyclerListener {
            Log.e("OoO", "recycle $it")
        }

        binding.refresh.setOnRefreshListener {
            lifecycleScope.launch {
                delay(2000)
                binding.refresh.finish(Random(System.currentTimeMillis()).nextBoolean())
            }
        }

        binding.btnLoading.setOnClickListener {
            binding.refresh.refresh()
        }
    }
}