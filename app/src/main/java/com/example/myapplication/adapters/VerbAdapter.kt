package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.classes.Verb
import com.example.myapplication.viewholders.VerbViewHolder
import com.example.myapplication.viewmodels.CommandViewModel
import com.example.myapplication.viewmodels.ItemViewModel

class VerbAdapter (var verbList: List<Verb>, val viewModel: CommandViewModel) : RecyclerView.Adapter<VerbViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerbViewHolder {
        return VerbViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.verb_item, parent, false))
    }

    override fun getItemCount() = verbList.size

    override fun onBindViewHolder(holder: VerbViewHolder, position: Int) {
        val item = verbList[position];
        holder.setContent(item)
        holder.deleteBtn.setOnClickListener{
            viewModel.deleteVerb(item)
        }
    }
}
