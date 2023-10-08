package com.demo.app

import android.app.Application
import me.reezy.cosmo.pullrefresh.PullRefreshLayout
import me.reezy.cosmo.pullrefresh.simple.SimpleHeader

class App: Application() {

    override fun onCreate() {
        super.onCreate()


        PullRefreshLayout.setDefaultHeaderFactory {
            SimpleHeader(it.context)
        }
    }
}