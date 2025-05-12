package com.example.doan10

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.doan10.data.user
import com.example.doan10.databinding.DangnhapUserLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class dangNhap_user : Fragment() {
    private lateinit var binding:DangnhapUserLayoutBinding
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.btnLogin.setOnClickListener {
//            findNavController().navigate(R.id.home_screen_blank)
//        }
        binding.tvQuenMatKhau.setOnClickListener {
            findNavController().navigate(R.id.quen_mk_bank)
        }
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.dangKy_user_blank)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DangnhapUserLayoutBinding.inflate(inflater,container,false)

        firebaseRef = FirebaseDatabase.getInstance().getReference("Users")
        auth = FirebaseAuth.getInstance()
        binding.btnLogin.setOnClickListener {
            Login()
        }
        return binding.root
    }

    private fun Login() {
        val email = binding.etLogin.text.toString().trim()
        val pass = binding.etConfirmPass.text.toString().trim()

        val fields = listOf(
            //to: tao mot cap gia tri (Pair) gom 2 phan: (EditText, String).
            binding.etLogin to "Vui lòng nhập Email",
            binding.etConfirmPass to "Vui lòng nhập mật khẩu",
        )
        for ((field, error) in fields) {
            if (field.text.toString().trim().isEmpty()) {
                // Gán lỗi vào dòng đó
                field.error = error
                // Đặt con trỏ vào dòng đó
                field.requestFocus()
                return
            }
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etLogin.error = "Email không hợp lệ"
            binding.etLogin.requestFocus()
            return
        }
        // Truy vấn dữ liệu
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Lấy thông tin user từ Realtime Database
                        firebaseRef.child(user.uid).get()
                            .addOnSuccessListener { dataSnapshot ->
                                if (dataSnapshot.exists()) {
                                    val role = dataSnapshot.child("role").getValue(Int::class.java)
                                    // Điều hướng theo role
                                    if (role == 0) {
                                        Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                                        findNavController().navigate(R.id.home_screen_blank)
                                    }
                                    else {
                                        Toast.makeText(context, "Đây là tài khoản admin", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Không tìm thấy dữ liệu người dùng", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Lỗi khi lấy dữ liệu người dùng: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    val exception = task.exception
                    Toast.makeText(context, "Đăng nhập thất bại: ${exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
