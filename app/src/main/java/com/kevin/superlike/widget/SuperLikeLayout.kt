package com.kevin.superlike.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.kevin.superlike.R
import com.kevin.superlike.support.BitmapProvider
import com.kevin.superlike.support.BitmapProviderFactory
import com.kevin.superlike.support.EruptionEvaluator
import timber.log.Timber
import java.lang.Exception

/**
 * 点赞表情弹射View
 *
 * Create by Kevin-Tu on 2019/6/25.
 */
class SuperLikeLayout : FrameLayout {

    companion object {
        const val DEFAULT_EMOJI_COUNT = 10
        const val DEFAULT_DURATION = 500
        const val DEFAULT_SPEED = 1600
        const val DEFAULT_GRAVITY = 3000
        const val DEFAULT_CACHE_SIZE = 32
    }

    private var mHeight: Int = 0
    private var mWidth: Int = 0
    private var emojiCount: Int = DEFAULT_EMOJI_COUNT // 一次动画的表情总个数
    private var duration: Int = DEFAULT_DURATION // 动画时间
    private var speed: Int = DEFAULT_SPEED // 速度
    private var gravity: Int = DEFAULT_GRAVITY // 重力加速度
    private var cacheSize: Int = DEFAULT_CACHE_SIZE

    private var bitmapProvider: BitmapProvider? = null
    private var animators: ArrayList<Animator> = ArrayList()
    private var numberViewMap: HashMap<View, ImageView> = HashMap()
    private var numberRunnableMap: HashMap<ImageView, Runnable> = HashMap()
    private var numbers: HashMap<View, Int> = HashMap()

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    fun init(context: Context, attrs: AttributeSet?) {
        val typeArray = context.theme.obtainStyledAttributes(attrs, R.styleable.SuperLikeLayout, 0, 0)
        try {
            emojiCount = typeArray.getInt(R.styleable.SuperLikeLayout_emojiCount, DEFAULT_EMOJI_COUNT)
            if (emojiCount < 0) {
                emojiCount = DEFAULT_EMOJI_COUNT
            }
            duration = typeArray.getInt(R.styleable.SuperLikeLayout_duration, DEFAULT_DURATION)
            if (duration < 0) {
                duration = DEFAULT_DURATION
            }
            speed = typeArray.getInt(R.styleable.SuperLikeLayout_speed, DEFAULT_SPEED)
            if (speed < 0) {
                speed = DEFAULT_SPEED
            }
            gravity = typeArray.getInt(R.styleable.SuperLikeLayout_gravity, DEFAULT_GRAVITY)
            if (gravity < 0) {
                gravity = DEFAULT_GRAVITY
            }
            cacheSize = typeArray.getInt(R.styleable.SuperLikeLayout_cacheSize, DEFAULT_CACHE_SIZE)
            if (cacheSize < 0) {
                cacheSize = DEFAULT_CACHE_SIZE
            }
        } catch (e: Exception) {
            Timber.e(e)
        } finally {
            typeArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mHeight = measuredHeight
        mWidth = measuredWidth
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        bitmapProvider?.release()
        numbers.clear()
        numberViewMap.clear()

        try {
            for (index in 0 until animators.size) {
                animators[index].cancel()
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        animators.clear()
    }

    /**
     * 点击事件
     */
    fun launch(view: View) {
        if (bitmapProvider == null) {
            bitmapProvider = BitmapProviderFactory.getDefault(context, cacheSize)
        }
        numbers[view] = 1
        startLaunch(view)
    }

    /**
     * 长按事件
     */
    fun pressLaunch(view: View) {
        bitmapProvider?.let {
            numbers[view] = numbers[view]?.plus(1) ?: 1
            startLaunch(view)
        }
    }

    private fun startLaunch(view: View) {
        var viewPosition = IntArray(2)
        var layoutPosition = IntArray(2)
        view.getLocationOnScreen(viewPosition)
        getLocationOnScreen(layoutPosition)

        drawEmoji(view, viewPosition, layoutPosition)
        drawableNumber(view, viewPosition, layoutPosition)
    }

    /**
     * 绘制数字
     */
    @Synchronized
    private fun drawableNumber(view: View, viewPosition: IntArray, layoutPosition: IntArray) {
        var numBitmap = bitmapProvider?.getNumberBitmap(numbers[view] ?: 1)
        numBitmap?.let {
            var height = resources.getDimensionPixelOffset(R.dimen.super_like_num_and_bg_height)
            var width = height.toFloat() / it.height.toFloat() * it.width
            var marginBottom = resources.getDimensionPixelOffset(R.dimen.super_like_num_and_bg_margin_bottom)
            var layoutParams = LayoutParams(width.toInt(), height)
            var imgX = viewPosition[0] - layoutPosition[0] + view.width - width
            var imgY = viewPosition[1] - layoutPosition[1] - height - marginBottom

            var imageView = ImageView(context)
            imageView.layoutParams = layoutParams
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            imageView.x = imgX
            imageView.y = imgY.toFloat()
            imageView.setImageBitmap(it)

            clearLastNumImgIfNeed(view)

            var runnable = Runnable {
                removeView(imageView)
            }
            imageView.postDelayed(runnable, duration - 50L )
            numberViewMap[view] = imageView
            numberRunnableMap[imageView] = runnable
            addView(imageView)
        }
    }

    /**
     * 清除上一次绘制的number image View
     */
    private fun clearLastNumImgIfNeed(view: View) {
        var lastView = numberViewMap.remove(view)
        lastView?.let { imgView ->
            removeView(imgView)
            var lastRunnable = numberRunnableMap.remove(imgView)
            lastRunnable?.let {
                imgView.removeCallbacks(it)
            }
        }
    }

    /**
     * 绘制弹射的表情
     */
    private fun drawEmoji(view: View, viewPosition: IntArray, layoutPosition: IntArray) {
        var imgWithAndHeight = resources.getDimensionPixelOffset(R.dimen.super_like_btn_launch_emoji_width_and_height)
        var layoutParams = LayoutParams(imgWithAndHeight, imgWithAndHeight)

        var imgX = viewPosition[0] + view.width / 2 - layoutPosition[0] - imgWithAndHeight / 2
        var imgY = viewPosition[1] + view.height / 2 - layoutPosition[1] - imgWithAndHeight / 2

        for (index in 0 until emojiCount) {
            var imageView = ImageView(context)
            var bitmap = bitmapProvider?.getEmojiBitmap(index)
            bitmap?.let {
                imageView.setImageBitmap(bitmap)
                imageView.layoutParams = layoutParams
                imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                imageView.x = imgX.toFloat()
                imageView.y = imgY.toFloat()
                addView(imageView)

                var animator = getEruptionAnimator(imageView)
                animator.addListener(AnimEndLis(imageView))
                animators.add(animator)
                animator.start()
            }
        }
    }

    /**
     * 获取弹射动画
     */
    private fun getEruptionAnimator(target: View): Animator {
        var angle = Math.random() * 360
        var finalSpeed = speed + Math.random() * 400
        var evaluator = EruptionEvaluator(gravity, angle, finalSpeed, duration)
        var endPointF = evaluator.getEndPointF(target.x, target.y)

        var animator = ValueAnimator.ofObject(evaluator, PointF(target.x, target.y), PointF(endPointF.x, endPointF.y))
        animator.duration = duration.toLong()
        animator.setTarget(target)
        animator.addUpdateListener(EruptionLis(target))
        return animator
    }

    inner class EruptionLis(var target: View) : ValueAnimator.AnimatorUpdateListener {
        override fun onAnimationUpdate(animation: ValueAnimator) {
            var pointF = animation.animatedValue
            if (pointF is PointF) {
                target.x = pointF.x
                target.y = pointF.y
                var alpha = 1F - animation.animatedFraction
                if (alpha < 0.5F) { // 最后加一个渐隐的效果
                    target.alpha = alpha + 0.5F
                }
            }
        }
    }

    inner class AnimEndLis(var target: View) : AnimatorListenerAdapter() {

        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            // 动画结束后移除添加的View
            removeView(target)
            animators.remove(animation)
        }
    }
}