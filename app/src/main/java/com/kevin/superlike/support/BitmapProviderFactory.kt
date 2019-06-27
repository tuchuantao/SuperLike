package com.kevin.superlike.support

import android.content.Context
import com.kevin.superlike.R

/**
 * Create by Kevin-Tu on 2019/6/25.
 */
class BitmapProviderFactory {

    companion object {

        fun getDefault(context: Context, cacheSize: Int): BitmapProvider {
            return DefaultBimapProvider.Builder(context)
                .setCacheSize(cacheSize)
                .setEmojiDrawableArr(
                    intArrayOf(
                        R.mipmap.emoji_1,
                        R.mipmap.emoji_2,
                        R.mipmap.emoji_3,
                        R.mipmap.emoji_4,
                        R.mipmap.emoji_5,
                        R.mipmap.emoji_6,
                        R.mipmap.emoji_7,
                        R.mipmap.emoji_8,
                        R.mipmap.emoji_9,
                        R.mipmap.emoji_10
                    )
                )
                .setNumberDrawableArr(
                    intArrayOf(
                        R.mipmap.icon_number_zero,
                        R.mipmap.icon_number_one,
                        R.mipmap.icon_number_two,
                        R.mipmap.icon_number_three,
                        R.mipmap.icon_number_four,
                        R.mipmap.icon_number_five,
                        R.mipmap.icon_number_six,
                        R.mipmap.icon_number_seven,
                        R.mipmap.icon_number_eight,
                        R.mipmap.icon_number_nine
                    )
                )
                .setStimulateDrawable(R.mipmap.icon_stimulate)
                .build()
        }
    }
}