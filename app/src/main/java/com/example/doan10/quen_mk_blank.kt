package com.example.doan10

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        // Inflate the layout for this fragment
        binding = QuenMkLayoutBinding.inflate(inflater, container, false)
        firebaseRef = FirebaseDatabase.getInstance().getReference("Users")
        auth = FirebaseAuth.getInstance()
        binding.btnAcceptUsers.setOnClickListener {
            val email = binding.etForgotPassUsers.text.toString().trim()
            getPasswordByEmail(email)
        }
        return binding.root
    }
    private fun getPasswordByEmail(email: String) {

        // kt cau truc email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()) {
            binding.etForgotPassUsers.error = "Email không hợp lệ"
            binding.etForgotPassUsers.requestFocus()
            return
        }
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Đã gửi email đặt lại mật khẩu", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val errorMessage = task.exception?.message ?: "Đã xảy ra lỗi"
                    Toast.makeText(context, "Không gửi được email: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
    }
}