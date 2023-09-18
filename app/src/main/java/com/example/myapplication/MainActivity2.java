package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityMain2Binding;

public class MainActivity2 extends AppCompatActivity {

    private ActivityMain2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    public void initView() {
        binding.includeToolbar.tvTitle.setText("基本資料");
        binding.includeProfile.includeName.tvTitle.setText("名字");
        binding.includeProfile.includeSpecies.tvTitle.setText("物種");
        binding.includeProfile.includeGender.tvTitle.setText("性別");
        binding.includeProfile.includeBirth.tvTitle.setText("生日");
        binding.includeProfile.includeVariety.tvTitle.setText("品種");
        binding.includeProfile.includeBodyShape.tvTitle.setText("體型");
        binding.includeProfile.includeWeight.tvTitle.setText("重量");
        binding.includeProfile.tvConfirm.setOnClickListener(v -> {
            MainActivity3.createIntent(this);
        });
        binding.includeProfile.ivQuestion.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "這個欄位通常用於描述一個人的身體結構或體型。", Toast.LENGTH_SHORT).show();
        });
    }


    public static void createIntent(Context context) {
        context.startActivity(new Intent(context, MainActivity2.class));
    }
}