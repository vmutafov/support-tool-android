package com.azbouki.supporttool.ui.home

import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.azbouki.supporttool.databinding.FragmentHomeBinding
import com.azbouki.supporttool.sdk.SdkState
import com.azbouki.supporttool.sdk.SupportTool

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        textView.setOnClickListener {
            Log.i("yoooo", "yoooo")
        }

        binding.startSupportToolBtn.setOnClickListener {
            SupportTool.start()
        }

        binding.stopSupportToolBtn.setOnClickListener {
            Log.i("pesho", "yoooo")
            SupportTool.stop()
        }

        binding.twilioRoomNameEditText.doAfterTextChanged {
            SdkState.twilioRoomName = it.toString()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}