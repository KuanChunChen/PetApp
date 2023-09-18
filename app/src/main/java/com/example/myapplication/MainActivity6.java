package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.adapter.NutrientAdapter;
import com.example.myapplication.databinding.ActivityMain6Binding;
import com.example.myapplication.util.PetFoodGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity6 extends AppCompatActivity {

    private ActivityMain6Binding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain6Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.includeToolbar.tvTitle.setText("結算");
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

        binding.tvSave.setOnClickListener(v -> {
            MainActivity7.createIntent(this);
        });

    }

    public static void createIntent(Context context) {
        context.startActivity(new Intent(context, MainActivity6.class));
    }
}