package com.kevin.superlike.support

import android.animation.TypeEvaluator
import android.graphics.PointF

/**
 * Create by Kevin-Tu on 2019/6/25.
 */
abstract class BaseEvaluator<T>: TypeEvaluator<T> {

    /**
     * 获取最终点
     */
    abstract fun getEndPointF(startX: Float, startY: Float): PointF
}