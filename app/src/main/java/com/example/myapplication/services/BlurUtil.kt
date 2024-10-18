package com.example.myapplication.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import com.example.myapplication.R

class BlurUtil {
    companion object{
        fun blurScreen(context: Context?, blurContainer:View,rootView:View,blurView:View) {
            blurContainer.visibility = View.VISIBLE

            val backgroundDrawable = BitmapDrawable(context?.resources, takeScreenShot(rootView))
            val bitmap = (backgroundDrawable as BitmapDrawable).bitmap
            val rs = RenderScript.create(context)
            val input = Allocation.createFromBitmap(rs, bitmap)
            val output = Allocation.createTyped(rs, input.type)
            val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

            script.setInput(input)
            script.setRadius(25f)
            script.forEach(output)
            output.copyTo(bitmap)

            val blurredDrawable = BitmapDrawable(context?.resources, bitmap)
            val colorDrawable = context?.let { ColorDrawable(it.getColor(R.color.mainTransparent)) }
            val layers = arrayOf(blurredDrawable, colorDrawable)
            val layerDrawable = LayerDrawable(layers)

            blurView.background = layerDrawable
        }
        private fun takeScreenShot(view: View): Bitmap {
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            return bitmap
        }
    }
}