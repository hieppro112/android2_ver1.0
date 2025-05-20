package com.example.doan10

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doan10.data.lay_UserID
import com.example.doan10.data.post
import com.example.doan10.databinding.XemLaiBaiDangLayoutBinding
import com.example.doan10.rv.PostAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class xem_lai_bai_dang_blank : Fragment() {

    private lateinit var listPost: ArrayList<post>
    private val layUserID: lay_UserID by activityViewModels()
    private lateinit var firebasePost: DatabaseReference
    private lateinit var adapter: PostAdapter
    private lateinit var binding: XemLaiBaiDangLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val user = layUserID.user_ID.toString()
        Log.d("test id du lieu", "id = $user")

        firebasePost = FirebaseDatabase.getInstance().getReference("Post-main")
        binding = XemLaiBaiDangLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listPost = ArrayList()

        // Nút quay lại
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Thiết lập RecyclerView
        adapter = PostAdapter(listPost, layUserID)
        binding.rvPort.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@xem_lai_bai_dang_blank.adapter
        }

        // Lấy danh sách bài đăng
        getListPost()
    }

    private fun getListPost() {
        val userId = layUserID.user_ID.toString()
        Log.d("Firebase", "User ID: $userId")
        firebasePost.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listPost.clear()
                var count = 0
                Log.d("Firebase", "Snapshot exists: ${snapshot.exists()}")
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val post = item.getValue(post::class.java)?.apply { id = item.key ?: "" }
                        Log.d("Firebase", "Post: $post")
                        post?.let {
                            if (it.user_id == userId && it.duyet == 2) { // Lọc theo user_id và duyet = 2
                                listPost.add(it)
                                count++
                            }
                        }
                    }
                    Log.d("Firebase", "Số bài đăng (user_id = $userId, duyet = 2): $count")
                    binding.tvSoluong.text = count.toString()
                    adapter.notifyDataSetChanged()
                } else {
                    binding.tvSoluong.text = "0"
                    Log.d("Firebase", "Danh sách rỗng")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Lỗi: ${error.message}")
            }
        })
    }
}