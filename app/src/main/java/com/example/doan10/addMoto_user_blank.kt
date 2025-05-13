package com.example.doan10

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.doan10.data.lay_UserID
import com.example.doan10.databinding.AddMotoLayputBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class addMoto_user_blank : Fragment() {
    private lateinit var brandAdapter: ArrayAdapter<String>
    private val brandList = ArrayList<String>()
    private lateinit var binding: AddMotoLayputBinding
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var firebaseRefPost: DatabaseReference
    private lateinit var firebaseRefCar: DatabaseReference
    private lateinit var storageRef: StorageReference

    //lay du lieu id duoc truyen
    private val layUserID: lay_UserID by activityViewModels()

    // Biến để theo dõi ImageView nào đang được chọn
    private var selectedImageView: Int = 0
    private val imageUris = mutableMapOf<Int, Uri?>(
        1 to null,
        2 to null,
        3 to null
    )

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddMotoLayputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("run test", "nhan du lieu: ${layUserID.user_ID} ")

        // Khởi tạo các tham chiếu Firebase
        firebaseRef = FirebaseDatabase.getInstance().getReference()
        firebaseRefPost = FirebaseDatabase.getInstance().getReference("Post-main")
        firebaseRefCar = FirebaseDatabase.getInstance().getReference("Hangxe")
        storageRef = FirebaseStorage.getInstance().reference

        // Khởi tạo danh sách thương hiệu
        brandList.addAll(listOf("Honda", "Yamaha", "Suzuki", "Kawasaki", "Piaggio", "Khác"))

        // Khởi tạo ArrayAdapter cho Spinner
        brandAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            brandList
        )
        binding.spinnerBrand.adapter = brandAdapter

        // Xử lý sự kiện nút Xác nhận
        binding.btnConfirm.setOnClickListener {
            saveDataToFirebase()
        }

        // Xử lý sự kiện nút Thoát
        binding.btnExit.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Xử lý nút Thêm ảnh
        binding.image1.setOnClickListener {
            selectedImageView = 1
            openGallery()
        }
        binding.image2.setOnClickListener {
            selectedImageView = 2
            openGallery()
        }
        binding.image3.setOnClickListener {
            selectedImageView = 3
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val imageUri: Uri? = data?.data
            imageUris[selectedImageView] = imageUri
            when (selectedImageView) {
                1 -> binding.image1.setImageURI(imageUri)
                2 -> binding.image2.setImageURI(imageUri)
                3 -> binding.image3.setImageURI(imageUri)
            }
        }
    }

    private fun saveDataToFirebase() {
        // Lấy dữ liệu từ các trường nhập liệu
        val phone = binding.editSdt.text.toString().trim()
        val quantity = binding.editSoluong.text.toString().trim()
        val condition = when (binding.radioGroupCondition.checkedRadioButtonId) {
            R.id.radio_new -> 0 // Mới
            R.id.radio_used -> 1 // Đã sử dụng
            else -> -1
        }
        val brand = binding.spinnerBrand.selectedItem?.toString() ?: ""
        val manufacturer = binding.editManufacturer.text.toString().trim()
        val year = binding.editNamsx.text.toString().trim()
        val price = binding.editPrice.text.toString().trim()
        val title = binding.editTitle.text.toString().trim()
        val description = binding.editDescription.text.toString().trim()
        val nhasx = binding.editManufacturer.text.toString().trim()

        // Sử dụng user_id cố định
        val userId = layUserID.user_ID?:"" // Thay bằng user_id cố định bạn muốn

        // Kiểm tra dữ liệu hợp lệ
        if (phone.isEmpty() || quantity.isEmpty() || condition == -1 || brand.isEmpty() ||
            manufacturer.isEmpty() || year.isEmpty() || price.isEmpty() || title.isEmpty() ||
            description.isEmpty()
        ) {
            Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        // Kiểm tra có ít nhất 1 ảnh được chọn
        if (imageUris.values.all { it == null }) {
            Toast.makeText(requireContext(), "Vui lòng chọn ít nhất 1 ảnh!", Toast.LENGTH_SHORT).show()
            return
        }

        // Chuyển đổi dữ liệu
        val quantityInt = quantity.toIntOrNull() ?: 0
        val priceDouble = price.toDoubleOrNull() ?: 0.0
        val yearInt = year.toIntOrNull() ?: 0
        val manufacturerId = brandList.indexOf(brand).takeIf { it >= 0 } ?: 0


        // Tạo ID duy nhất cho bài đăng
        val postId = firebaseRefPost.push().key ?: return

        // Vô hiệu hóa nút xác nhận trong khi đang xử lý
        binding.btnConfirm.isEnabled = false

        uploadImages(postId) { imageUrls ->
            val url1 = imageUrls.getOrNull(0) ?: ""
            val url2 = imageUrls.getOrNull(1) ?: ""
            val url3 = imageUrls.getOrNull(2) ?: ""

            // Tạo object post theo cấu trúc class post
            val post = mapOf(
                "id" to postId,
                "Url" to url1,
                "Url2" to url2,
                "Url3" to url3,
                "tinhtrang" to condition,
                "loaixe" to brand,
                "nsx" to nhasx,
                "namsx" to yearInt,
                "giaban" to priceDouble,
                "soluong" to quantityInt,
                "ghim" to false,
                "tieude" to title,
                "mota" to description,
                "sdt" to phone,
                "user_id" to userId, // Sử dụng user_id cố định
                "duyet" to 1
            )

            // Lưu dữ liệu vào node Port_khanh
            firebaseRefPost.child(postId).setValue(post).addOnCompleteListener { task ->
                binding.btnConfirm.isEnabled = true
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Lưu bài đăng thành công!", Toast.LENGTH_SHORT).show()
                    clearInputFields()
                } else {
                    Toast.makeText(requireContext(), "Lưu bài đăng thất bại!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadImages(postId: String, callback: (List<String?>) -> Unit) {
        val imageUrls = mutableListOf<String?>(null, null, null)
        var uploadCount = 0
        val totalImages = imageUris.count { it.value != null }

        if (totalImages == 0) {
            callback(imageUrls)
            return
        }

        imageUris.forEach { (index, uri) ->
            uri?.let {
                val fileName = "post_${postId}_${UUID.randomUUID()}.jpg"
                val imageRef = storageRef.child("post_images/$fileName")

                imageRef.putFile(it)
                    .addOnSuccessListener { taskSnapshot ->
                        imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            imageUrls[index - 1] = downloadUri.toString()
                            uploadCount++
                            if (uploadCount == totalImages) {
                                callback(imageUrls)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        uploadCount++
                        if (uploadCount == totalImages) {
                            callback(imageUrls)
                        }
                    }
            }
        }
    }

    private fun clearInputFields() {
        binding.editSdt.text.clear()
        binding.editSoluong.text.clear()
        binding.radioGroupCondition.clearCheck()
        binding.spinnerBrand.setSelection(0)
        binding.editManufacturer.text.clear()
        binding.editNamsx.text.clear()
        binding.editPrice.text.clear()
        binding.editTitle.text.clear()
        binding.editDescription.text.clear()
        binding.image1.setImageResource(R.drawable.ic_camera)
        binding.image2.setImageResource(R.drawable.ic_camera)
        binding.image3.setImageResource(R.drawable.ic_camera)
        imageUris[1] = null
        imageUris[2] = null
        imageUris[3] = null
    }
}