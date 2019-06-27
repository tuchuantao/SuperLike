package com.kevin.superlike

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.kevin.superlike.databinding.ActivityMainBinding
import com.kevin.superlike.support.SuperLikeHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_main, null, false)

        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        title = "仿头条点赞弹射动画"

        SuperLikeHelper(binding.superLikeLayout, binding.superLikeBtn)
    }
}
