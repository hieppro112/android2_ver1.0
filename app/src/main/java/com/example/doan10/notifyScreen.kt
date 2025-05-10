package com.example.doan10

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doan10.data.notify
import com.example.doan10.databinding.NotifyScreenLayoutBinding
import com.example.doan10.rv.AdapterNotify
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class notifyScreen : Fragment() {
    private lateinit var firebaseRef:DatabaseReference

    private lateinit var listNotify:ArrayList<notify>

    private lateinit var binding:NotifyScreenLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = NotifyScreenLayoutBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseRef=FirebaseDatabase.getInstance().getReference("Notify")

        listNotify= arrayListOf()

        val adapter = AdapterNotify(listNotify)
        binding.rvList.adapter=adapter
        binding.rvList.layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.home_screen_blank)
        }

        hienthithongbao()
    }

    private fun hienthithongbao() {
        firebaseRef.addValueEventListener(object  :ValueEventListener{
            override fun onDataChange(snap: DataSnapshot) {
                listNotify.clear()
                if (snap.exists()){
                    for (item in snap.children){
                        val thongbao = item.getValue(notify::class.java)
                        listNotify.add(thongbao!!)
                    }

                    binding.rvList.adapter?.notifyDataSetChanged()
                }
                else{
                    Log.d("hienthi", "khong cos du lieu: ")
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("hienthi", "co loi: ")
            }

        })
    }


}