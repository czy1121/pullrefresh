package me.reezy.cosmo.pullrefresh

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Scroller
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import me.reezy.cosmo.pullrefresh.PullRefreshLayout.PullChecker
import kotlin.math.abs
import kotlin.math.min


class PullRefreshLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    ViewGroup(context, attrs, defStyle), NestedScrollingParent3, NestedScrollingChild3 {

    companion object {
        private var defaultHeaderFactory: HeaderFactory? = null

        fun setDefaultHeaderFactory(factory: HeaderFactory) {
            defaultHeaderFactory = factory
        }
    }

    //<editor-fold desc="接口定义">
    fun interface HeaderFactory {
        fun create(parent: PullRefreshLayout): PullHeader
    }

    fun interface PullChecker {
        fun canPull(): Boolean
    }

    fun interface OnRefreshListener {
        fun onRefresh()
    }

    fun interface OnSecondFloorListener {
        fun onSecondFloor()
    }

    fun interface OnPullingListener {
        fun onPulling(distance: Int)
    }

    fun interface OnStateChangedListener {
        fun onStateChanged(oldState: PullState, newState: PullState)
    }
    //</editor-fold>


    private lateinit var header: PullHeader
    private lateinit var content: View

    private var onStateChangedListener: OnStateChangedListener? = null
    private var onPullingListener: OnPullingListener? = null
    private var onRefreshListener: OnRefreshListener? = null
    private var onSecondFloorListener: OnSecondFloorListener? = null

    //<editor-fold desc="开放接口">

    fun setStateChangedListener(listener: OnStateChangedListener) {
        onStateChangedListener = listener
    }

    fun setOnPullingListener(listener: OnPullingListener) {
        onPullingListener = listener
    }

    fun setOnRefreshListener(listener: OnRefreshListener) {
        onRefreshListener = listener
    }

    fun setOnSecondFloorListener(listener: OnSecondFloorListener) {
        onSecondFloorListener = listener
    }

    fun setPullChecker(checker: PullChecker) {
        pullChecker = checker
    }

    fun setHeader(view: PullHeader) {
        if (this::header.isInitialized) {
            super.removeView(header)
        }
        super.addView(view)
        header = view
    }

    fun switchSecondFloor(isOpen: Boolean) {
        if (!this::header.isInitialized) return
        if (isOpen) {
            setState(PullState.SecondFloor)
        } else if (state == PullState.SecondFloor) {
            setState(PullState.None)
        }
    }

    fun refresh() {
        if (!this::header.isInitialized) return
        if (state != PullState.Loading) {
            setState(PullState.Loading)
        }
    }

    fun finish(success: Boolean = true) {
        if (!this::header.isInitialized) return
        if (state == PullState.Loading) {
            setState(if (success) PullState.Loaded else PullState.Failed)
        }
    }


    //</editor-fold>

    //<editor-fold desc="状态变化">
    private var state: PullState = PullState.None
    private fun setState(newState: PullState) {
        if (state == newState) return
        val oldState = state
        state = newState

//        log("onStateChanged($oldState, $newState)")

        when (newState) {
            PullState.None -> {
                startOffsetTo(0)
            }
            PullState.Pulling, PullState.Ready, PullState.SecondReady -> {}
            PullState.Loading -> {
                startOffsetTo(header.loadingHeight)
                onRefreshListener?.onRefresh()
            }
            PullState.Loaded -> {
                postDelayed(1000) {
                    setState(PullState.None)
                }
            }
            PullState.Failed -> {
                postDelayed(1000) {
                    setState(PullState.None)
                }
            }
            PullState.SecondFloor -> {
                startOffsetTo(measuredHeight)
                onSecondFloorListener?.onSecondFloor()
            }
        }

        mInterceptRequestLayout = true
        header.onStateChanged(oldState, newState)
        mInterceptRequestLayout = false

        onStateChangedListener?.onStateChanged(oldState, newState)
    }

    private var mInterceptRequestLayout = false

    override fun requestLayout() {
        if (!mInterceptRequestLayout) {
            super.requestLayout()
        }
    }
    //</editor-fold>

    //<editor-fold desc="拖动逻辑">
    private var pullDistance: Int = 0

