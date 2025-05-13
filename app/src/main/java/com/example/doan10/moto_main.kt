package com.example.doan10

import android.Manifest
import android.content.pm.PackageManager
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
    private val REQ=99
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
        firebaseRef = FirebaseDatabase.getInstance().getReference("Users-thuan")
        firebaseRefPost = FirebaseDatabase.getInstance().getReference("Post")
        firebaseRefCar = FirebaseDatabase.getInstance().getReference("Hangxe")

//        importDL()


        if (checkPermission(Manifest.permission.CALL_PHONE)){
            Log.d("hieppro", "1: ")
        }
        else{
            requestPermissions(arrayOf<String>(Manifest.permission.CALL_PHONE),REQ)
        }
    }

    private fun checkPermission(permission: String):Boolean {
        val check =checkSelfPermission(permission)
        return check== PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode==REQ&& permissions.size==grantResults.size){
            for (item in grantResults){
                if (item!=PackageManager.PERMISSION_GRANTED){
                    return
                }
            }
            Log.d("hieppro", "Chot: ")
        }

    }

    private fun importDL() {
        val id_us = firebaseRef.push().key!!
//        firebaseRef.child(id_us).setValue(user(id_us,"hieppro1223","123123","0898415185","lehiep@gmail.com","",1))
//            .addOnCompleteListener { Log.d("do du lieu", "passs: ") }
//            .addOnFailureListener { Log.d("do du lieu", "fail: ") }

//        val id_post = firebaseRefPost.push().key!!
//        firebaseRefPost.child(id_post).setValue(post(id_post,"","","",0,"moi",2020,999.999,3,false,"Xe BMW Tu Thai"
//        ,"dfsdfsdfgfdgoiuoiretklj" +
//                    "fdgsdjfgklsdjfglsk" +
//                    "dsfgk",id_us,"lkl"))
//            .addOnCompleteListener { Log.d("do du lieu", "passs: ") }
//            .addOnFailureListener { Log.d("do du lieu", "fail: ") }

//        var listxe = ArrayList<hangxe>()
//        var id_xe =""
//        listxe.add(hangxe(id_xe,"yamaha"))
//        listxe.add(hangxe(id_xe,"Honda"))
//        listxe.add(hangxe(id_xe,"Suzuki"))
//        listxe.add(hangxe(id_xe,"Amada"))
//
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