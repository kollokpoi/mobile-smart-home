package com.example.myapplication.ui.createverb

import android.app.Dialog
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.classes.Verb
import com.example.myapplication.databinding.FragmentBottomSheetCreateVerbBinding
import com.example.myapplication.viewmodels.CommandViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

class CreateVerbFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetCreateVerbBinding? = null
    private val binding get() = _binding!!
    private var imageUri : Uri? = null
    private var commandId:Int?=null
    private lateinit var model: CommandViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        _binding = FragmentBottomSheetCreateVerbBinding.inflate(inflater, container, false)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
        commandId = arguments?.getInt("commandId")
        model = ViewModelProvider(requireActivity())[CommandViewModel::class.java]

        binding.createBtn.setOnClickListener(this::createBtnClick)
        return binding.root
    }

    private fun createBtnClick(view: View){
        if (!binding.nameEt.text.any()){
            Toast.makeText(context, getText(R.string.empty_fields_error), Toast.LENGTH_SHORT).show()
            return
        }

        val name = binding.nameEt.text.toString()
        val verb = Verb(commandId!!,name)
        model.createVerb(verb)
        onDestroyView()
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
        fun newInstance(itemId: Int): CreateVerbFragment =
            CreateVerbFragment().apply {
                arguments = Bundle().apply {
                    putInt("commandId",itemId)
                }
            }

    }
}