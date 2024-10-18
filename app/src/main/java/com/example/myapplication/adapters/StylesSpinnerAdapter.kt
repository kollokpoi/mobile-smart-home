package com.example.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.classes.Styles
import com.squareup.picasso.Picasso


class StylesSpinnerAdapter(context: Context, private val items: List<Styles>) :
    ArrayAdapter<Styles>(context, R.layout.styles_spinner_item, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent, R.layout.styles_spinner_item)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent, R.layout.styles_spinner_item)
    }

    private fun createViewFromResource(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
        resource: Int
    ): View {
        val view = LayoutInflater.from(context).inflate(resource, parent, false)
        val imageView = view.findViewById<ImageView>(R.id.iconImageView)
        val nameTv = view.findViewById<TextView>(R.id.nameTv)

        val item = items[position]
        nameTv.text = item.name
        Picasso.get()
            .load(item.image.toString())
            .into(imageView)

        return view
    }
}
