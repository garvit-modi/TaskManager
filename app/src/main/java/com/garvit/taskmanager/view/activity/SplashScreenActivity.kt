package com.garvit.taskmanager.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.garvit.taskmanager.R
import com.garvit.taskmanager.databinding.ActivitySplashScreenBinding


class SplashScreenActivity : AppCompatActivity() {
    lateinit var binding : ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)

        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.side_slide)
        binding.imgLogo.startAnimation(slideAnimation)
        binding.textName.startAnimation(slideAnimation)
        val outMetrics = DisplayMetrics()
        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        Handler().postDelayed({
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000) // 3000 is the delayed time in millisecond
    }
}