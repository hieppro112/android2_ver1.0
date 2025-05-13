package com.example.doan10
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.doan10.data.lay_UserID
import com.example.doan10.databinding.XemLaiBaiDangLayoutBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.net.HttpURLConnection
import java.net.URL

class xem_lai_bai_dang_blank : Fragment() {

    private lateinit var binding: XemLaiBaiDangLayoutBinding
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var firebaseRefPost: DatabaseReference
    private lateinit var postAdapter: PostAdapter
    private val posts = mutableListOf<Post>()

    //lay du lieu duoc truyen den
    private val layUserID: lay_UserID by activityViewModels()

    // Lớp Post để lưu trữ dữ liệu bài đăng
    data class Post(
        val tieude: String = "",
        val loaixe: String = "",
        val namsx: Int = 0,
        val giaban: Double = 0.0,
        val user_id: String = "",
        val Url: String = "",
        val key: String = ""
    )

    // Adapter tùy chỉnh cho ListView
    inner class PostAdapter(context: Context, posts: List<Post>) :
        ArrayAdapter<Post>(context, 0, posts) {

        private val imageLoadTasks = HashMap<Int, LoadImageTask>()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.list_item_post, parent, false)

            val post = getItem(position) ?: return view

            val tvTieude = view.findViewById<TextView>(R.id.tvTieude)
            val tvLoaixe = view.findViewById<TextView>(R.id.tvLoaixe)
            val tvNamsx = view.findViewById<TextView>(R.id.tvNamsx)
            val tvGiaban = view.findViewById<TextView>(R.id.tvGiaban)
            val ivImage = view.findViewById<ImageView>(R.id.ivImage)

            tvTieude.text = post.tieude
            tvLoaixe.text = "Loại xe: ${post.loaixe}"
            tvNamsx.text = "Năm sản xuất: ${post.namsx}"
            tvGiaban.text = "Giá bán: ${post.giaban} VNĐ"

            // Hủy task cũ nếu có
            imageLoadTasks[position]?.cancel(true)

            // Tải ảnh thủ công từ URL
            if (post.Url.isNotEmpty()) {
                val task = LoadImageTask(ivImage)
                imageLoadTasks[position] = task
                task.execute(post.Url)
            } else {
                ivImage.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            return view
        }

        // Hủy tất cả task khi làm mới danh sách
        fun cancelAllTasks() {
            imageLoadTasks.values.forEach { it.cancel(true) }
            imageLoadTasks.clear()
        }

        // AsyncTask để tải ảnh từ URL
        private inner class LoadImageTask(private val imageView: ImageView) :
            AsyncTask<String, Void, Bitmap?>() {

            override fun doInBackground(vararg params: String?): Bitmap? {
                if (isCancelled) return null
                val urlString = params[0] ?: return null
                return try {
                    val url = URL(urlString)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val input = connection.inputStream
                    BitmapFactory.decodeStream(input)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

            override fun onPostExecute(result: Bitmap?) {
                if (!isCancelled && result != null) {
                    imageView.setImageBitmap(result)
                } else if (!isCancelled) {
                    imageView.setImageResource(android.R.drawable.ic_menu_gallery)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = XemLaiBaiDangLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo các tham chiếu Firebase
        firebaseRef = FirebaseDatabase.getInstance().getReference()
        firebaseRefPost = FirebaseDatabase.getInstance().getReference("Post-main")

        // Khởi tạo Adapter cho ListView
        postAdapter = PostAdapter(requireContext(), posts)
        binding.listItem.adapter = postAdapter

        // Thêm sự kiện nhấn giữ cho ListView
        binding.listItem.setOnItemLongClickListener { _, _, position, _ ->
            val post = posts[position]
            showDeleteConfirmationDialog(post, position)
            true
        }

        // Xử lý sự kiện nút Thoát
        binding.btnExit.setOnClickListener {
            findNavController().popBackStack()
        }

        // Lấy dữ liệu từ Firebase
        fetchPosts()
    }

    private fun fetchPosts() {
        val userId = layUserID.user_ID // Thay bằng userId thực tế nếu có
        if (!isNetworkAvailable()) {
            Toast.makeText(requireContext(), "Không có kết nối internet", Toast.LENGTH_SHORT).show()
            return
        }
        postAdapter.cancelAllTasks()
        firebaseRefPost.orderByChild("user_id").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    posts.clear()
                    if (!snapshot.exists()) {
                        postAdapter.notifyDataSetChanged()
                        Toast.makeText(requireContext(), "Không có bài đăng nào", Toast.LENGTH_SHORT).show()
                        return
                    }
                    for (postSnapshot in snapshot.children) {
                        val tieude = postSnapshot.child("tieude").getValue(String::class.java) ?: ""
                        val loaixe = postSnapshot.child("loaixe").getValue(String::class.java) ?: ""
                        val namsx = postSnapshot.child("namsx").getValue(Int::class.java) ?: 0
                        val giaban = postSnapshot.child("giaban").getValue(Double::class.java) ?: 0.0
                        val user_id = postSnapshot.child("user_id").getValue(String::class.java) ?: ""
                        val url = postSnapshot.child("Url").getValue(String::class.java) ?: ""
                        val key = postSnapshot.key ?: ""
                        val post = Post(tieude, loaixe, namsx, giaban, user_id, url, key)
                        posts.add(post)
                    }
                    postAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting == true
    }

    private fun showDeleteConfirmationDialog(post: Post, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa bài đăng \"${post.tieude}\"?")
            .setPositiveButton("Có") { _, _ ->
                deletePost(post, position)
            }
            .setNegativeButton("Không", null)
            .setCancelable(true)
            .show()
    }

    private fun deletePost(post: Post, position: Int) {
        if (post.key.isNotEmpty()) {
            firebaseRefPost.child(post.key).removeValue()
                .addOnSuccessListener {
                    fetchPosts()
                    Toast.makeText(requireContext(), "Xóa bài đăng thành công", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { error ->
                    Toast.makeText(requireContext(), "Lỗi khi xóa: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Không thể xóa: Khóa bài đăng không hợp lệ", Toast.LENGTH_SHORT).show()
        }
    }
}