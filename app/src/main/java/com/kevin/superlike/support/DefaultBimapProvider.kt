package com.kevin.superlike.support

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.support.annotation.DrawableRes
import android.util.LruCache
import com.kevin.superlike.R

/**
 * Create by Kevin-Tu on 2019/6/25.
 */
class DefaultBimapProvider : BitmapProvider {

    companion object {
        private const val NUMBER_PREFIX = 0x60000000
        private const val STIMULATE_PREFIX = 0x70000000
        private const val MIN_CACHE_SIZE = 16
    }

    private var bitmapCache: LruCache<Int, Bitmap>
    @DrawableRes
    private var emojiDrawableArr: IntArray
    @DrawableRes
    private var numberDrawableArr: IntArray
    @DrawableRes
    private var stimulateDrawable: Int?
    private var appContext: Context

    constructor(
        context: Context, cacheSize: Int,
        @DrawableRes emojiDrawableArr: IntArray,
        @DrawableRes numberDrawableArr: IntArray,
        @DrawableRes stimulateDrawable: Int?
    ) {
        this.appContext = context.applicationContext
        this.emojiDrawableArr = emojiDrawableArr
        this.numberDrawableArr = numberDrawableArr
        this.stimulateDrawable = stimulateDrawable

        if (cacheSize < MIN_CACHE_SIZE) {
            bitmapCache = LruCache(MIN_CACHE_SIZE)
        } else {
            bitmapCache = LruCache(cacheSize)
        }
    }

    /**
     * 随机选取emoji表情bitmap
     */
    override fun getEmojiRandomBitmap(): Bitmap? {
        var index = (Math.random() * emojiDrawableArr.size).toInt()
        return getEmojiBitmap(index)
    }

    /**
     * 获取emoji表情bitmap
     */
    override fun getEmojiBitmap(index: Int): Bitmap? {
        var bitmap: Bitmap? = null
        if (emojiDrawableArr.isNotEmpty()) {
            var realIndex = index % emojiDrawableArr.size // 防止IndexOutOfBoundsException
            var key = emojiDrawableArr[realIndex]
            bitmap = bitmapCache.get(key)
            if (bitmap == null) { // 内存中未有缓存
                bitmap = BitmapFactory.decodeResource(appContext.resources, emojiDrawableArr[realIndex])
                bitmapCache.put(key, bitmap)
            }
        }
        return bitmap
    }

    /**
     * 获取数字的Bitmap
     */
    override fun getNumberBitmap(number: Int): Bitmap? {
        var bitmap: Bitmap? = null
        if (number > 0) {
            // 获取数字的bitmap列表
            var numBitmaps = getNumBitmaps(number)
            // 获取数字背景的bitmap
            var stimulateBitmap = getStimulateBitmap()

            // 组装Bitmap
            // 1、计算最终画布的宽高
            var width = 0
            var height = 0
            numBitmaps.forEach {
                width += it.width
                height = Math.max(height, it.height)
            }
            var numLeft = 0F
            var stimulateLeft = 0F
            var overlap = appContext.resources.getDimensionPixelOffset(R.dimen.super_like_num_and_bg_overlap_width)
            var stimulateMarginTop = appContext.resources.getDimensionPixelOffset(R.dimen.super_like_num_bg_margin_top)
            stimulateBitmap?.let {
                if (overlap > it.width) {
                    overlap = it.width
                }
                if (width > overlap) {
                    stimulateLeft = (width - overlap).toFloat()
                    width += it.width - overlap
                } else {
                    numLeft = (overlap - width).toFloat()
                    width = it.width
                }
                height = Math.max(height, it.height) + stimulateMarginTop
            }

            // 2、绘制Bitmap
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
            var canvas = Canvas(bitmap!!)
            // 绘制背景
            stimulateBitmap?.let {
                canvas.drawBitmap(it, stimulateLeft, stimulateMarginTop.toFloat(), null)
            }
            // 绘制数字
            numBitmaps.forEach {
                canvas.drawBitmap(it, numLeft, 0F, null)
                numLeft += it.width
            }
        }
        return bitmap
    }

    /**
     * 根据数字获取相应数字Bitmap列表
     */
    private fun getNumBitmaps(number: Int) : ArrayList<Bitmap> {
        var numBitmaps = ArrayList<Bitmap>()
        number.toString().toCharArray().forEach {
            var realNum = it - '0'
            var index = realNum % numberDrawableArr.size // 防止IndexOutOfBoundsException
            var key = numberDrawableArr[index] or NUMBER_PREFIX
            var numBitmap = bitmapCache.get(key)
            if (numBitmap == null) {
                numBitmap = BitmapFactory.decodeResource(appContext.resources, numberDrawableArr[index])
                bitmapCache.put(key, numBitmap)
            }
            numBitmaps.add(numBitmap)
        }
        return numBitmaps
    }

    /**
     * 获取数字的激励背景Bitmap
     */
    private fun getStimulateBitmap(): Bitmap? {
        var bitmap: Bitmap? = null
        stimulateDrawable?.let {
            var key = it or STIMULATE_PREFIX
            bitmap = bitmapCache.get(key)
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(appContext.resources, it)
                bitmapCache.put(key, bitmap)
            }
        }
        return bitmap
    }

    override fun release() {
        bitmapCache.evictAll()
    }

    class Builder {

        private var context: Context
        private var cacheSize: Int = MIN_CACHE_SIZE
        @DrawableRes
        private var emojiDrawableArr: IntArray = IntArray(0)
        @DrawableRes
        private var numberDrawableArr: IntArray = IntArray(0)
        @DrawableRes
        private var stimulateDrawable: Int? = null

        constructor(context: Context) {
            this.context = context
        }

        fun setCacheSize(cacheSize: Int): Builder {
            this.cacheSize = cacheSize
            return this
        }

        fun setEmojiDrawableArr(@DrawableRes emojiDrawableArr: IntArray): Builder {
            this.emojiDrawableArr = emojiDrawableArr
            return this
        }

        fun setNumberDrawableArr(@DrawableRes numberDrawableArr: IntArray): Builder {
            this.numberDrawableArr = numberDrawableArr
            return this
        }

        fun setStimulateDrawable(@DrawableRes stimulateDrawable: Int): Builder {
            this.stimulateDrawable = stimulateDrawable
            return this
        }

        fun build(): BitmapProvider {
            return DefaultBimapProvider(
                context,
                cacheSize,
                emojiDrawableArr,
                numberDrawableArr,
                stimulateDrawable
            )
        }
    }
}