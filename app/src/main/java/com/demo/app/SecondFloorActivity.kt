package com.demo.app

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.demo.app.adapter.VerticalAdapter
import com.demo.app.databinding.ActivitySecondFloorBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class SecondFloorActivity : AppCompatActivity(R.layout.activity_second_floor) {

    val binding by lazy { ActivitySecondFloorBinding.bind(findViewById<ViewGroup>(android.R.id.content).getChildAt(0)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding.btnClose.setOnClickListener {
            binding.refresh.switchSecondFloor(false)
        }

        binding.btnTest.setOnClickListener {
            Toast.makeText(this, "test clicked", Toast.LENGTH_SHORT).show()
        }

        binding.rv.adapter = VerticalAdapter()

        binding.refresh.setOnRefreshListener {
            lifecycleScope.launch {
                delay(2000)
                binding.refresh.finish(Random(System.currentTimeMillis()).nextBoolean())
            }
        }

        binding.refresh.setOnPullingListener {
            binding.secondFloor.translationY = (binding.secondFloor.height - it) / 2f
        }
    }
}