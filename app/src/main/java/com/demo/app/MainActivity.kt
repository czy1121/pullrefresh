package com.demo.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import com.demo.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    val binding by lazy { ActivityMainBinding.bind(findViewById<ViewGroup>(android.R.id.content).getChildAt(0)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        binding.btnNestedScroll.setOnClickListener {
            startActivity(Intent(this, NestedScrollActivity::class.java))
        }
        binding.btnRecyclerView.setOnClickListener {
            startActivity(Intent(this, RecyclerViewActivity::class.java))
        }
        binding.btnSecondFloor.setOnClickListener {
            startActivity(Intent(this, SecondFloorActivity::class.java))
        }

        binding.btnCoordinatorChild.setOnClickListener {
            startActivity(Intent(this, CoordinatorChildActivity::class.java))
        }

        binding.btnCoordinatorParent.setOnClickListener {
            startActivity(Intent(this, CoordinatorParentActivity::class.java))
        }

        binding.btnScrollView.setOnClickListener {
            startActivity(Intent(this, ScrollViewActivity::class.java))
        }

        binding.btnListView.setOnClickListener {
            startActivity(Intent(this, ListViewActivity::class.java))
        }

        binding.btnWebView.setOnClickListener {
            startActivity(Intent(this, WebViewActivity::class.java))
        }
    }
}