    // pull down/up，计算消耗的滚动距离
    private fun pull(dy: Int, isPullDown: Boolean): Int {
        if (isPullDown && dy < 0 && !content.canScrollVertically(-1)) { // pull down
            val consumedDy = -min(-(pullDistance - header.loadingHeight * header.maximumPullRate).toInt(), -dy)
            if (consumedDy != 0) {
                pullTo(pullDistance - consumedDy)
                return consumedDy
            }
        } else if (!isPullDown && dy > 0) { // pull up
            val consumedDy = min(dy, pullDistance)
            if (consumedDy != 0) {
                pullTo(pullDistance - consumedDy)
                return consumedDy
            }
        }
        return 0
    }

    private fun pullTo(distance: Int) {
//        log("onPulling($distance)")
        offsetTo(distance)

        val pullRate = pullDistance.toFloat() / header.loadingHeight
        val newState = when {
            pullRate < header.loadingPullRate -> PullState.Pulling
            header.secondPullRate <= header.loadingPullRate || pullRate < header.secondPullRate -> PullState.Ready
            else -> PullState.SecondReady
        }
        setState(newState)
    }

    private fun offsetTo(distance: Int) {
        pullDistance = distance
        scrollTo(0, -pullDistance)
//        header.translationY = pullDistance.toFloat()
//        content.translationY = pullDistance.toFloat()

        header.onPulling(distance)
        onPullingListener?.onPulling(distance)
    }

    private fun pulled() {
//        log("onPulled, state = $state")
        when (state) {
            PullState.Pulling -> setState(PullState.None)
            PullState.Ready -> setState(PullState.Loading)
            PullState.SecondReady -> setState(PullState.SecondFloor)
            else -> {}
        }
    }


    private val scroller = Scroller(context)
    private fun startOffsetTo(endY: Int) {
        val startY = pullDistance
        val dy = endY - startY
        val duration = abs(dy).coerceIn(300, 600)
        scroller.startScroll(0, startY, 0, dy, duration)
        postInvalidate()
    }

    override fun computeScroll() {
        if (!scroller.isFinished && scroller.computeScrollOffset()) {
            offsetTo(scroller.currY)
            postInvalidateOnAnimation()
        }
    }
    //</editor-fold>

