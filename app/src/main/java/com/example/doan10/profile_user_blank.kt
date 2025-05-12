package com.example.doan10

import android.os.Bundle
import android.util.Printer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.doan10.databinding.ProfileUserLayoutBinding

class profile_user_blank : Fragment() {
    private lateinit var binding:ProfileUserLayoutBinding



    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=ProfileUserLayoutBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSavedPosts.setOnClickListener {
            findNavController().navigate(R.id.ghimPostMain)
        }

    }

}