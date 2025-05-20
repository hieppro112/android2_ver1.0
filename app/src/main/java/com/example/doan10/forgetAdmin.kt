package com.example.androidadminver1

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.androidadminver1.databinding.ForgetAdminAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class forgetAdmin : Fragment() {
    private lateinit var binding: ForgetAdminAdminBinding
    private lateinit var firebaseRefAdmin: DatabaseReference
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = ForgetAdminAdminBinding.inflate(inflater, container, false)
        firebaseRefAdmin = FirebaseDatabase.getInstance().getReference("Admin")
        auth = FirebaseAuth.getInstance()
        binding.btnAcceptAdmin.setOnClickListener {
            val email = binding.etForgotPassAdmin.text.toString().trim()
            getPasswordByEmail(email)
        }
        return binding.root
    }
    private fun getPasswordByEmail(email: String) {

        // kt cau truc email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()) {
            binding.etForgotPassAdmin.error = "Email không hợp lệ"
            binding.etForgotPassAdmin.requestFocus()
            return
        }
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Đã gửi email đặt lại mật khẩu", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.loginAdmin)
                } else {
                    val errorMessage = task.exception?.message ?: "Đã xảy ra lỗi"
                    Toast.makeText(context, "Không gửi được email: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
    }
}