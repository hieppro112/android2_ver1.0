package com.example.doan10

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.doan10.data.post
import com.example.doan10.databinding.ProfileUserLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class profile_user_blank : Fragment() {

    private var _binding: ProfileUserLayoutBinding? = null
    private val binding get() = _binding!!
    private var selectedImg: Uri? = null
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storageRef: StorageReference
    private var authStateListener: FirebaseAuth.AuthStateListener? = null
    private lateinit var database: DatabaseReference

    private val yeuCauQuyen = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
        if(isGranted){
            openGoi()
        }else{
            Toast.makeText(requireContext(), "Yêu cầu quyền thất bại", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileUserLayoutBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        firebaseRef = FirebaseDatabase.getInstance().getReference("Users")
        storageRef = FirebaseStorage.getInstance().getReference("avatars")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = auth.currentUser
        if (user == null) {
            try {
                findNavController().navigate(R.id.dangNhap_user)
            } catch (e: Exception) {
                Log.e("NavigationError", "Lỗi navigation: ${e.message}")
                Toast.makeText(requireContext(), "Lỗi điều hướng: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            return
        }
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            loadTrangThai(userId)
        } else {
            Toast.makeText(requireContext(), "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show()
        }

        binding.tvXemBaiViet.setOnClickListener {
            findNavController().navigate(R.id.xem_lai_bai_dang_blank)
        }
        binding.tvSavedPosts.setOnClickListener {
            findNavController().navigate(R.id.ghimPostMain)
        }
        binding.titleImg.setOnClickListener {
            findNavController().popBackStack()
        }

        loadUserName()
        loadAvatar(user.uid)
        loadTrangThai(user.uid)

        binding.imgAvatar.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Đổi ảnh đại diện")
                .setMessage("Bạn có muốn chọn ảnh đại diện mới không?")
                .setPositiveButton("Chọn ảnh") { _, _ ->
                    checkPermissionAndOpenGallery()
                }
                .setNegativeButton("Hủy", null)
                .show()
        }

        binding.btnLogout.setOnClickListener { showThongBao() }
        binding.tvDoiMatKhau.setOnClickListener {
            try {
                findNavController().navigate(R.id.doiMatKhau_blank)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Lỗi điều hướng: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        binding.tvTroGiup.setOnClickListener {
            openGoi()
        }
    }

    override fun onStart() {
        super.onStart()
        authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                Log.d("AuthDebug", "Người dùng đã đăng nhập: ${user.uid}, ${user.email}")
                loadUserName()
                loadAvatar(user.uid)
                loadTrangThai(user.uid)
            } else {
                Log.d("AuthDebug", "Chưa đăng nhập")
                try {
                    findNavController().navigate(R.id.dangNhap_user)
                } catch (e: Exception) {
                    Log.e("NavigationError", "Lỗi navigation: ${e.message}")
                }
            }
        }
        auth.addAuthStateListener(authStateListener!!)
    }

    override fun onStop() {
        super.onStop()
        authStateListener?.let { auth.removeAuthStateListener(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Hàm kiểm tra và yêu cầu quyền
    private fun checkPermissionAndOpenGallery() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (requireContext().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    // mở giao diện thư viện ảnh
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(intent)
    }
    // ActivityResultLauncher cho việc chọn ảnh
    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedImg = data?.data
            selectedImg?.let {
                binding.imgAvatar.setImageURI(it) // Hiển thị tạm thời
                uploadImageToStorage(it) // Tải lên Firebase Storage
            } ?: run {
                Log.e("PickImageError", "Không lấy được URI của ảnh")
                Toast.makeText(requireContext(), "Không lấy được ảnh", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ActivityResultLauncher cho việc yêu cầu quyền
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openImagePicker()
        } else {
            Toast.makeText(requireContext(), "Cần cấp quyền để truy cập ảnh", Toast.LENGTH_SHORT).show()
        }
    }


    private fun uploadImageToStorage(imageUri: Uri) {
        val userId = auth.currentUser?.uid ?: run {
            Log.e("AuthError", "Không tìm thấy userId")
            Toast.makeText(requireContext(), "Chưa đăng nhập", Toast.LENGTH_SHORT).show()
            return
        }
        val fileRef = storageRef.child("$userId.jpg")
        fileRef.putFile(imageUri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    Log.d("UploadDebug", "URL ảnh: $imageUrl")
                    firebaseRef.child(userId).child("url_img").setValue(imageUrl)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Đã tải lên thành công", Toast.LENGTH_SHORT).show()
                            // Tải lại ảnh từ URL sau khi lưu thành công
                            loadAvatar(userId)
                        }
                        .addOnFailureListener {
                            Log.e("DatabaseError", "Lỗi lưu URL: ${it.message}")
                            Toast.makeText(requireContext(), "Lỗi lưu URL: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }.addOnFailureListener {
                    Log.e("StorageError", "Lỗi lấy URL: ${it.message}")
                    Toast.makeText(requireContext(), "Lỗi lấy URL: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Log.e("StorageError", "Lỗi tải ảnh lên: ${it.message}")
                Toast.makeText(requireContext(), "Lỗi tải ảnh lên: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadAvatar(userId: String) {
        firebaseRef.child(userId).child("url_img")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val url = snapshot.getValue(String::class.java)
                    if (!url.isNullOrEmpty()) {
                        Log.d("UploadDebug", "Tải ảnh từ URL: $url")
                        // Sử dụng Glide để tải ảnh từ URL
                        Glide.with(requireContext())
                            .load(url)
                            .placeholder(R.drawable.ic_profile) // trong quá trình tải ảnh
                            .error(R.drawable.error) // hiển thị icon lỗi khi ko tải đc ảnh
                            .into(binding.imgAvatar)
                    } else {
                        Log.d("UploadDebug", "Không tìm thấy URL ảnh cho userId: $userId")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Lỗi tải URL ảnh: ${error.message}")
                }
            })
    }

    private fun loadUserName() {
        val user = auth.currentUser
        Log.d("AuthDebug", "Nguoi dung hien tai: ${user?.uid}, Email: ${user?.email}")
        if (user != null) {
            val userId = user.uid
            val database = FirebaseDatabase.getInstance().getReference("Users").child(userId)
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val username = snapshot.child("username").getValue(String::class.java)
                        binding.tvUserName.text = username ?: "không có tên"
                    } else {
                        binding.tvUserName.text = "Không tìm thấy dữ liệu"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Lỗi: ${error.message}")
                    binding.tvUserName.text = "Không tải được tên"
                }
            })
        } else {
            binding.tvUserName.text = "chưa đăng nhập"
        }
    }

    private fun loadTrangThai(userId: String) {
        val database = FirebaseDatabase.getInstance().getReference("Post-main")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var daDuyet = 0
                var doiDuyet = 0
                var tuChoi = 0

                for (postSnapshot in snapshot.children) {
                    val p = postSnapshot.getValue(post::class.java)
                    if (p != null && p.user_id == userId) {
                        when (p.duyet) {
                            1 -> doiDuyet++
                            2 -> daDuyet++
                            3 -> tuChoi++

                            else -> Log.w("loadTrangThai", "Trạng thái không xác định: ${p.duyet}")
                        }
                        Log.d("trangThai", "daDuyet: ${daDuyet}, doiDuyet: ${doiDuyet}, tuChoi: ${tuChoi}")
                    }
                }
                binding.tvDaDuyet.text = daDuyet.toString()
                binding.tvDoiduyet.text = doiDuyet.toString()
                binding.tvTuchoi.text = tuChoi.toString()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Lỗi tải dữ liệu: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun showThongBao() {
        AlertDialog.Builder(requireContext())
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
            .setPositiveButton("Đồng ý") { _, _ -> dangXuat() }
            .setNegativeButton("Không", null)
            .show()
    }



    private fun dangXuat() {
        auth.signOut()
        Toast.makeText(requireContext(), "Bạn đã đăng xuất", Toast.LENGTH_SHORT).show()
        try {
            findNavController().navigate(R.id.dangNhap_user)
        } catch (e: Exception) {
            Log.e("NavigationError", "Lỗi navigation: ${e.message}")
        }
    }
    private fun openGoi(){
        val phone = "0898430927"
        binding.tvTroGiup.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                setData(Uri.parse("tel:$phone"))
            }
            startActivity(intent)
        }

    }
}