package com.example.doan10

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.doan10.data.post
import com.example.doan10.databinding.InfoMotoFrBinding

class info_moto_blank : Fragment() {
    private lateinit var binding:InfoMotoFrBinding


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

        //var moto_byte =  post(1,"","","",1,"Honda",2020,123123.132123,5,true,"ban gap xe moi","àlàklàlkẩuởiiơiủu")
        //apply_dulieu(moto_byte)
    }


    fun apply_dulieu(moto: post){
        binding.imgMoto1.setImageResource(R.drawable.img_moto)
        binding.imgMoto2.setImageResource(R.drawable.imag_moto2)
        binding.imgMoto3.setImageResource(R.drawable.img_moto3)
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

}