    //<editor-fold desc="触摸事件实现下拉刷新">
    private var pullChecker: PullChecker = PullChecker { !content.canScrollVertically(-1) }
    private var isPulling = false
    private var lastY = 0f

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!this::content.isInitialized || content is NestedScrollingChild || state == PullState.Loading) {
            return super.dispatchTouchEvent(ev)
        }
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = ev.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaY = ev.rawY - lastY
                lastY = ev.rawY
                isPulling = pullChecker.canPull() && (pullDistance > 0 || pullDistance + deltaY > 0)
                if (isPulling) {
                    pull(-deltaY.toInt(), deltaY > 0)
                    if (deltaY < 0) {
                        return true
                    }
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (isPulling) {
                    isPulling = false
                    pulled()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
    //</editor-fold>

    //<editor-fold desc="嵌套滚动 - NestedScrollingParent">
    private val parentHelper = NestedScrollingParentHelper(this)
    private val consumed = intArrayOf(0, 0)

    override fun getNestedScrollAxes(): Int {
        return parentHelper.nestedScrollAxes
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return onStartNestedScroll(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        onNestedScrollAccepted(child, target, axes, ViewCompat.TYPE_TOUCH)
    }

    override fun onStopNestedScroll(child: View) {
        onStopNestedScroll(child, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        consumed[1] = 0
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, ViewCompat.TYPE_TOUCH, consumed)
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        consumed[1] = 0
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return false
    }
    //</editor-fold>

    //<editor-fold desc="嵌套滚动 - NestedScrollingParent2">

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
//        log("start-nested-scroll => ${child.javaClass.simpleName}, ${target.javaClass.simpleName}, $type")
        return child is NestedScrollingChild && (axes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
//        log("accept-nested-scroll => ${child.javaClass.simpleName}, ${target.javaClass.simpleName}, $type")
        parentHelper.onNestedScrollAccepted(child, target, axes, type)
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
//        log("stop-nested-scroll =>  ${target.javaClass.simpleName}, $type")
        parentHelper.onStopNestedScroll(target, type)
        stopNestedScroll(type)
        if (type != ViewCompat.TYPE_TOUCH) return
        pulled()
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        val oldY = consumed[1]
        childHelper.dispatchNestedPreScroll(dx, dy, consumed, null, type)
        val unconsumed = dy - (consumed[1] - oldY)
        if (type == ViewCompat.TYPE_TOUCH && unconsumed > 0) {
            // pull up
            consumed[1] += pull(unconsumed, false)
        }
    }

    //</editor-fold>

    //<editor-fold desc="嵌套滚动 - NestedScrollingParent3">

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int, consumed: IntArray) {
        val consumedY = consumed[1]
        childHelper.dispatchNestedScroll(0, 0, 0, dyUnconsumed, null, type, consumed)
        val unconsumed = dyUnconsumed - (consumed[1] - consumedY)
        if (type == ViewCompat.TYPE_TOUCH && unconsumed < 0) {
            // pull down，计算剩余可消耗的滚动距离
            consumed[1] += pull(unconsumed, true)
        }
    }
    //</editor-fold>

    //<editor-fold desc="嵌套滚动 - NestedScrollingChild">
    private val childHelper = NestedScrollingChildHelper(this).apply {
        isNestedScrollingEnabled = true
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return childHelper.isNestedScrollingEnabled
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        childHelper.isNestedScrollingEnabled = enabled
    }

    override fun hasNestedScrollingParent(): Boolean {
        return hasNestedScrollingParent(ViewCompat.TYPE_TOUCH)
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return startNestedScroll(axes, ViewCompat.TYPE_TOUCH)
    }

    override fun stopNestedScroll() {
        stopNestedScroll(ViewCompat.TYPE_TOUCH)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?): Boolean {
        return dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, ViewCompat.TYPE_TOUCH)
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?): Boolean {
        return dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, ViewCompat.TYPE_TOUCH)
    }
    //</editor-fold>

    //<editor-fold desc="嵌套滚动 - NestedScrollingChild2">
    override fun hasNestedScrollingParent(type: Int): Boolean {
        return childHelper.hasNestedScrollingParent(type)
    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return childHelper.startNestedScroll(axes, type)
    }

    override fun stopNestedScroll(type: Int) {
        childHelper.stopNestedScroll(type)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?, type: Int): Boolean {
        return childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int): Boolean {
        return childHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type)
    }
    //</editor-fold>

    //<editor-fold desc="嵌套滚动 - NestedScrollingChild3">
    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int, consumed: IntArray) {
        return childHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type, consumed)
    }
    //</editor-fold>


    //<editor-fold desc="生命周期">
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (childCount > 2) {
            throw RuntimeException("Most only support two child view")
        }

        forEach {
            if (it is PullHeader) {
                header = it
            } else {
                content = it
            }
        }

        if (!this::content.isInitialized) {
            val view = TextView(context)
            view.setTextColor(Color.RED)
            view.gravity = Gravity.CENTER
            view.textSize = 20f
            view.text = "找不到内容视图！"
            content = view
            super.addView(content, 0)
        }

        if (!this::header.isInitialized) {
            header = defaultHeaderFactory?.create(this) ?: EmptyHeader(context)
            super.addView(header)
        }

        if (content is CoordinatorLayout) {
            pullChecker = CoordinatorLayoutChecker(content as CoordinatorLayout)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        childHelper.onDetachedFromWindow()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)
    }

    override fun measureChild(child: View, parentWidthMeasureSpec: Int, parentHeightMeasureSpec: Int) {
        val msw = getChildMeasureSpec(parentWidthMeasureSpec, 0, LayoutParams.MATCH_PARENT)
        val msh = getChildMeasureSpec(parentHeightMeasureSpec, paddingTop + paddingBottom, LayoutParams.MATCH_PARENT)
        child.measure(msw, msh)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val w = measuredWidth
        val h = measuredHeight //- appbarOffset

        header.layout(0, -h, w, 0)
        content.layout(0, paddingTop, w, h - paddingTop - paddingBottom)

        if (state == PullState.None) {
            offsetTo(0)
        }
    }


    //</editor-fold>


    private fun log(message: String) {
        Log.e("OoO", message)
    }


    private class EmptyHeader(context: Context) : PullHeader(context) {
        init {
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (resources.displayMetrics.density * 60).toInt())
        }

        override fun onStateChanged(oldState: PullState, newState: PullState) {

        }
    }

    private class CoordinatorLayoutChecker(view: CoordinatorLayout) : PullChecker {
        private var offset = 0

        private val onOffsetChangedListener = OnOffsetChangedListener { _, offset ->
            this.offset = offset
        }

        init {
            view.forEach {
                if (it is AppBarLayout) {
                    it.addOnOffsetChangedListener(onOffsetChangedListener)
                }
            }
        }

        override fun canPull(): Boolean {
            return offset == 0
        }
    }
}