package com.example.doan10

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.doan10.data.user
import com.example.doan10.databinding.DangKyUserLayoutBinding
import com.example.doan10.databinding.DangnhapUserLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class dangKy_user_blank : Fragment() {
    private lateinit var binding:DangKyUserLayoutBinding
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.dangNhap_user)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.dang_ky_user_layout, container, false)
        binding = DangKyUserLayoutBinding.inflate(inflater, container, false)
        firebaseRef = FirebaseDatabase.getInstance().getReference("Users")
        auth = FirebaseAuth.getInstance()
        binding.btnAcceptUsers.setOnClickListener {
            pushData()
        }
        return binding.root
    }
    private fun pushData() {
        val userName = binding.etNameRegister.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val pass = binding.etConfirmPass.text.toString().trim()
        val rePass = binding.etReEnterPass.text.toString().trim()


        val fields = listOf(
            binding.etNameRegister to "Vui lòng nhập tên đăng nhập",
            binding.etEmail to "Vui lòng nhập Email",
            binding.etConfirmPass to "Vui lòng nhập mật khẩu",
            binding.etReEnterPass to "Vui lòng nhập lại mật khẩu"
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
        // kt cau truc email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Email không hợp lệ"
            binding.etEmail.requestFocus()
            return
        }
        if (pass.length < 6) {
            binding.etConfirmPass.error = "Mật khẩu phải từ 6 ký tự"
            binding.etConfirmPass.requestFocus()
            return
        }

        if (rePass != pass) {
            binding.etReEnterPass.error = "Mật khẩu nhập lại không khớp"
            binding.etReEnterPass.requestFocus()
            return
        }

        if(pass == rePass){
            // Đăng ký với Firebase Authentication
            // Kiểm tra username đã tồn tại chưa
            val query = firebaseRef.orderByChild("username").equalTo(userName)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Username đã tồn tại
                        binding.etNameRegister.error = "Username đã tồn tại. Vui lòng chọn tên khác."
                        binding.etNameRegister.requestFocus()
                    } else {
                        // Username chưa tồn tại, tiến hành đăng ký
                        auth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val userId = auth.currentUser?.uid
                                    if (userId != null) {
                                        val userData = hashMapOf(
                                            "id" to userId,
                                            "username" to userName,
                                            "email" to email,
                                            "role" to 0,
                                            "sdt" to "",
                                            "url_img" to ""
                                        )
                                        firebaseRef.child(userId).setValue(userData)
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                                                findNavController().navigate(R.id.dangNhap_user)
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(context, "Lỗi lưu dữ liệu: ${it.message}", Toast.LENGTH_SHORT).show()
                                            }
                                    } else {
                                        Toast.makeText(context, "Không lấy được thông tin người dùng", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    val exception = task.exception
                                    if (exception is FirebaseAuthUserCollisionException) {
                                        binding.etEmail.error = "Email đã được sử dụng. Vui lòng dùng email khác."
                                        binding.etEmail.requestFocus()
                                    } else {
                                        Toast.makeText(context, "Đăng ký thất bại: ${exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Lỗi kiểm tra username: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

}