package com.demo.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.demo.app.adapter.HorizontalAdapter
import com.demo.app.adapter.VerticalAdapter
import com.demo.app.databinding.ActivityCoordinatorChildBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class CoordinatorChildActivity : AppCompatActivity(R.layout.activity_coordinator_child) {

    val binding by lazy { ActivityCoordinatorChildBinding.bind(findViewById<ViewGroup>(android.R.id.content).getChildAt(0)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.rv.adapter = VerticalAdapter()

        binding.hrv.adapter = HorizontalAdapter()

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