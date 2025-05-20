package com.example.doan10

import android.os.Bundle
import android.text.InputType
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
    private var isVisible = false

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
        binding = DangKyUserLayoutBinding.inflate(inflater, container, false)
        firebaseRef = FirebaseDatabase.getInstance().getReference("Users")
        auth = FirebaseAuth.getInstance()
        binding.btnAcceptUsers.setOnClickListener {
            pushData()
        }
        binding.ivToggleConfirmPassword.setOnClickListener {
            EyeConfirm()
        }
        binding.ivToggleConfirmPasswordNew.setOnClickListener{
            EyeConfirmNew()
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
        // Kiểm tra username
        if (userName.length > 30) {
            binding.etNameRegister.error = "Tên người dùng không được vượt quá 30 ký tự"
            binding.etNameRegister.requestFocus()
            return
        }

        if (!userName.trim().matches("^[a-zA-Z0-9 ]+$".toRegex())) {
            binding.etNameRegister.error = "Chỉ được nhập chữ, số và khoảng trắng"
            binding.etNameRegister.requestFocus()
            return
        }


        // kt cau truc email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Email không hợp lệ"
            binding.etEmail.requestFocus()
            return
        }
        if (pass.length < 6|| pass.length > 35) {
            binding.etConfirmPass.error = "Mật khẩu phải từ 6 kí tự và bé hơn 35 kí tự"
            binding.etConfirmPass.requestFocus()
            return
        }

        if (rePass != pass) {
            binding.etReEnterPass.error = "Mật khẩu nhập lại không khớp"
            binding.etReEnterPass.requestFocus()
            return
        }

        if(pass == rePass){
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            val userData = hashMapOf(
                                "id" to userId,
                                "username" to userName,
                                "email" to email,
                                "pass" to pass,
                                "role" to 0,
                                "sdt" to "",
                                "url_img" to ""
                            )
                            firebaseRef.child(userId).setValue(userData)
                                .addOnSuccessListener {
                                    // Gửi email xác thực
                                    val user = auth.currentUser
                                    user?.sendEmailVerification()
                                        ?.addOnCompleteListener { verificationTask ->
                                            if (verificationTask.isSuccessful) {
                                                Toast.makeText(context, "Đăng ký thành công. Vui lòng kiểm tra email để xác thực.", Toast.LENGTH_LONG).show()
                                                // Hướng dẫn người dùng tiếp tục đăng nhập
                                                findNavController().navigate(R.id.dangNhap_user)
                                            } else {
                                                Toast.makeText(context, "Lỗi gửi email xác thực: ${verificationTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
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
    private fun EyeConfirmNew() {
        if (isVisible) {
            // Ẩn mật khẩu
            binding.etReEnterPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.ivToggleConfirmPasswordNew.setImageResource(R.drawable.ic_eye)
        } else {
            // Hiện mật khẩu
            binding.etReEnterPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.ivToggleConfirmPasswordNew.setImageResource(R.drawable.ic_eye_close)
        }
        isVisible = !isVisible
        binding.etReEnterPass.setSelection(binding.etReEnterPass.text.length)
    }

}
