    package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.adapter.NutrientAdapter;
import com.example.myapplication.databinding.ActivityMain7Binding;
import com.example.myapplication.util.PetFoodGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity7 extends AppCompatActivity {


    private ActivityMain7Binding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain7Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.includeToolbar.tvTitle.setText("總結");
        NutrientAdapter nutrientAdapterA = new NutrientAdapter(Collections.singletonList(""));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvExpect.setLayoutManager(layoutManager);
        binding.rvExpect.setAdapter(nutrientAdapterA);
        List<String> randomPetFoodListA = PetFoodGenerator.generateRandomPetFoodList(new Random().nextInt(6) + 3);
        nutrientAdapterA.updateList(randomPetFoodListA);

        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this);
        NutrientAdapter nutrientAdapterB = new NutrientAdapter(Collections.singletonList(""));
        binding.rvExpect2.setLayoutManager(layoutManager2);
        binding.rvExpect2.setAdapter(nutrientAdapterB);
        List<String> randomPetFoodListB = PetFoodGenerator.generateRandomPetFoodList(new Random().nextInt(6) + 3);
        nutrientAdapterB.updateList(randomPetFoodListB);

        binding.tvExit.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

    }

    public static void createIntent(Context context) {
        context.startActivity(new Intent(context, MainActivity7.class));
    }
}