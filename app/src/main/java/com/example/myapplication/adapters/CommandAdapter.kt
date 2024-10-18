package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.classes.Command
import com.example.myapplication.viewholders.CommandViewHolder
import com.example.myapplication.viewholders.ItemsViewHolder
import com.example.myapplication.viewmodels.CommandViewModel
import com.example.myapplication.viewmodels.ItemViewModel

class CommandAdapter(var commands: List<Command>, val viewModel: ItemViewModel) : RecyclerView.Adapter<CommandViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommandViewHolder {
        return CommandViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.command_item, parent, false))
    }

    override fun getItemCount() = commands.size

    override fun onBindViewHolder(holder: CommandViewHolder, position: Int) {
        val item = commands[position];
        holder.setContent(item)
        holder.executeBtn.setOnClickListener{
            viewModel.executeCommand(item)
        }
    }
}