package com.example.doan10

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.doan10.databinding.HomeScreenFrBinding
import com.example.doan10.databinding.MainLayoutBinding

class moto_main : AppCompatActivity() {

    private  lateinit var binding: MainLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}