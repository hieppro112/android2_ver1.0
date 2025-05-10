package com.example.doan10.rv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.doan10.data.postGhim
import com.example.doan10.databinding.ItemPostGhimBinding

class AdapterghimPost(val ds:ArrayList<postGhim>):RecyclerView.Adapter<AdapterghimPost.ViewHolder>() {
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

        }
    }
}