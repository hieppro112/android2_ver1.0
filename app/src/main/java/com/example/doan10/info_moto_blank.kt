package com.example.doan10

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.doan10.data.Moto
import com.example.doan10.databinding.InfoMotoFrBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [info_moto_blank.newInstance] factory method to
 * create an instance of this fragment.
 */
class info_moto_blank : Fragment() {
    private lateinit var binding:InfoMotoFrBinding
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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

        var moto_byte =  Moto(1,"","","",1,"Honda",2020,123123.132123,5,true,"ban gap xe moi","àlàklàlkẩuởiiơiủu")

        apply_dulieu(moto_byte)
    }


    fun apply_dulieu(moto: Moto){
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment info_moto_blank.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            info_moto_blank().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}