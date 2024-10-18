package com.example.myapplication.viewholders

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.myapplication.CommandActivity
import com.example.myapplication.ItemActivity
import com.example.myapplication.R
import com.example.myapplication.classes.Command
import com.example.myapplication.services.RetrofitFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Base64

class CommandViewHolder(itemView: View) : ViewHolder(itemView) {
    private val imageView : ImageView = itemView.findViewById(R.id.iconImageView)
    private val nameTextView : TextView = itemView.findViewById(R.id.nameTv)
    val executeBtn : FloatingActionButton = itemView.findViewById(R.id.fab)

    fun setContent(command: Command) {
        nameTextView.text = command.commandName
        if (command.image!=null){
            val image = Base64.getDecoder().decode(command.image)
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.size))
        }else{
            imageView.setImageDrawable(AppCompatResources.getDrawable(itemView.context,R.drawable.ic_launcher_background))
        }
        if (itemView.context!=null){
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, CommandActivity::class.java)
                intent.putExtra("commandId",command.id);
                itemView.context.startActivity(intent)
            }
        }

    }
}