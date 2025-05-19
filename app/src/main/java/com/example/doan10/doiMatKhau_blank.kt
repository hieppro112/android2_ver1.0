package com.example.doan10

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.doan10.databinding.DoiMatKhauLayoutBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class doiMatKhau_blank : Fragment() {

    private var _binding: DoiMatKhauLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = DoiMatKhauLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()


        binding.btnDoiMatKhau.setOnClickListener {
            doiMatKhau()
        }
        binding.btnThoat.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun doiMatKhau() {
        val oldPass = binding.etDoiMatKhauMkcu.text.toString().trim()
        val newPass = binding.etDoiMatKhauMkmoi.text.toString().trim()
        val confirmPass = binding.etDoiMatKhauComfimrPass.text.toString().trim()

        val user = auth.currentUser
        val email = user?.email

        // Kiểm tra đầu vào
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }
        if (newPass != confirmPass) {
            Toast.makeText(requireContext(), "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show()
            return
        }
        if (email == null) {
            Toast.makeText(requireContext(), "Không tìm thấy email người dùng", Toast.LENGTH_SHORT).show()
            return
        }

        val xacThuc = EmailAuthProvider.getCredential(email, oldPass)
        user.reauthenticate(xacThuc)
            .addOnSuccessListener {
                user.updatePassword(newPass)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                        // Đăng xuất và chuyển về màn hình đăng nhập
                        auth.signOut()
                        try {
                            findNavController().navigate(R.id.dangNhap_user)
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Lỗi điều hướng: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Lỗi cập nhật mật khẩu: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                // Xác thực thất bại (mật khẩu cũ không đúng hoặc lỗi khác)
                Toast.makeText(requireContext(), "Xác thực thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



}