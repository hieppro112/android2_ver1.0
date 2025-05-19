package com.example.doan10

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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
import kotlin.math.log


class home_screen_blank : Fragment() {
    private lateinit var binding:HomeScreenFrBinding
    private lateinit var listPost:ArrayList<post>
    private lateinit var firebaseRefPost:DatabaseReference
    private lateinit var adapterPost: ProductAdapter



    private var currentKeyword: String = ""
    private var currentCategory: String = "tất cả"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chuyenanh()
        val tinhTrangOptions = listOf("Tất cả", "Xe mới", "Đã qua sử dụng");
        val adapter  =ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,tinhTrangOptions)
        binding.spDanhmucxe.adapter=adapter

        firebaseRefPost=FirebaseDatabase.getInstance().getReference("Post-main")
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
        binding.btnHome.setOnClickListener {
            findNavController().navigate(R.id.home_screen_blank)
        }
        binding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentKeyword = query.orEmpty()
                filterData()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentKeyword = newText.orEmpty()
                filterData()
                return true
            }
        })

        // Lọc theo danh mục
        binding.spDanhmucxe.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                currentCategory = parent.getItemAtPosition(position).toString()
                filterData()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun fetchData() {
        firebaseRefPost.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snap: DataSnapshot) {
                listPost.isEmpty()
                if (snap.exists()){
                    for (item in snap.children){
                        var duyet = item.child("duyet").getValue(Int::class.java)
                        if (duyet==2){
                            item?.let {
                                var post = it.getValue(post::class.java)
                                listPost.add(post!!)
                            }
                        }
                    }
                    filterData()
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
    private fun filterData() {
        val filteredList = listPost.filter { post ->
            val matchCategory = when (currentCategory) {
                "Tất cả" -> true
                "Xe mới" -> post.tinhtrang == 0
                "Đã qua sử dụng" -> post.tinhtrang == 1
                else -> true
            }

            val matchKeyword = currentKeyword.isEmpty() || post.tieude.contains(currentKeyword, ignoreCase = true)

            matchCategory && matchKeyword
        }

        adapterPost.updateData(filteredList)
    }

    fun chuyenanh(){
        binding.bannerHome.flipInterval=3000
        binding.bannerHome.isAutoStart=true
        binding.bannerHome.startFlipping()

        binding.btnHome.setOnClickListener {
            Log.d("banner", "view: ")

        }

        binding.banner1.setOnClickListener {
            Log.d("banner", "banner1: ")
        }
        binding.banner2.setOnClickListener {
            Log.d("banner", "banner2: ")
        }
        binding.banner3.setOnClickListener {
            Log.d("banner", "banner3: ")
        }

    }

}