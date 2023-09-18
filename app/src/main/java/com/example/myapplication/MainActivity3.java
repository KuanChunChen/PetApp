package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.databinding.ActivityMain2Binding;
import com.example.myapplication.databinding.ActivityMain3Binding;

public class MainActivity3 extends AppCompatActivity {


    private ActivityMain3Binding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    public void initView() {
        binding.includeToolbar.cvBlock.setVisibility(View.VISIBLE);
        binding.includeToolbar.ibRight.setBackground(ContextCompat.getDrawable(this, R.drawable.plus_circle));
        binding.tvFoodExample.setOnClickListener(v -> {
            MainActivity4.createIntent(this);
        });
        binding.tvCount.setOnClickListener(v -> {
            MainActivity5.createIntent(this);
        });
        binding.includeRecord.tvRecord.setOnClickListener(v -> {
            MainActivity8.createIntent(this);
        });
        binding.includeRecord.tvAnalyze.setOnClickListener(v -> {
            MainActivity8.createIntent(this);
        });
        binding.includeRecord.tvBySelf.setOnClickListener(v -> {
            MainActivity8.createIntent(this);
        });
    }

    public static void createIntent(Context context) {
        context.startActivity(new Intent(context, MainActivity3.class));
    }
}