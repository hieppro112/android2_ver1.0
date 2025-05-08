package com.example.doan10

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doan10.databinding.HomeScreenFrBinding
import com.example.doan10.item.Product
import com.example.doan10.rv.ProductAdapter


class home_screen_blank : Fragment() {
    private lateinit var binding:HomeScreenFrBinding



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val danhmucxe = listOf("xe đã qua sử dụng", "xe mới","xe không chính chủ");
        val adapter  =ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,danhmucxe)
        binding.spDanhmucxe.adapter=adapter

        setupRecyclerViews()

        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.profile_user_blank)
        }

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.add_moto_screen)
        }
    }

    private fun setupRecyclerViews() {
        // Thiết lập RecyclerView cho sản phẩm
        binding.productRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.productRecyclerView.adapter = ProductAdapter(getProducts())
    }



    private fun getProducts(): List<Product> {
        return listOf(
            Product("MOTO Z1000 đã qua sử dụng còn mới 90%", R.drawable.img_moto), // Thay bằng hình ảnh thực tế
            Product("Siêu phẩm BMW M 1000 RR 2022", R.drawable.img_moto),
            Product("Siêu mô tô BMW S1000RR 2023", R.drawable.img_moto)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=HomeScreenFrBinding.inflate(layoutInflater,container,false)
        return binding.root
    }


}