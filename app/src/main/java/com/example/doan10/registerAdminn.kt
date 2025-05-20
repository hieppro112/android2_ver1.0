package com.example.androidadminver1

import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.androidadminver1.databinding.RegisterAdminnLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class registerAdminn : Fragment() {

    private lateinit var binding: RegisterAdminnLayoutBinding
    private lateinit var firebaseRefAdmin: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var isVisible = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvHaveAccountAdmin.setOnClickListener {
            findNavController().navigate(R.id.loginAdmin)
        }
        binding.ivToggleConfirmPassword.setOnClickListener {
            EyeConfirm()
        }
        binding.ivToggleConfirmPasswordNew.setOnClickListener {
            EyeConfirmNew()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = RegisterAdminnLayoutBinding.inflate(inflater,container,false)
        firebaseRefAdmin = FirebaseDatabase.getInstance().getReference("Admin")
        auth = FirebaseAuth.getInstance()
        binding.btnAcceptAdmin.setOnClickListener {
            RegisterAdmin()
        }
        return binding.root
    }

    private fun RegisterAdmin() {
        val userName = binding.etNameRegister.text.toString().trim()
        val email = binding.etRegister.text.toString().trim()
        val pass = binding.etConfirmPass.text.toString().trim()
        val rePass = binding.etReEnterPass.text.toString().trim()


        val fields = listOf(
            binding.etNameRegister to "Vui lòng nhập tên đăng nhập",
            binding.etRegister to "Vui lòng nhập Email",
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
            binding.etRegister.error = "Email không hợp lệ"
            binding.etRegister.requestFocus()
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
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val adminId = auth.currentUser?.uid
                        if (adminId != null) {
                            val adminData = hashMapOf(
                                "id" to adminId,
                                "adminname" to userName,
                                "email" to email,
                                "pass" to pass,
                                "role" to 1,
                                "sdt" to "",
                                "url_img" to ""
                            )
                            firebaseRefAdmin.child(adminId).setValue(adminData)
                                .addOnSuccessListener {
                                    // Gửi email xác thực
                                    val user = auth.currentUser
                                    user?.sendEmailVerification()
                                        ?.addOnCompleteListener { verificationTask ->
                                            if (verificationTask.isSuccessful) {
                                                Toast.makeText(context, "Đăng ký thành công. Vui lòng kiểm tra email để xác thực.", Toast.LENGTH_LONG).show()
                                                // Hướng dẫn người dùng tiếp tục đăng nhập
                                                findNavController().navigate(R.id.loginAdmin)
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
                            binding.etRegister.error = "Email đã được sử dụng. Vui lòng dùng email khác."
                            binding.etRegister.requestFocus()
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