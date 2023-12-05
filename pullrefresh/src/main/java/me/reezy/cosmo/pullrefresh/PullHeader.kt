package me.reezy.cosmo.pullrefresh

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout


abstract class PullHeader @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {


    // 加载状态下的高度
    open val loadingHeight: Int get() = measuredHeight

    // 触发加载的拖动距离比率 (当前拖动距离 / loadingHeight)
    open val loadingPullRate: Float = 1f

    // 最大可拖动距离比率 (最大可拖动距离 / loadingHeight)
    open val maximumPullRate: Float = 2.5f

    // 打开二楼的拖动距离比率，<=loadingPullRate 表示没有二楼
    open val secondPullRate: Float = 0f


    open fun onPulling(distance: Int) {

    }

    abstract fun onStateChanged(oldState: PullState, newState: PullState)
}