package yuvabharat.ramu.bloodonator.kotlination

import android.animation.ValueAnimator

/**
 * Created by ramu on 4/6/17.
 */
class UIAnimation{
    interface Elapsed {
       fun OnValueChange(value: Any)
    }

    fun upTo(value:Any,inTime:Long,elapsed: Elapsed){
        val anim:ValueAnimator
        when(value){
            is Int-> anim = ValueAnimator.ofInt(value)
            is Float->anim = ValueAnimator.ofFloat(value)
            else-> anim = ValueAnimator()
        }
        anim.duration = inTime
        anim.addUpdateListener { elapsed.OnValueChange(it.animatedValue) }
    }
}