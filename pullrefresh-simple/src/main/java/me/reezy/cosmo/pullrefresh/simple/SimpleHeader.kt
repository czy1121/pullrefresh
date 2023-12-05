package me.reezy.cosmo.pullrefresh.simple

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.*
import me.reezy.cosmo.pullrefresh.PullHeader
import me.reezy.cosmo.pullrefresh.PullState


open class SimpleHeader @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : PullHeader(context, attrs, defStyleAttr) {

    private val vHeader: ViewGroup by lazy { findViewById(R.id.header)!! }
    private val vImage: ImageView by lazy { findViewById(R.id.image)!! }
    private val vText: TextView by lazy { findViewById(R.id.text)!! }

    private var vSecondFloor: View? = null

    override val loadingHeight: Int get() = vHeader.measuredHeight
    override val maximumPullRate: Float get() = if (vSecondFloor == null) 2.5f else 4f
    override val secondPullRate: Float get() = if (vSecondFloor == null) 0f else 2f

    var textColor: Int = Color.BLACK

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SimpleHeader)
        textColor = a.getColor(R.styleable.SimpleHeader_android_textColor, Color.BLACK)
        a.recycle()
        inflate(context, R.layout.prl_simple_header, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        vSecondFloor = children.find { it != vHeader }

        vText.setTextColor(textColor)
        vImage.imageTintList = ColorStateList.valueOf(textColor)
    }

    override fun onStateChanged(oldState: PullState, newState: PullState) {

        vHeader.isVisible = newState != PullState.SecondFloor && newState != PullState.None
        vImage.isVisible = newState != PullState.SecondFloor && newState != PullState.SecondReady

        if (newState == PullState.Loading) {
            vImage.play()
        } else {
            vImage.stop()
        }

        when (newState) {
            PullState.Pulling -> vText.setText(R.string.prl_simple_header_pulling)
            PullState.Ready -> vText.setText(R.string.prl_simple_header_ready)
            PullState.Loading -> vText.setText(R.string.prl_simple_header_loading)
            PullState.Loaded -> vText.setText(R.string.prl_simple_header_loaded)
            PullState.Failed -> vText.setText(R.string.prl_simple_header_failed)
            PullState.SecondReady -> vText.setText(R.string.prl_simple_header_second_ready)
            else -> {}
        }
    }


    private val spinner by lazy { createAnimation() }

    private fun ImageView.play() {
        val d = drawable ?: return
        if (d is Animatable) {
            clearAnimation()
            d.start()
        } else {
            startAnimation(spinner)
        }
    }

    private fun ImageView.stop() {
        val d = drawable ?: return
        if (d is Animatable) {
            d.stop()
        }
        clearAnimation()
    }

    private fun createAnimation(): Animation {
        val spinner = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        spinner.repeatCount = Animation.INFINITE
        spinner.duration = 1000
        spinner.interpolator = Interpolator { (it * 12).toInt() / 12f }
        return spinner
    }
}