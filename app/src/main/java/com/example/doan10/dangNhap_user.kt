package com.example.doan10

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.doan10.data.lay_UserID
import com.example.doan10.data.user
import com.example.doan10.databinding.DangnhapUserLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class dangNhap_user : Fragment() {
    private lateinit var binding:DangnhapUserLayoutBinding
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var isVisible = false

    private val layUserID:lay_UserID by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        binding.ivToggleConfirmPassword.setOnClickListener {
            Eye()
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

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        if (user.isEmailVerified) {
                            // Lay t.tin role de xac nhan user hay admin
                            firebaseRef.child(user.uid).get()
                                .addOnSuccessListener { dataSnapshot ->
                                    if (dataSnapshot.exists()) {
                                        val role = dataSnapshot.child("role").getValue(Int::class.java)
                                        if (role == 0) {
                                            // cap nhat vao user
                                            val updates = mapOf(
                                                "email" to email,
                                                "pass" to pass
                                            )
                                            firebaseRef.child(user.uid).updateChildren(updates)
                                                .addOnSuccessListener {
                                                    Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                                                    truyenID(email)
                                                    findNavController().navigate(R.id.home_screen_blank)
                                                }
                                                .addOnFailureListener {
                                                    Toast.makeText(context, "Lỗi khi cập nhật dữ liệu người dùng: ${it.message}", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    } else {
                                        Toast.makeText(context, "Không tìm thấy dữ liệu", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Lỗi khi lấy dữ liệu người dùng: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            // Email chưa xác minh
                            Toast.makeText(context, "Vui lòng xác minh email trước khi đăng nhập.", Toast.LENGTH_LONG).show()
                            user.sendEmailVerification()
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

    private fun Eye() {
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
        val firetest = FirebaseDatabase.getInstance().getReference("Users")
        firetest.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snap: DataSnapshot) {
                if (snap.exists()){
                    Log.d("run test", "snapo co du lieu : ")
                    for (item in snap.children){
                        item?.let {
                            Log.d("run test", "item = ${item.child("email").getValue(String::class.java)} ")
                            if (item.child("email").getValue(String::class.java)==id){
                                a=item.key!!.toString()
                                Log.d("run test", "a = $a: ")
                                layUserID.user_ID=a
                            }


//                            for (i in item.children){
//                                Log.d("run test", "gia tri trong item con $i: ")
//
//                            }
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
