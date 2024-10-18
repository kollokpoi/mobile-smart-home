package com.example.myapplication.ui.createitem

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.classes.Item
import com.example.myapplication.databinding.FragmentBottomSheetCreateItemBinding
import com.example.myapplication.viewmodels.ItemViewModel
import com.example.myapplication.ui.itemsfragment.ItemsViewModel
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

class CreateItemFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetCreateItemBinding? = null
    private val binding get() = _binding!!
    private var imageUri : Uri? = null
    private var createMode = true
    private lateinit var modelForCreate: ItemsViewModel
    private lateinit var modelForEdit: ItemViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        _binding = FragmentBottomSheetCreateItemBinding.inflate(inflater, container, false)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)

        arguments?.run {
            createMode = getBoolean("createMode")
        }

        if (createMode)
            modelForCreate = ViewModelProvider(requireActivity())[ItemsViewModel::class.java]
        else{
            modelForEdit = ViewModelProvider(requireActivity())[ItemViewModel::class.java]
            modelForEdit.currentItem.value!!.run{
                binding.nameEt.setText(name)
                binding.ipEt.setText(ipaddr)
                if (image!=null){
                    val image = Base64.getDecoder().decode(image)
                    binding.iconImageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.size));
                }
            }
        }


        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            val octets = source.split('.')
            if (octets.size>4)
                return@InputFilter ""

            if (end!=0 && source[end-1]!='.'){
                for (i in octets.indices){
                    val octet = octets[i]
                    for (char in octet){
                        if (!char.isDigit() && char!=':')
                            return@InputFilter ""
                    }
                    if (i==3){
                        val lastParts = octet.split(':')
                        if (lastParts.size==2){
                            val lastOctetToInt = lastParts[0].toIntOrNull()
                            val portToInt = lastParts[1].toIntOrNull()
                            if (lastOctetToInt == null || lastOctetToInt>255)
                                return@InputFilter ""
                            if (portToInt!=null && portToInt > 65535)
                                return@InputFilter ""

                        }else if (lastParts.size==1){
                            val lastOctetToInt = lastParts[0].toIntOrNull()
                            if (lastOctetToInt == null || lastOctetToInt>255)
                                return@InputFilter ""
                        }
                    }else{
                        val octetToInt = octet.toIntOrNull()
                        if (octetToInt == null || octetToInt > 255){
                            return@InputFilter ""
                        }
                    }
                }
            }
            null
        }

        binding.ipEt.filters = arrayOf(filter)
        binding.ipEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val input = s.toString()
                if (!isValidIP(input)) binding.ipEt.error = getString(R.string.ip_error)
            }
        })
        binding.createBtn.setOnClickListener(this::createBtnClick)
        binding.chooseImageTv.setOnClickListener(this::chooseImageClick)
        binding.createBtn.setOnClickListener(this::createBtnClick)
        binding.chooseImageTv.setOnClickListener(this::chooseImageClick)
        return binding.root
    }

    private fun createBtnClick(view: View){
        val name = binding.nameEt.text
        val ipaddr = binding.ipEt.text
        if (!name.any() || !ipaddr.any()){
            Toast.makeText(context, getText(R.string.empty_fields_error), Toast.LENGTH_SHORT).show()
            return
        }

        var imagePart:MultipartBody.Part? = null;
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
        val item = Item(name.toString(),ipaddr.toString())

        if (createMode) modelForCreate.createItem(item,imagePart)
        else modelForEdit.editItem(item,imagePart)

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

    private fun isValidIP(ip: String): Boolean {
        val ipRegex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
        val portRegex = "^([0-9]{1,5})$"
        val parts = ip.split(":")

        val ip = parts[0]
        if (!ip.matches(ipRegex.toRegex())) {
            return false
        }

        if (parts.size == 2) {
            val port = parts[1]
            if (!port.matches(portRegex.toRegex()))
                return false

            val portNumber = port.toInt()
            if (portNumber < 0 || portNumber > 65535)
                return false
        }
        else if (parts.size > 2)
            return false
        return true
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
        fun newInstance(createMode:Boolean): CreateItemFragment =
            CreateItemFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("createMode",createMode)
                }
            }
    }
}