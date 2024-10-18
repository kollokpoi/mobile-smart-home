package com.example.myapplication

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.VerbAdapter
import com.example.myapplication.databinding.ActivityCommandBinding
import com.example.myapplication.services.Helpers
import com.example.myapplication.services.Helpers.Companion.blurElements
import com.example.myapplication.services.Helpers.Companion.pumpUpElement
import com.example.myapplication.ui.createcommand.CreateCommandFragment
import com.example.myapplication.ui.createverb.CreateVerbFragment
import com.example.myapplication.viewmodels.CommandViewModel
import jp.wasabeef.blurry.Blurry
import java.util.Base64

class CommandActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommandBinding
    private val model : CommandViewModel by viewModels()
    private var commandId = 0
    private var imageExpanded = false
    private var originalHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommandBinding.inflate(layoutInflater)
        commandId = intent.getIntExtra("commandId",0)
        setContentView(binding.root)

        val adapter = VerbAdapter(emptyList(),model)
        binding.commandRecycler.layoutManager = LinearLayoutManager(this)
        binding.commandRecycler.adapter = adapter

        model.currentCommand.observe(this){
            if (it.image!=null){
                val image = Base64.getDecoder().decode(it.image)
                binding.iconImageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.size));
            }
            binding.itemNameTv.text = it.commandName
        }
        model.verbs.observe(this){
            adapter.verbList = it
            adapter.notifyDataSetChanged()
        }
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
            val bottomSheetFragment = CreateVerbFragment.newInstance(commandId)
            bottomSheetFragment.show(supportFragmentManager, "MyBottomSheetFragment")
        }
        binding.editImageView.setOnClickListener {
            val bottomSheetFragment = CreateCommandFragment.newInstance(commandId,false)
            bottomSheetFragment.show(supportFragmentManager, "MyBottomSheetFragment")
        }
        binding.deleteImageView.setOnClickListener {
            model.deleteCommand()
        }
        binding.imageHolder.setOnClickListener(this::openFullClick)
        binding.imageHolder.post{
            originalHeight = binding.imageHolder.height
        }
    }

    private fun openFullClick(view:View){
        Helpers.openFull(binding.root, binding.imageHolder, imageExpanded, originalHeight)
        imageExpanded=!imageExpanded
    }

    override fun onStart() {
        super.onStart()
        model.setId(commandId)
    }
}