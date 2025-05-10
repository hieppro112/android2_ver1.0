package com.example.doan10

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doan10.data.post
import com.example.doan10.databinding.HomeScreenFrBinding
import com.example.doan10.rv.ProductAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class home_screen_blank : Fragment() {
    private lateinit var binding:HomeScreenFrBinding
    private lateinit var listPost:ArrayList<post>
    private lateinit var firebaseRefPost:DatabaseReference


    private lateinit var adapterPost:ProductAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val danhmucxe = listOf("tất cả","xe đã qua sử dụng", "xe mới","xe không chính chủ");
        val adapter  =ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,danhmucxe)
        binding.spDanhmucxe.adapter=adapter

        firebaseRefPost=FirebaseDatabase.getInstance().getReference("Post")
        listPost= arrayListOf()

        adapterPost = ProductAdapter(listPost)
        binding.productRecyclerView.adapter=adapterPost
        binding.productRecyclerView.layoutManager=LinearLayoutManager(context)

        fetchData()

        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.profile_user_blank)
        }

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.add_moto_screen)
        }
        binding.btnNotify.setOnClickListener {
            findNavController().navigate(R.id.notifyScreen)
        }



    }

    private fun fetchData() {
        firebaseRefPost.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snap: DataSnapshot) {
                listPost.isEmpty()
                if(snap.exists()){
                    for (item in snap.children){
                        Log.d("fech data", "passs1: ")
                        var item_post = item.getValue(post::class.java)
                        Log.d("fech data", "passs1:  $item_post")
                        listPost.add(item_post!!)
                    }
                    binding.productRecyclerView.adapter?.notifyDataSetChanged()
                    Log.d("fech data", "${listPost.size}: ")
                }
                else{
                    Log.d("fech data", "fail: ")
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("fech data", "fail: ")
            }

        })
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