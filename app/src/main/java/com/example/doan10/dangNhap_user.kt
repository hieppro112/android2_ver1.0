package com.example.doan10

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.doan10.databinding.DangnhapUserLayoutBinding

class dangNhap_user : Fragment() {
    private lateinit var binding:DangnhapUserLayoutBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.home_screen_blank)
        }
        binding.tvQuenMatKhau.setOnClickListener {
            findNavController().navigate(R.id.quen_mk_bank)
        }
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.dangKy_user_blank)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DangnhapUserLayoutBinding.inflate(inflater,container,false)
         return binding.root
    }

}