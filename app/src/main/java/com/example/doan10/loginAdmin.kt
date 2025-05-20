package com.example.androidadminver1

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.androidadminver1.databinding.LoginAdminLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class loginAdmin : Fragment() {
    private lateinit var binding: LoginAdminLayoutBinding
    private lateinit var firebaseRefAdmin: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var isVisible = false

    //private val layUserID:lay_UserID by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvQuenMatKhau.setOnClickListener {
            findNavController().navigate(R.id.forgetAdmin)
        }
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.registerAdminn)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoginAdminLayoutBinding.inflate(inflater,container,false)
        firebaseRefAdmin = FirebaseDatabase.getInstance().getReference("Admin")
        auth = FirebaseAuth.getInstance()
        binding.btnLogin.setOnClickListener {
            LoginAdmin()
        }
        binding.ivToggleConfirmPassword.setOnClickListener {
            EyeConfirm()
        }
        return binding.root
    }

    private fun LoginAdmin() {
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
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val admin = auth.currentUser
                    if (admin != null) {
                        if (admin.isEmailVerified) {
                            // Trước tiên kiểm tra role, rồi mới cập nhật
                            firebaseRefAdmin.child(admin.uid).get()
                                .addOnSuccessListener { dataSnapshot ->
                                    if (dataSnapshot.exists()) {
                                        val role = dataSnapshot.child("role").getValue(Int::class.java)
                                        if (role == 1) {
                                            // cap nhat lai vao admin
                                            val updates = mapOf(
                                                "email" to email,
                                                "pass" to pass,
                                                "role" to role
                                            )
                                            firebaseRefAdmin.child(admin.uid).updateChildren(updates)
                                                .addOnSuccessListener {
                                                    Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                                                    truyenID(email)
                                                    findNavController().navigate(R.id.homeAdmin)
                                                }
                                                .addOnFailureListener {
                                                    Toast.makeText(context, "Lỗi khi cập nhật dữ liệu admin: ${it.message}", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    } else {
                                        Toast.makeText(context, "Không tìm thấy dữ liệu", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Lỗi khi lấy dữ liệu admin: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(context, "Vui lòng xác minh email trước khi đăng nhập.", Toast.LENGTH_LONG).show()
                            admin.sendEmailVerification()
                                .addOnCompleteListener { verifyTask ->
                                    if (verifyTask.isSuccessful) {
                                        Toast.makeText(context, "Đã gửi lại email xác minh.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Không thể gửi email xác minh: ${verifyTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            auth.signOut()
                        }
                    }
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthInvalidUserException) {
                        Toast.makeText(context, "Tài khoản không tồn tại hoặc đã bị xóa", Toast.LENGTH_SHORT).show()
                    } else if (exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(context, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Đăng nhập thất bại: ${exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }



    }
    private fun EyeConfirm() {
        if (isVisible) {
            // Ẩn mật khẩu
            binding.etConfirmPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye)
        } else {
            // Hiện mật khẩu
            binding.etConfirmPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye_close)
        }
        isVisible = !isVisible
        binding.etConfirmPass.setSelection(binding.etConfirmPass.text.length)
    }
    private fun truyenID(id:String){
        var a:String=""
        val firetest = FirebaseDatabase.getInstance().getReference("Admin")
        firetest.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {
                if (snap.exists()){
                    Log.d("run test", "snapo co du lieu : ")
                    for (item in snap.children){
                        item?.let {
                            Log.d("run test", "item = ${item.child("email").getValue(String::class.java)} ")
                            if (item.child("email").getValue(String::class.java)==id){
                                a=item.key!!.toString()
                                Log.d("run test", "a = $a: ")
                                //layUserID.user_ID=a
                            }


//                            for (i in item.children){
//                                Log.d("run test", "gia tri trong item con $i: ")
//
//                            }F
                        }
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

//        val acAddpost = dangNhap_userDirections.actionDangNhapUserToAddMotoScreen(
//            id.toString()
//        )
//        val acXemPost = dangNhap_userDirections.actionDangNhapUserToXemLaiBaiDangBlank(
//            id.toString()
//        )
    }
}
