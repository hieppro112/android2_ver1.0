package com.example.doan10.rv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.doan10.data.notify
import com.example.doan10.databinding.ItemNotifyBinding
import com.example.doan10.databinding.ItemProductBinding

class AdapterNotify(val ds:List<notify>):RecyclerView.Adapter<AdapterNotify.ViewHolder>() {

    inner class ViewHolder(val binding:ItemNotifyBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val binding= ItemNotifyBinding.inflate(LayoutInflater.from(p0.context),p0,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val itemNoti= ds[pos]
        with(holder.binding){

            titleNotiItem.text=itemNoti.title
            contentNotiItem.text=itemNoti.content

        }
    }
}