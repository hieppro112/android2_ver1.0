package com.example.doan10

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.doan10.data.lay_UserID
import com.example.doan10.data.post
import com.example.doan10.data.postGhim
import com.example.doan10.databinding.InfoMotoFrBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class InfoMotoBlank : Fragment() {
    private lateinit var binding:InfoMotoFrBinding
    private val agrs:InfoMotoBlankArgs by navArgs()

    private lateinit var firebaseRef:DatabaseReference

    private val layUserID:lay_UserID by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=InfoMotoFrBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chuyenanh()

        firebaseRef=FirebaseDatabase.getInstance().getReference("PostGhim")
        setvalue()
        yeuthich_moto()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun setvalue() {
        binding.apply {

            var ghim:Boolean = agrs.ghim
            var id = agrs.idPost
            val sdt = agrs.sdt
            txtNsx.text = agrs.nhasx
            soluongMoto.setText(agrs.postSoluong.toString())
            giabanMoto.setText(agrs.postGiaban)
            titleMoto.setText(agrs.postTieude)
            motaMoto.setText(agrs.postMota)
            tinhtrangMoto.text = when(agrs.postTinhtrang){
                0->"Còn mới"
                1->"Đã qua sử dụng"
                else->"Khong xac dinh"
            }
            loaiMoto.setText(agrs.postLoaixe)
            nxsMoto.setText(agrs.postNsx.toString())
            Persmission_call(agrs.sdt)
        }
    }

    //xu ly cac hoat dong lien he
    fun Persmission_call(sdt:String){
        binding.itemBtnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                setData(Uri.parse("tel:$sdt"))
            }
            startActivity(intent)
        }

        binding.btnSend.setOnClickListener {
            val mess="xin chào, tôi đang quan tâm đến sản phẩm \"${agrs.postTieude}\" qua ứng dụng XEXIN!!"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setData(Uri.parse("sms:$sdt"))
                putExtra("sms_body",mess)
            }
            startActivity(intent)
        }
    }

    fun yeuthich_moto(){

        var ischeck =false

        binding.yeuthichMoto.setOnClickListener {

            ischeck=!ischeck
//            laydulieu()
            val user =layUserID.user_ID
            val id = firebaseRef.push().key!!
            Log.d("isscheck", "$ischeck: ")

            if (ischeck==true){
                binding.yeuthichMoto.setImageResource(R.drawable.ic_heart_chitiet)
                firebaseRef.child(user).child(agrs.idPost).setValue(0)
                    .addOnCompleteListener {
                        Toast.makeText(requireContext(),"Yeu thich tin",Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(),"Yeu thich Fail",Toast.LENGTH_SHORT).show()
                    }
            }
            else{
                binding.yeuthichMoto.setImageResource(R.drawable.ic_heart_none)
                val firebaseGhim = FirebaseDatabase.getInstance().getReference("PostGhim").child(user)
                firebaseGhim.child(agrs.idPost).removeValue()
                        .addOnCompleteListener { Toast.makeText(requireContext(),"bo yeu thich",Toast.LENGTH_SHORT).show() }
            }


        }
    }


    fun chuyenanh(){
        binding.flipperInfoMoto.flipInterval=3000
        binding.flipperInfoMoto.isAutoStart=true
        binding.flipperInfoMoto.startFlipping()

        // thuc hien các hình ảnh đem về và thay đổi
        Log.d("link img", "${agrs.UrlImg} ")
        Log.d("link img", "${agrs.UrlImg2} ")
        Log.d("link img", "${agrs.UrlImg3} ")

        agrs.UrlImg?.let {
            Glide.with(this)
                .load(it)
                .centerCrop()
                .fitCenter()
                .error(R.drawable.img)
                .into(binding.imgMoto1)
        }
        agrs.UrlImg2?.let {
            Glide.with(this)
                .load(it)
                .error(R.drawable.img)
                .into(binding.imgMoto2)
        }
        agrs.UrlImg3?.let {
            Glide.with(this)
                .load(it)
                .error(R.drawable.img)
                .into(binding.imgMoto3)
        }

    }
}