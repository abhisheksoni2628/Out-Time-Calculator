package com.abhishek.outx.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abhishek.outx.databinding.FragmentCustomDialogBinding
import com.abhishek.outx.model.ResultTimeFeed
import com.abhishek.outx.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CustomDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var binding: FragmentCustomDialogBinding

    private var breakTaken: String = ""
    var outTime: String = ""
    private var mBottomBehavior: BottomSheetBehavior<*>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCustomDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBottomBehavior = BottomSheetBehavior.from(binding.root.parent as View)
        mBottomBehavior?.maxWidth = ViewGroup.LayoutParams.MATCH_PARENT
        mBottomBehavior?.state = BottomSheetBehavior.STATE_EXPANDED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(
                Constants.passingDataKey,
                ResultTimeFeed::class.java
            )?.let {
                breakTaken = it.breakTaken
                outTime = it.outTime
            }
        } else {
            arguments?.getParcelable<ResultTimeFeed>(Constants.passingDataKey)
                ?.let {
                    breakTaken = it.breakTaken
                    outTime = it.outTime
                }
        }
        initViews()
    }

    private fun initViews() {
        setData()
        binding.btnClose.setOnClickListener(this)
    }

    private fun setData() {
        binding.tvBreak.text = breakTaken
        binding.tvOutTime.text = outTime
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnClose -> {
                dismiss()
            }
        }
    }

}