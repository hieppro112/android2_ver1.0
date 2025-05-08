package com.example.doan10

import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.doan10.data.hangxe
import com.example.doan10.data.post
import com.example.doan10.data.user
import com.example.doan10.databinding.HomeScreenFrBinding
import com.example.doan10.databinding.MainLayoutBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class moto_main : AppCompatActivity() {

    private  lateinit var binding: MainLayoutBinding
    private lateinit var firebaseRef:DatabaseReference
    private lateinit var firebaseRefPost:DatabaseReference
    private lateinit var firebaseRefCar:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        var id:String="",
//        var username:String="",
//        var pass:String="",
//        var email:String="",
//        var role:Int=0,
//        var Url_img:String="",
        firebaseRef = FirebaseDatabase.getInstance().getReference("Users")
        firebaseRefPost = FirebaseDatabase.getInstance().getReference("Post")
        firebaseRefCar = FirebaseDatabase.getInstance().getReference("Hangxe")

//        importDL()
    }

    private fun importDL() {
        val id_us = firebaseRef.push().key!!
        firebaseRef.child(id_us).setValue(user(id_us,"hieppro1223","123123","lehiep@gmail.com",1,"https://firebasestorage.googleapis.com/v0/b/crudfirebase-30e96.firebasestorage.app/o/Images%2F-OPi3dqAKlKcfnbxZQcd?alt=media&token=ec5c9d80-5f05-4532-a497-6bc186d3ced7"))
            .addOnCompleteListener { Log.d("do du lieu", "passs: ") }
            .addOnFailureListener { Log.d("do du lieu", "fail: ") }

        val id_post = firebaseRefPost.push().key!!
        firebaseRefPost.child(id_post).setValue(post(id_post,"","","",0,"moi",2020,999.999,3,false,"Thanh Ly moto R1000"
        ,"dfsdfsdfgfdgoiuoiretklj" +
                    "fdgsdjfgklsdjfglsk" +
                    "dsfgk",id_us,3))
            .addOnCompleteListener { Log.d("do du lieu", "passs: ") }
            .addOnFailureListener { Log.d("do du lieu", "fail: ") }

        var listxe = ArrayList<hangxe>()
        var id_xe =""
        listxe.add(hangxe(id_xe,"yamaha"))
        listxe.add(hangxe(id_xe,"Honda"))
        listxe.add(hangxe(id_xe,"Suzuki"))
        listxe.add(hangxe(id_xe,"Amada"))

//        for (item in listxe){
//            id_xe=firebaseRefCar.push().key!!
//            var xe =hangxe(id_xe,item.name)
//
//            firebaseRefCar.child(id_xe).setValue(xe)
//                .addOnCompleteListener { Log.d("do du lieu", "passs: ") }
//                .addOnFailureListener { Log.d("do du lieu", "fail: ") }
//        }





    }
}