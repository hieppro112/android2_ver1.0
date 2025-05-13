package com.example.doan10.rv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.navigation.Navigation
import androidx.navigation.Navigator
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doan10.R
import com.example.doan10.data.post
import com.example.doan10.databinding.ItemProductBinding
import com.example.doan10.home_screen_blankDirections


class ProductAdapter(var ds: List<post>) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

     inner class ViewHolder(val binding:ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val items = ds[position]
        with(holder.binding){
//            productImage.setImageURI(items.Url.toUri())
            productTitle.text=items.tieude

            if (items.Url.isNotEmpty()){
                Glide.with(holder.itemView)
                    .load(items.Url)
                    .centerCrop()
                    .error(R.drawable.error)
                    .into(productImage)
            }
            else if (items.Url2.isNotEmpty()){
                Glide.with(holder.itemView)
                    .load(items.Url2)
                    .centerCrop()
                    .error(R.drawable.error)
                    .into(productImage)
            }
            else if (items.Url3.isNotEmpty()){
                Glide.with(holder.itemView)
                    .load(items.Url3)
                    .centerCrop()
                    .error(R.drawable.error)
                    .into(productImage)
            }
            else {
                Glide.with(holder.itemView)
                    .load(R.drawable.error)
                    .into(productImage)
            }



            productItem.setOnClickListener {
//                val action = home_screen_blankDirections.actionHomeScreenBlankToInfoMotoBlank();
                val action = home_screen_blankDirections.actionHomeScreenBlankToInfoMotoBlank(
                    items.id,
                    items.Url,
                    items.soluong,
                    items.giaban.toString(), // Thử truyền String tĩnh
                    items.ghim,
                    items.tieude,
                    items.mota,
                    items.tinhtrang,
                    items.loaixe,
                    items.namsx,
                    items.sdt,
                    items.nsx,
                    items.Url2.toString(),
                    items.Url3.toString(),
                )
                Navigation.findNavController(holder.itemView).navigate(action)
            }

        }
    }

    override fun getItemCount(): Int= ds.size
    //kiet
    fun updateData(newList: List<post>) {
        ds = newList
        notifyDataSetChanged()
    }
}