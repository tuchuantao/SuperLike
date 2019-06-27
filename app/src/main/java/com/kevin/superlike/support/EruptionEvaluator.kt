package com.kevin.superlike.support

import android.graphics.PointF

/**
 * Create by Kevin-Tu on 2019/6/25.
 */
class EruptionEvaluator: BaseEvaluator<PointF> {

    private var gravity: Int // 重力加速度
    private var xSpeed: Double
    private var ySpeed: Double
    private var duration: Int

    constructor(gravity: Int, angle: Double, speed: Double, duration: Int) {
        this.gravity = gravity
        xSpeed = speed * Math.cos(angle * Math.PI / 180)
        ySpeed = -speed * Math.sin(angle * Math.PI / 180)
        this.duration = duration
    }

    override fun evaluate(fraction: Float, startValue: PointF, endValue: PointF): PointF {
        return getPoint(fraction * duration, startValue.x, startValue.y)
    }

    override fun getEndPointF(startX: Float, startY: Float): PointF {
        return getPoint(duration.toFloat(), startX, startY)
    }

    private fun getPoint(time: Float, startX: Float, startY: Float) : PointF {
        var secondTime = time / 1000F
        var point = PointF()
        point.x = (startX + xSpeed * secondTime).toFloat()
        point.y = (startY + (ySpeed * secondTime) + (gravity * secondTime * secondTime) / 2).toFloat()
        return point
    }
}