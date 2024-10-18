package com.example.myapplication.ui.imagegenerator

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.adapters.StylesSpinnerAdapter
import com.example.myapplication.databinding.FragmentImageGeneratorBinding
import com.example.myapplication.services.Helpers
import com.example.myapplication.services.Helpers.Companion.hideItem
import com.example.myapplication.services.Helpers.Companion.openFull
import com.example.myapplication.services.Helpers.Companion.pumpUpElement
import com.example.myapplication.services.Helpers.Companion.showAndHide
import com.example.myapplication.services.Helpers.Companion.showItem
import jp.wasabeef.blurry.Blurry
import java.io.File
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class ImageGeneratorFragment : Fragment() {
    private lateinit var model: ImageGeneratorViewModel
    private var _binding: FragmentImageGeneratorBinding? = null
    private val binding get() = _binding!!
    private var imageExpanded = false
    private var originalHeight = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageGeneratorBinding.inflate(layoutInflater)
        model = ViewModelProvider(requireActivity())[ImageGeneratorViewModel::class.java]

        binding.stylesSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,view: View, position: Int, id: Long) {
                model.setStyle(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        binding.createBtn.setOnClickListener(this::createClick)
        binding.saveBtn.setOnClickListener(this::saveClick)
        binding.iconImageView.setOnClickListener(this::openFullClick)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            originalHeight = binding.imageHolder.height
            model.loading.observe(viewLifecycleOwner){
                if (it) {
                    Helpers.blurElements(
                        binding.blurContainer,
                        binding.blurView,
                        binding.root,
                        requireContext()
                    )
                    showItem(binding.loadingElement.loadingProgress)
                    pumpUpElement(binding.loadingElement.animateContainer,binding.root)
                }else{
                    hideItem(binding.loadingElement.loadingProgress)
                    hideItem(binding.blurContainer)
                    Thread.sleep(200)
                }
            }
            model.failure.observe(viewLifecycleOwner){
                if (it){
                    Helpers.showAndHide(binding.loadingElement.errorImageView)
                }
            }
            model.changesSaved.observe(viewLifecycleOwner){
                if (it){
                    showAndHide(binding.loadingElement.savedImageView)
                }
            }
            model.styles.observe(viewLifecycleOwner){
                binding.stylesSpinner.adapter = StylesSpinnerAdapter(requireContext(),it)
            }
            model.imageBase64.observe(viewLifecycleOwner){
                if (it!=null){
                    val image = Base64.getDecoder().decode(it)
                    binding.iconImageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.size))
                }else{
                    binding.iconImageView.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_launcher_background))
                }
            }
        }
    }

    private fun createClick(view:View){
        val prompt = binding.promptEt.text
        if (!prompt.any()){
            Toast.makeText(context, getText(R.string.empty_fields_error), Toast.LENGTH_SHORT).show()
            return
        }
        model.getImage(prompt.toString())
    }

    private fun saveClick(view:View){
        if (model.imageBase64.value == null){
            Toast.makeText(context, getText(R.string.error_hint), Toast.LENGTH_SHORT).show()
            return
        }
        val image = Base64.getDecoder().decode(model.imageBase64.value)
        val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timeStamp.jpg"

        val resolver: ContentResolver = requireContext().contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "MyAppFolder")
        }

        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        resolver.openOutputStream(imageUri!!)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
        }
        showAndHide(binding.loadingElement.savedImageView)
    }

    private fun openFullClick(view:View){
        openFull(binding.root,binding.imageHolder,imageExpanded,originalHeight)
        imageExpanded=!imageExpanded
    }
}