package com.kevin.superlike.support

import android.graphics.Bitmap

/**
 * Create by Kevin-Tu on 2019/6/25.
 */
interface BitmapProvider {

    fun getEmojiRandomBitmap(): Bitmap?

    fun getEmojiBitmap(index: Int): Bitmap?

    fun getNumberBitmap(number: Int): Bitmap?

    fun release()
}