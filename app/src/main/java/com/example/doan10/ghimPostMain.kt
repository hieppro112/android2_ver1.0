package com.example.doan10

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil.Callback
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doan10.data.lay_UserID
import com.example.doan10.data.post
import com.example.doan10.data.user
import com.example.doan10.databinding.GhimPostLayoutBinding
import com.example.doan10.databinding.ItemPostGhimBinding
import com.example.doan10.rv.AdapterghimPost
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.callbackFlow

class ghimPostMain : Fragment() {

    private lateinit var listId:ArrayList<String>
    private lateinit var listGhim:ArrayList<post>

//    private val layid:lay_UserID by activityViewModels()
    private val layUserID:lay_UserID by activityViewModels()

    private lateinit var firebasePostGhim:DatabaseReference
    private lateinit var firebasePost:DatabaseReference
    private lateinit var adapter:AdapterghimPost

    private lateinit var binding:GhimPostLayoutBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val user:String = layUserID.user_ID.toString()?:""
        Log.d("tesst id du lieu", "id = ${user}: ")

        firebasePostGhim = FirebaseDatabase.getInstance().getReference("PostGhim").child("$user")
        firebasePost=FirebaseDatabase.getInstance().getReference("Post-main")
        binding = GhimPostLayoutBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listId= ArrayList<String>()
        listGhim= ArrayList<post>()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        adapter= AdapterghimPost(listGhim,layUserID)
        binding.rvPostGhim.adapter=adapter
        binding.rvPostGhim.layoutManager=LinearLayoutManager(requireContext())


        get_list_id { listId->
            //cac su kien xu ly bat dong bo hoan thanh
            for (item in listId){
                firebasePost.child(item).addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snap: DataSnapshot) {
                        if (snap.exists()){
//                            for (item in snap.children){
                                var postGhim=snap.getValue(post::class.java)
                                postGhim?.let {
                                    listGhim.add(it)
                                    Log.d("ds_ghim", "$it: ")
                                }
                                Log.d("ds_ghim", "$item: ")
//                            }
                            binding.rvPostGhim.adapter?.notifyDataSetChanged()
                            binding.tvSoluong.text = listGhim.size.toString()
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {
                        Log.d("ds_id", "count = ${p0}")

                    }

                })
            }

        //xu ly khi nhan vao


        }

    }

    fun get_list_id(callback: (ArrayList<String>) -> Unit){
        var listId=ArrayList<String>()
        firebasePostGhim.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snap: DataSnapshot) {
                if (snap.exists()){
                    for (item in snap.children){
                        var a = item.key
                        a?.let {
                            listId.add(it)
                        }
                    }
                    Log.d("ds_id", "count = ${listId.size}")
                    callback(listId)
                }
                else{
                    Log.d("ds_id", "DS rong: ")
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("ds_id", "Fail: $p0: ")
            }

        })
    }

}