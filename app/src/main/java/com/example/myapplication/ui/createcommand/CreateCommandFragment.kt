package com.example.myapplication.ui.createcommand

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.classes.Command
import com.example.myapplication.databinding.FragmentBottomSheetCreateCommandBinding
import com.example.myapplication.viewmodels.CommandViewModel
import com.example.myapplication.viewmodels.ItemViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Base64

class CreateCommandFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetCreateCommandBinding? = null
    private val binding get() = _binding!!
    private var imageUri : Uri? = null
    private var itemId:Int?=null
    private var createMode = true
    private lateinit var modelForCreate: ItemViewModel
    private lateinit var modelForEdit: CommandViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        _binding = FragmentBottomSheetCreateCommandBinding.inflate(inflater, container, false)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
        arguments?.run {
            itemId=getInt("itemId")
            createMode = getBoolean("createMode")
        }
        if (createMode)
            modelForCreate = ViewModelProvider(requireActivity())[ItemViewModel::class.java]
        else{
            modelForEdit = ViewModelProvider(requireActivity())[CommandViewModel::class.java]
            modelForEdit.currentCommand.value!!.run {
                binding.nameEt.setText(commandName)
                binding.commandToSendEt.setText(commandToSend)
                binding.commandBodyEt.setText(jsonBody)
                binding.shouldReturnCb.isChecked = shouldReturn
                if (image!=null){
                    val image = Base64.getDecoder().decode(image)
                    binding.iconImageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.size));
                }
            }
        }

        binding.createBtn.setOnClickListener(this::createBtnClick)
        binding.chooseImageTv.setOnClickListener(this::chooseImageClick)
        return binding.root
    }

    private fun createBtnClick(view: View){
        if (!binding.nameEt.text.any() || !binding.commandToSendEt.text.any()){
            Toast.makeText(context, getText(R.string.empty_fields_error), Toast.LENGTH_SHORT).show()
            return
        }

        val name = binding.nameEt.text.toString()
        val commandToSend = binding.commandToSendEt.text.toString()
        val jsonBody = binding.commandBodyEt.text.toString()
        val shouldReturn = binding.shouldReturnCb.isChecked;

        var imagePart: MultipartBody.Part? = null;
        imageUri?.let {
            val imageBitmap = uriToBitmap(it)

            val imageRequestBody = imageBitmap.let {bitmap->
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val imageBytes = byteArrayOutputStream.toByteArray()
                imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, imageBytes.size)
            }

            imagePart = imageRequestBody.let {imagePart->
                MultipartBody.Part.createFormData("image", "image.jpg", imagePart)
            }
        }

        val command = Command(name,commandToSend,itemId!!,shouldReturn, jsonBody =  jsonBody)
        if (createMode)
            modelForCreate.createCommand(command, imagePart)
        else{
            modelForEdit.editCommand(command,imagePart)
        }


        onDestroyView()
    }

    private fun chooseImageClick(view:View){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            data?.data?.let { uri ->
                binding.iconImageView.setImageURI(uri)
                imageUri = uri
            }
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap {
        val inputStream: InputStream? = context?.contentResolver?.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }

    private fun getBackgroundColorFromTheme(): ColorStateList {
        val typedArray: TypedArray = requireContext().theme.obtainStyledAttributes(intArrayOf(android.R.attr.colorBackground))
        val color = typedArray.getColor(0, 0)
        typedArray.recycle()
        return ColorStateList.valueOf(color)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet!!.run {
                BottomSheetBehavior.from(this).state = BottomSheetBehavior.STATE_EXPANDED
                BottomSheetBehavior.from(this).skipCollapsed = true

                val shapeAppearanceModel = ShapeAppearanceModel.builder(context, 0, R.style.ShapeAppearanceOverlay_BottomSheet).build()
                val materialShapeDrawable = MaterialShapeDrawable(shapeAppearanceModel).apply {
                    fillColor = getBackgroundColorFromTheme()
                }
                this.background = materialShapeDrawable
            }

        }
        return dialog
    }

    companion object {
        fun newInstance(itemId: Int, createMode:Boolean = true): CreateCommandFragment =
            CreateCommandFragment().apply {
                arguments = Bundle().apply {
                    putInt("itemId",itemId)
                    putBoolean("createMode", createMode)
                }
            }
    }
}