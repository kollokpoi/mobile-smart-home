package com.example.myapplication.viewholders

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.View
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.myapplication.ItemActivity
import com.example.myapplication.R
import com.example.myapplication.classes.Command
import com.example.myapplication.classes.Item
import com.example.myapplication.services.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Base64

class ItemsViewHolder(itemView: View) : ViewHolder(itemView) {

    private val imageView : ImageView = itemView.findViewById(R.id.iconImageView)
    private val nameTextView : TextView = itemView.findViewById(R.id.nameTv)
    private val loading : ProgressBar = itemView.findViewById(R.id.loading)
    private val failedLL : LinearLayout = itemView.findViewById(R.id.loadingFailed)
    private val loadedLL : LinearLayout = itemView.findViewById(R.id.loadingGrate)

    fun setContent(item:Item){
        nameTextView.text = item.name
        if (item.image!=null){
            val image = Base64.getDecoder().decode(item.image)
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.size));
        }
        if (itemView.context!=null){
            itemView.setOnClickListener {
                val intent = Intent(itemView.context,ItemActivity::class.java)
                intent.putExtra("id",item.id);
                itemView.context.startActivity(intent)
            }
        }
        val call = RetrofitFactory.instance.pingItem(item.id!!)

        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                loading.animate().translationX(100f * itemView.context.resources.displayMetrics.density).alpha(0f).setDuration(500).withEndAction {
                    if (response.body()==true) {
                        loadedLL.animate().translationX(0f).alpha(1f).setDuration(500)
                    } else {
                        failedLL.animate().translationX(0f).alpha(1f).setDuration(500)
                    }
                }
            }
            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                loading.animate().translationX(100f * itemView.context.resources.displayMetrics.density).alpha(0f).setDuration(500).withEndAction {
                    failedLL.animate().translationX(0f).alpha(1f).setDuration(500)
                }
            }
        })
    }
}