package com.example.doan10

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.doan10.databinding.QuenMkLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class quen_mk_blank : Fragment() {

    private lateinit var binding: QuenMkLayoutBinding
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = QuenMkLayoutBinding.inflate(inflater, container, false)
        firebaseRef = FirebaseDatabase.getInstance().getReference("Users")
        auth = FirebaseAuth.getInstance()
        binding.btnAcceptUsers.setOnClickListener {
            val email = binding.etForgotPassUsers.text.toString().trim()
            checEmail(email)
        }
        return binding.root
    }
    private fun checEmail(email: String) {

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etForgotPassUsers.error = "Email không hợp lệ"
            return
        }
        firebaseRef.get().addOnSuccessListener { snapshot ->
            // Duyet qua ds email trong node user
            for (user in snapshot.children) {
                val userEmail = user.child("email").value.toString()

                // neu email = nhau
                if (userEmail.equals(email, ignoreCase = true)) {
                    val role = user.child("role").getValue(Int::class.java)
                    //  user role = 0
                    if (role == 0) {
                        sendResetEmail(email)
                        return@addOnSuccessListener
                    }
                }
            }
            Toast.makeText(context, "Email không tồn tại trong danh sách", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Lỗi kết nối cơ sở dữ liệu", Toast.LENGTH_SHORT).show()
        }
    }
    private fun sendResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(context, "Đã gửi email đặt lại mật khẩu", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.dangNhap_user)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Gửi email thất bại", Toast.LENGTH_SHORT).show()
            }
    }
}