package com.example.doan10.rv

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doan10.R
import com.example.doan10.data.lay_UserID
import com.example.doan10.data.post
import com.example.doan10.databinding.ListItemPostBinding
import com.example.doan10.xem_lai_bai_dang_blankDirections
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase

class PostAdapter(val ds: ArrayList<post>, val layUserid: lay_UserID) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ListItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        Log.d("PostAdapter", "Số lượng item: ${ds.size}")
        return ds.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemPost = ds[position]
        Log.d("PostAdapter", "Binding post: ${itemPost.tieude}")

        with(holder.binding) {
            // Hiển thị tiêu đề, xử lý null
            tvPost.text = itemPost.tieude ?: "Không có tiêu đề"

            val user = layUserid.user_ID.toString()

            // Sự kiện nhấn giữ để xóa bài đăng
            layoutPost.setOnLongClickListener {
                MaterialAlertDialogBuilder(holder.itemView.context)
                    .setTitle("Thao Tác Bài Đăng")
                    .setMessage("Bạn muốn làm gì với bài đăng ${itemPost.tieude ?: "này"}?")
                    .setPositiveButton("Xóa") { _, _ ->
                        val postId = itemPost.id ?: return@setPositiveButton
                        val fireBaseRef = FirebaseDatabase.getInstance().getReference("Post-main")
                        fireBaseRef.child(postId).removeValue()
                            .addOnCompleteListener {
                                Log.d("delete", "Xóa thành công")

                            }
                            .addOnFailureListener {
                                Log.d("delete", "Xóa thất bại: ${it.message}")

                            }
                    }
                    .setNegativeButton("Hủy") { _, _ ->
                        Log.d("delete", "Hủy xóa")
                    }
                    .setNeutralButton("Cập nhật") { _, _ ->
                        try {
                            val postId = itemPost.id ?: return@setNeutralButton
                            val action = xem_lai_bai_dang_blankDirections.actionXemLaiBaiDangBlankToSuaBaiDanngBlank(
                                postKey = postId
                            )
                            Navigation.findNavController(holder.itemView).navigate(action)
                            Log.d("dialog", "Chuyển đến màn hình cập nhật")
                        } catch (e: Exception) {
                            Log.e("Navigation", "Lỗi điều hướng cập nhật: ${e.message}")
                        }
                    }
                    .show()
                true
            }

            // Tải hình ảnh bài đăng
            when {
                !itemPost.Url.isNullOrEmpty() -> {
                    Glide.with(holder.itemView)
                        .load(itemPost.Url)
                        .centerCrop()
                        .error(R.drawable.error)
                        .into(imgPost)
                }
                !itemPost.Url2.isNullOrEmpty() -> {
                    Glide.with(holder.itemView)
                        .load(itemPost.Url2)
                        .centerCrop()
                        .error(R.drawable.error)
                        .into(imgPost)
                }
                !itemPost.Url3.isNullOrEmpty() -> {
                    Glide.with(holder.itemView)
                        .load(itemPost.Url3)
                        .centerCrop()
                        .error(R.drawable.error)
                        .into(imgPost)
                }
                else -> {
                    Glide.with(holder.itemView)
                        .load(R.drawable.error)
                        .into(imgPost)
                }
            }

            // Sự kiện nhấn để xem chi tiết bài đăng
            layoutPost.setOnClickListener {

                    val action = xem_lai_bai_dang_blankDirections.actionXemLaiBaiDangBlankToInfoMotoBlank(
                        itemPost.id,
                        itemPost.Url3,
                        itemPost.soluong,
                        itemPost.giaban.toString(), // Thử truyền String tĩnh
                        itemPost.ghim,
                        itemPost.tieude,
                        itemPost.mota,
                        itemPost.tinhtrang,
                        itemPost.loaixe,
                        itemPost.namsx,
                        itemPost.sdt,
                        itemPost.nsx.toString(),
                        itemPost.Url.toString(),
                        itemPost.Url2.toString(),
                    )
                    Navigation.findNavController(holder.itemView).navigate(action)

            }
        }
    }

}