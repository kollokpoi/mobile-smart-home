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
import com.example.myapplication.classes.Verb
import java.util.Base64

class VerbViewHolder(itemView: View) : ViewHolder(itemView) {
    private val nameTextView : TextView = itemView.findViewById(R.id.nameTv)
    val deleteBtn : Button = itemView.findViewById(R.id.deleteBtn)

    fun setContent(verb: Verb) {
        nameTextView.text = verb.verb
    }
}