package yuvabharat.ramu.bloodonator

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({
            val centerX = (FillColor.left + FillColor.width) / 2
            val centerY = (FillColor.top + FillColor.height) / 2
            val dx = Math.max(centerX, FillColor.width - centerX)
            val dy = Math.max(centerY, FillColor.height - centerY)
            val radius = Math.hypot(dx.toDouble(), dy.toDouble())
            if (Build.VERSION.SDK_INT >= 21) {
                val reveal = ViewAnimationUtils.createCircularReveal(FillColor, centerX, centerY, 10f, radius.toFloat())
                reveal.duration = 700
                reveal.interpolator = AccelerateDecelerateInterpolator()
                FillColor.visibility = View.VISIBLE
                reveal.start()
            } else {
                finish()
            }
        },100)
    }
}
