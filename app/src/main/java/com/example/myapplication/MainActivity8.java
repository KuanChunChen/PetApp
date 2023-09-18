package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.databinding.ActivityMain3Binding;
import com.example.myapplication.databinding.ActivityMain8Binding;

public class MainActivity8 extends AppCompatActivity {

    private ActivityMain8Binding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain8Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.tvBack.setOnClickListener(v -> finish());
        binding.includeToolbar.cvBlock.setVisibility(View.VISIBLE);
        binding.includeSnake.ivDot.setColorFilter(new LightingColorFilter(Color.RED, 1));
        binding.includeSnake.tvTitle.setText("零食");
        binding.includeSnake2.ivDot.setColorFilter(new LightingColorFilter(Color.BLUE, 1));
        binding.includeSnake2.tvTitle.setText("乾式");
        binding.includeSnake3.ivDot.setColorFilter(new LightingColorFilter(Color.GREEN, 1));
        binding.includeSnake3.tvTitle.setText("罐頭");
    }

    public static void createIntent(Context context) {
        context.startActivity(new Intent(context, MainActivity8.class));
    }
}