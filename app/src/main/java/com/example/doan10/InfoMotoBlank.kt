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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
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
//        var moto_byte =  post("123","","","",1,"Honda",2020,123123.132123,5,true,"ban gap xe moi","àlàklàlkẩuởiiơiủu")
//        apply_dulieu(moto_byte)
    }

    private fun setvalue() {
        binding.apply {

//            imgMoto1.setImageResource(R.drawable.error)
//            imgMoto2.setImageResource(R.drawable.imag_moto2)
//            imgMoto3.setImageResource(R.drawable.img_moto3)
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
    fun apply_dulieu(moto: post){
//        binding.imgMoto1.setImageResource(R.drawable.ic_chat_chiet)
//        binding.imgMoto2.setImageResource(R.drawable.imag_moto2)
//        binding.imgMoto3.setImageResource(R.drawable.img_moto3)
        binding.soluongMoto.text = moto.soluong.toString()
        binding.giabanMoto.text=moto.giaban.toString()

        var check_yeuthich=moto.ghim
        binding.yeuthichMoto.setImageResource(
            when(check_yeuthich){
                true -> R.drawable.ic_heart_chitiet
                else ->R.drawable.ic_heart_none
            }
        )


        binding.titleMoto.text=moto.tieude.toString()
        binding.motaMoto.text=moto.mota.toString()
        binding.tinhtrangMoto.text = when(moto.tinhtrang){
            0 -> "Đã qua sử dụng"
            1 -> "Mới"
            else -> "Chưa xác định"
        }

        binding.nxsMoto.text = moto.nsx.toString()
        binding.loaiMoto.text = moto.loaixe



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
        binding.yeuthichMoto.setOnClickListener {
            laydulieu()
            val user = "hiep2"
            val id = firebaseRef.push().key!!

            firebaseRef.child(user).child(agrs.idPost).setValue(0)
                .addOnCompleteListener {
                    Toast.makeText(requireContext(),"Yeu thich tin",Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(),"Yeu thich Fail",Toast.LENGTH_SHORT).show()
                }


        }
    }

    fun laydulieu(){
        var a:String ="";
        Log.d("value_hiep", " value = $a: ")
        val idcuthe = "hiep2";
        firebaseRef.child(idcuthe).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snap: DataSnapshot) {
                var listkey = ArrayList<String>()
                if (snap.exists()){
                    for (item in snap.children){
                        var a = item.key
                        a?.let {
                            listkey.add(a.toString())
                        }
                    }

                    for (item in listkey)
                    {
                        Log.d("value_hiep", "item = $item: ")
                    }
                }
                else{
                    Log.d("value_hiep", " value = null: ")
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("value hiep", "Failk: ")
            }

        })

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
                .error(R.drawable.error)
                .into(binding.imgMoto1)
        }
        agrs.UrlImg2?.let {
            Glide.with(this)
                .load(it)
                .error(R.drawable.error)
                .into(binding.imgMoto2)
        }
        agrs.UrlImg3?.let {
            Glide.with(this)
                .load(it)
                .error(R.drawable.error)
                .into(binding.imgMoto3)
        }

    }
}