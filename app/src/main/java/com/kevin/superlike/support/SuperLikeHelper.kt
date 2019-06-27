package com.kevin.superlike.support

import android.os.CountDownTimer
import android.view.View
import com.kevin.superlike.widget.LikeBtnListener
import com.kevin.superlike.widget.SuperLikeButton
import com.kevin.superlike.widget.SuperLikeLayout

class SuperLikeHelper {

    private var countDownTimer: CountDownTimer? = null
    private var superLikeLayout: SuperLikeLayout
    private lateinit var likeListener: LikeBtnListener

    constructor(superLikeLayout: SuperLikeLayout, superLikeButton: SuperLikeButton) {
        this.superLikeLayout = superLikeLayout
        initListener()
        superLikeButton.setListener(likeListener)
    }

    private fun initListener() {
        likeListener = object : LikeBtnListener {
            override fun onDown(view: View) {
                superLikeLayout.launch(view)
                startCountTimer(view)
            }

            override fun onUp(view: View) {
                countDownTimer?.cancel()
                countDownTimer = null
            }

            override fun onFastDown(view: View) {
                superLikeLayout.pressLaunch(view)
                startCountTimer(view)
            }
        }
    }

    private fun startCountTimer(view: View) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(Long.MAX_VALUE, 80L) {
            override fun onFinish() {
            }

            override fun onTick(millisUntilFinished: Long) {
                superLikeLayout.pressLaunch(view)
            }
        }
        superLikeLayout.postDelayed({
            countDownTimer?.start()
        }, 80L)
    }
}