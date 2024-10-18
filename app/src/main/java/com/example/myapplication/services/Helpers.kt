package com.example.myapplication.services

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import jp.wasabeef.blurry.Blurry
import kotlin.math.roundToInt

class Helpers {
    companion object{
        fun pumpUpElement(view: View, root:View) {
            val screenHeight = root.height.toFloat()
            val targetHeight = -(screenHeight+view.height)*0.5f
            AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(view, "translationY", 0f,targetHeight).apply {
                        duration = 400
                    },
                )
            }.start()
            Thread.sleep(500)
        }

        fun blurElements(blurContainer:View,blurView:ImageView,root:View,context: Context){
            blurView.setImageDrawable(null)

            val bitmap = Blurry.with(context)
                .radius(25)
                .sampling(10)
                .capture(root).get()
            val blurredDrawable = BitmapDrawable(bitmap)
            val colorDrawable =  ColorDrawable(
                ContextCompat.getColor(context,
                R.color.mainTransparent
            ))
            val layers = arrayOf(blurredDrawable, colorDrawable)
            val layerDrawable = LayerDrawable(layers)

            blurView.background = layerDrawable
            AnimatorSet().apply {
                play(ObjectAnimator.ofFloat(blurContainer, "alpha", 0f, 1f).apply { duration = 500 })
            }.start()
        }

        fun showAndHide(view:View){
            AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(view, "alpha",  1f).apply {
                        duration = 500
                    },
                    ObjectAnimator.ofFloat(view, "alpha",  0f).apply {
                        duration = 500
                        startDelay = 600
                    },
                )
                start()
            }
        }

        fun hideItem(view:View){
            val objectAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).apply {
                duration = 300
            }
            objectAnimator.start()
        }

        fun showItem(view:View){
            val objectAnimator = ObjectAnimator.ofFloat(view, "alpha",  0f,1f).apply {
                duration = 400
            }
            objectAnimator.start()
        }

        fun openFull(root:View,viewToExpand:View,imageExpanded:Boolean,originalHeight:Int){
            val fragmentHeight = root.height.toFloat()
            val targetHeight = (fragmentHeight * 0.9).roundToInt()

            val startHeight = if (imageExpanded) targetHeight else originalHeight
            val endHeight = if (imageExpanded) originalHeight else targetHeight

            val heightAnimator = ValueAnimator.ofInt(startHeight, endHeight).apply {
                addUpdateListener { animator ->
                    val newHeight = animator.animatedValue as Int
                    val layoutParams = viewToExpand.layoutParams
                    layoutParams.height = newHeight
                    viewToExpand.layoutParams = layoutParams
                }
                duration = 500
            }
            heightAnimator.start()
        }
    }
}