package com.example.myapplication.ui.itemsfragment

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.ItemsAdapter
import com.example.myapplication.databinding.FragmentItemsBinding
import com.example.myapplication.services.Helpers
import com.example.myapplication.services.Helpers.Companion.blurElements
import com.example.myapplication.services.Helpers.Companion.pumpUpElement
import com.example.myapplication.ui.createitem.CreateItemFragment
import jp.wasabeef.blurry.Blurry

class ItemsFragment : Fragment() {
    private var _binding: FragmentItemsBinding? = null
    private val binding get() = _binding!!
    private lateinit var model: ItemsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val adapter = ItemsAdapter(emptyList())

        _binding = FragmentItemsBinding.inflate(layoutInflater);
        model = ViewModelProvider(requireActivity())[ItemsViewModel::class.java]

        binding.itemsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.itemsRecycler.adapter = adapter

        model.items.observe(viewLifecycleOwner) { items ->
            adapter.itemsList = items
            adapter.notifyDataSetChanged()
        }
        model.loading.observe(viewLifecycleOwner){
            if (it) {
                Helpers.blurElements(
                    binding.blurContainer,
                    binding.blurView,
                    binding.root,
                    requireContext()
                )
                Helpers.showItem(binding.loadingElement.loadingProgress)
                pumpUpElement(binding.loadingElement.animateContainer,binding.root)
            }else{
                Helpers.hideItem(binding.loadingElement.loadingProgress)
                Helpers.hideItem(binding.blurContainer)
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
                Helpers.showAndHide(binding.loadingElement.savedImageView)
            }
        }

        binding.fab.setOnClickListener {
            val bottomSheetFragment = CreateItemFragment()
            bottomSheetFragment.show(parentFragmentManager, "MyBottomSheetFragment")
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        model.fetchItems()
    }
}