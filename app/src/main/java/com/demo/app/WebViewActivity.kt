package com.demo.app

import android.net.http.SslError
import android.os.Bundle
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.reezy.cosmo.pullrefresh.PullRefreshLayout
import kotlin.random.Random

class WebViewActivity : AppCompatActivity(R.layout.activity_web_view) {


    private val refresh by lazy { findViewById<PullRefreshLayout>(R.id.refresh) }

    private val web by lazy { findViewById<WebView>(R.id.web) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        web.initWebSettings()
        web.loadUrl("https://juejin.cn")

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


    private fun WebView.initWebSettings() {

        // 存储(storage)
        settings.domStorageEnabled = true
        settings.databaseEnabled = true

        // 定位(location)
        settings.setGeolocationEnabled(true)

        // 缩放(zoom)
        settings.setSupportZoom(true)
        settings.builtInZoomControls = false
        settings.displayZoomControls = false

        // 文件权限
        settings.allowContentAccess = true
        settings.allowFileAccess = true

        //
        settings.textZoom = 100

        // 支持Javascript
        settings.javaScriptEnabled = true

        // 支持https
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        // 页面自适应手机屏幕，支持viewport属性
        settings.useWideViewPort = true
        // 缩放页面，使页面宽度等于WebView宽度
        settings.loadWithOverviewMode = true

        // 是否自动加载图片
        settings.loadsImagesAutomatically = true
        // 禁止加载网络图片
        settings.blockNetworkImage = false
        // 禁止加载所有网络资源
        settings.blockNetworkLoads = false
//        settings.allowUniversalAccessFromFileURLs = false
    }

}