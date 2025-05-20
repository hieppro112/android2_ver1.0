package com.example.doan10

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.doan10.databinding.SuaBaiDangLayoutBinding
import com.google.firebase.database.*

class sua_bai_dang_blank : Fragment() {
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var firebaseRefPost: DatabaseReference
    private lateinit var firebaseRefCar: DatabaseReference
    private lateinit var binding: SuaBaiDangLayoutBinding
    private val args: sua_bai_dang_blankArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SuaBaiDangLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseRef = FirebaseDatabase.getInstance().getReference()
        firebaseRefPost = FirebaseDatabase.getInstance().getReference("Post-main")
        firebaseRefCar = FirebaseDatabase.getInstance().getReference("Car")

        // Thiết lập Spinner với danh sách xe
        val brands = arrayOf("Honda", "Yamaha", "Suzuki", "Kawasaki", "Piaggio", "Khác")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, brands)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBrand.adapter = adapter

        fetchPostData(args.postKey)

        // Thêm sự kiện click cho các ImageButton để hiển thị thông báo
        binding.image1.setOnClickListener {
            Toast.makeText(requireContext(), "Bạn chỉ được sửa thông tin khác, không được sửa ảnh", Toast.LENGTH_SHORT).show()
        }
        binding.image2.setOnClickListener {
            Toast.makeText(requireContext(), "Bạn chỉ được sửa thông tin khác, không được sửa ảnh", Toast.LENGTH_SHORT).show()
        }
        binding.image3.setOnClickListener {
            Toast.makeText(requireContext(), "Bạn chỉ được sửa thông tin khác, không được sửa ảnh", Toast.LENGTH_SHORT).show()
        }

        binding.btnExit.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnConfirm.setOnClickListener {
            updatePost()
        }
    }

    private fun fetchPostData(postKey: String) {
        if (postKey.isEmpty()) {
            Toast.makeText(requireContext(), "Khóa bài đăng không hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseRefPost.child(postKey).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(requireContext(), "Bài đăng không tồn tại", Toast.LENGTH_SHORT).show()
                    return
                }

                Log.d("sua_bai_dang_blank", "Snapshot: ${snapshot.value}")

                try {
                    val id = snapshot.child("id").getValue(String::class.java) ?: ""
                    val url = snapshot.child("Url").getValue(String::class.java) ?: ""
                    val url2 = snapshot.child("Url2").getValue(String::class.java) ?: ""
                    val url3 = snapshot.child("Url3").getValue(String::class.java) ?: ""
                    val tinhtrang = snapshot.child("tinhtrang").getValue(Int::class.java) ?: 0
                    val loaixe = snapshot.child("loaixe").getValue(String::class.java) ?: ""
                    val namsxRaw = snapshot.child("namsx").getValue() ?: 0
                    val namsx = when (namsxRaw) {
                        is Long -> namsxRaw.toInt()
                        is String -> namsxRaw.toIntOrNull() ?: 0
                        else -> namsxRaw as Int
                    }
                    val nsx = snapshot.child("nsx").getValue(String::class.java) ?: ""
                    val giabanRaw = snapshot.child("giaban").getValue() ?: 0.0
                    val giaban = when (giabanRaw) {
                        is Long -> giabanRaw.toDouble()
                        is String -> giabanRaw.toDoubleOrNull() ?: 0.0
                        else -> giabanRaw as Double
                    }
                    val soluong = snapshot.child("soluong").getValue(Int::class.java) ?: 0
                    val ghim = snapshot.child("ghim").getValue(Boolean::class.java) ?: false
                    val tieude = snapshot.child("tieude").getValue(String::class.java) ?: ""
                    val mota = snapshot.child("mota").getValue(String::class.java) ?: ""
                    val sdt = snapshot.child("sdt").getValue(String::class.java) ?: ""
                    val userId = snapshot.child("user_id").getValue(String::class.java) ?: ""
                    val duyet = snapshot.child("duyet").getValue(Int::class.java) ?: 2

                    binding.editTitle.setText(tieude)
                    binding.spinnerBrand.setSelection(getSpinnerIndex(binding.spinnerBrand, loaixe))
                    binding.editNamsx.setText(namsx.toString())
                    binding.editPrice.setText(giaban.toString())
                    binding.editSdt.setText(sdt)
                    binding.editSoluong.setText(soluong.toString())
                    binding.editManufacturer.setText(nsx)
                    binding.editDescription.setText(mota)
                    binding.radioGroupCondition.check(
                        if (tinhtrang == 0) R.id.radio_new else R.id.radio_used
                    )

                    loadImage(binding.image1, url, 1)
                    loadImage(binding.image2, url2, 2)
                    loadImage(binding.image3, url3, 3)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Lỗi khi lấy dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("sua_bai_dang_blank", "Error: ${e.message}, Snapshot: ${snapshot.value}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("sua_bai_dang_blank", "Firebase error: ${error.message}")
            }
        })
    }

    private fun loadImage(imageView: ImageView, url: String, position: Int) {
        if (url.isNotEmpty()) {
            Log.d("sua_bai_dang_blank", "Tải ảnh từ URL: $url (position: $position)")
            Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(android.R.drawable.ic_menu_gallery)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(imageView)
                .clearOnDetach()
        } else {
            Log.d("sua_bai_dang_blank", "URL rỗng tại position: $position")
            imageView.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    private fun updatePost() {
        val updatedTitle = binding.editTitle.text.toString().trim()
        val updatedLoaixe = binding.spinnerBrand.selectedItem.toString()
        val updatedNamsx = binding.editNamsx.text.toString().toIntOrNull() ?: 0
        val updatedGiaban = binding.editPrice.text.toString().toDoubleOrNull() ?: 0.0
        val updatedSdt = binding.editSdt.text.toString().trim()
        val updatedSoluong = binding.editSoluong.text.toString().toIntOrNull() ?: 0
        val updatedNsx = binding.editManufacturer.text.toString().trim()
        val updatedMota = binding.editDescription.text.toString().trim()
        val updatedTinhtrang = if (binding.radioGroupCondition.checkedRadioButtonId == R.id.radio_new) 0 else 1

        if (updatedTitle.isEmpty() || updatedLoaixe.isEmpty() || updatedNamsx == 0 || updatedGiaban == 0.0 || updatedSdt.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show()
            return
        }

        updatePostInFirebase(
            updatedTitle, updatedLoaixe, updatedNamsx, updatedGiaban, updatedSdt,
            updatedSoluong, updatedNsx, updatedMota, updatedTinhtrang
        )
    }

    private fun updatePostInFirebase(
        title: String, loaixe: String, namsx: Int, giaban: Double, sdt: String,
        soluong: Int, nsx: String, mota: String, tinhtrang: Int
    ) {
        val updates = mapOf(
            "tieude" to title,
            "loaixe" to loaixe,
            "namsx" to namsx,
            "giaban" to giaban,
            "sdt" to sdt,
            "soluong" to soluong,
            "nsx" to nsx,
            "mota" to mota,
            "tinhtrang" to tinhtrang
        )

        Log.d("sua_bai_dang_blank", "Cập nhật Firebase với updates: $updates")
        firebaseRefPost.child(args.postKey).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Cập nhật bài đăng thành công", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            .addOnFailureListener { error ->
                Log.e("sua_bai_dang_blank", "Lỗi cập nhật Firebase: ${error.message}")
                Toast.makeText(requireContext(), "Lỗi khi cập nhật: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getSpinnerIndex(spinner: Spinner, value: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(value, ignoreCase = true)) {
                return i
            }
        }
        return spinner.count - 1 // Chọn "Khác" nếu không tìm thấy
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Glide tự động hủy tải ảnh khi view bị hủy
    }
}