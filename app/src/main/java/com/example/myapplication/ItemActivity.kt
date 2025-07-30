package com.example.myapplication

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.res.integerArrayResource
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.CommandAdapter
import com.example.myapplication.databinding.ActivityItemBinding
import com.example.myapplication.services.Helpers
import com.example.myapplication.services.Helpers.Companion.blurElements
import com.example.myapplication.services.Helpers.Companion.pumpUpElement
import com.example.myapplication.ui.createcommand.CreateCommandFragment
import com.example.myapplication.ui.createitem.CreateItemFragment
import com.example.myapplication.viewmodels.ItemViewModel
import jp.wasabeef.blurry.Blurry
import java.util.Base64
import kotlin.properties.Delegates

class ItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityItemBinding
    private var id by Delegates.notNull<Int>()
    private val model : ItemViewModel by viewModels()
    private var imageExpanded = false
    private var originalHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemBinding.inflate(layoutInflater)
        id = intent.getIntExtra("id",0)

        setContentView(binding.root)
        val adapter = CommandAdapter(emptyList(),model)
        binding.commandRecycler.layoutManager = LinearLayoutManager(this)
        binding.commandRecycler.adapter = adapter

        model.currentItem.observe(this, Observer { item->
            if (item.image!=null){
                val image = Base64.getDecoder().decode(item.image)
                binding.iconImageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.size));
            }
            binding.itemNameTv.text = item.name
        })
        model.commands.observe(this, Observer { commands->
            adapter.commands = commands
            adapter.notifyDataSetChanged()
        })
        model.loading.observe(this){
            if (it) {
                Helpers.blurElements(
                    binding.blurContainer,
                    binding.blurView,
                    binding.root,
                    this
                )
                Helpers.showItem(binding.loadingElement.loadingProgress)
                pumpUpElement(binding.loadingElement.animateContainer,binding.root)
            }else{
                Helpers.hideItem(binding.loadingElement.loadingProgress)
                Helpers.hideItem(binding.blurContainer)
                Thread.sleep(200)
            }
        }
        model.failure.observe(this){
            if (it){
                Helpers.showAndHide(binding.loadingElement.errorImageView)
            }
        }
        model.changesSaved.observe(this){
            if (it){
                Helpers.showAndHide(binding.loadingElement.savedImageView)
            }
        }
        model.deleted.observe(this){
            if (it) finish()
        }

        binding.fab.setOnClickListener {
            val bottomSheetFragment = CreateCommandFragment.newInstance(id)
            bottomSheetFragment.show(supportFragmentManager, "MyBottomSheetFragment")
        }
        binding.editImageView.setOnClickListener {
            val bottomSheetFragment = CreateItemFragment.newInstance(false)
            bottomSheetFragment.show(supportFragmentManager, "MyBottomSheetFragment")
        }
        binding.deleteImageView.setOnClickListener {
            model.deleteItem()
        }
        binding.imageHolder.setOnClickListener(this::openFullClick)
        binding.imageHolder.post{
            originalHeight = binding.imageHolder.height
        }
        binding.webImageView.setOnClickListener{
            val intent = Intent(this,WebViewActivity::class.java)
            intent.putExtra("id",id)
            intent.putExtra("name",model.currentItem.value!!.name)
            startActivity(intent)
        }
    }

    private fun openFullClick(view:View){
        Helpers.openFull(binding.root, binding.imageHolder, imageExpanded, originalHeight)
        imageExpanded=!imageExpanded
    }

    override fun onStart() {
        super.onStart()
        model.setItemId(id)
    }
}