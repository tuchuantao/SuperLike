package com.kevin.superlike.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout

class SuperLikeButton : FrameLayout {

    private var listener: LikeBtnListener? = null
    private var lastUpTime = 0L

    companion object {
        const val FAST_CLICK_INTERVAL = 800L
    }

    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setListener(listener: LikeBtnListener) {
        this.listener = listener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                requestDisallowInterceptTouchEvent(true)
                if (System.currentTimeMillis() - lastUpTime < FAST_CLICK_INTERVAL) {
                    listener?.onFastDown(this)
                } else {
                    listener?.onDown(this)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                requestDisallowInterceptTouchEvent(false)
                lastUpTime = System.currentTimeMillis()
                listener?.onUp(this)
            }
        }
        return true
    }
}

interface LikeBtnListener {

    fun onDown(view: View)

    fun onUp(view: View)

    fun onFastDown(view: View)
}