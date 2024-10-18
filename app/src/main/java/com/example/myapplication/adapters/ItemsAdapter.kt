package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.classes.Item
import com.example.myapplication.viewholders.ItemsViewHolder

class ItemsAdapter(var itemsList: List<Item>) : RecyclerView.Adapter<ItemsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        return ItemsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false))
    }
    override fun getItemCount() = itemsList.size
    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        val item = itemsList[position];
        holder.setContent(item)
    }
}