package com.example.doan10.rv

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doan10.R
import com.example.doan10.data.lay_UserID
import com.example.doan10.data.post
import com.example.doan10.data.postGhim
import com.example.doan10.databinding.ItemPostGhimBinding
import com.example.doan10.ghimPostMain
import com.example.doan10.ghimPostMainDirections
import com.example.doan10.home_screen_blankDirections
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase

class AdapterghimPost(val ds:ArrayList<post>,val layUserid: lay_UserID):RecyclerView.Adapter<AdapterghimPost.ViewHolder>() {
    inner class ViewHolder(val binding:ItemPostGhimBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val binding= ItemPostGhimBinding.inflate(LayoutInflater.from(p0.context),p0,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val itemPost = ds[pos]

        with(holder.binding){

            tvGhimPost.text=itemPost.tieude

            val user:String = layUserid.user_ID.toString()

            layoutPostGhim.setOnLongClickListener {
                MaterialAlertDialogBuilder(holder.itemView.context)
                    .setTitle("Bỏ Ghim")
                    .setMessage("Bạn muốn bỏ Ghim ${itemPost.tieude}")
                    .setPositiveButton("yes"){_,_->
                        val fireBaseRef = FirebaseDatabase.getInstance().getReference("PostGhim").child(user)
                        Log.d("delete", "id = ${itemPost.id.toString()}: ")
                        fireBaseRef.child(itemPost.id.toString()).removeValue()
                            .addOnCompleteListener { Log.d("delete", "delete succses: ") }
                            .addOnFailureListener { Log.d("delete", "delete Fail: ") }
                    }
                    .setNegativeButton("No"){_,_->
                        Log.d("delete", "Cancel: ")
                    }
                    .show()
                return@setOnLongClickListener true
            }

            if (itemPost.Url.isNotEmpty()){
                Glide.with(holder.itemView)
                    .load(itemPost.Url)
                    .centerCrop()
                    .error(R.drawable.error)
                    .into(imgGhimPost)
            }
            else if (itemPost.Url2.isNotEmpty()){
                Glide.with(holder.itemView)
                    .load(itemPost.Url2)
                    .centerCrop()
                    .error(R.drawable.error)
                    .into(imgGhimPost)
            }
            else if (itemPost.Url3.isNotEmpty()){
                Glide.with(holder.itemView)
                    .load(itemPost.Url3)
                    .centerCrop()
                    .error(R.drawable.error)
                    .into(imgGhimPost)
            }
            else {
                Glide.with(holder.itemView)
                    .load(R.drawable.error)
                    .into(imgGhimPost)
            }

            //khii nhan vao item
            layoutPostGhim.setOnClickListener {
                val action = ghimPostMainDirections.actionGhimPostMainToInfoMotoBlank(
                    itemPost.id,
                    itemPost.Url3,
                    itemPost.soluong,
                    itemPost.giaban.toString(), // Thử truyền String tĩnh
                    itemPost.ghim,
                    itemPost.tieude,
                    itemPost.mota,
                    itemPost.tinhtrang,
                    itemPost.loaixe,
                    itemPost.namsx,
                    itemPost.sdt,
                    itemPost.nsx.toString(),
                    itemPost.Url.toString(),
                    itemPost.Url2.toString(),
                )
                Navigation.findNavController(holder.itemView).navigate(action)
            }

        }
    }
}