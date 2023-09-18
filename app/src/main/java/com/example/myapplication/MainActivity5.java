package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.NutrientNoInputAdapter;
import com.example.myapplication.databinding.ActivityMain5Binding;
import com.example.myapplication.model.NutritionFacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity5 extends AppCompatActivity {


    private ActivityMain5Binding binding;
    private NutrientNoInputAdapter nutrientAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain5Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.includeToolbar.tvTitle.setText("營養計算");
        binding.includeToolbar.cvBlock.setVisibility(View.VISIBLE);
        binding.includeExample.tvTitle.setText("選用食物");
        binding.includeFreeInput.tvTitle.setText("克數:");
        binding.tvRvTitle.setText("營養素");

        binding.ivQuestion.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Show something here.", Toast.LENGTH_SHORT).show();

        });

        nutrientAdapter = new NutrientNoInputAdapter(Collections.singletonList(""));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvNutrients.setLayoutManager(layoutManager);
        binding.rvNutrients.setAdapter(nutrientAdapter);


        List<String> itemList = new ArrayList<>();
        itemList.add("寵物日糧");
        itemList.add("日糧伴侶");
        itemList.add("功能性糧");
        itemList.add("品種專用糧");
        itemList.add("寵物保健食品");
        itemList.add("處方食品");
        itemList.add("寵物零食");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.includeExample.spinner.setAdapter(adapter);
        binding.includeExample.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedString = parent.getItemAtPosition(position).toString();
                NutritionFacts nutritionFacts = NutritionFacts.generateFacts(selectedString);
                nutrientAdapter.updateList(nutritionFacts.toStringList());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.tvSave.setText("再新增食物");
        binding.tvSave.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "已新增食物", Toast.LENGTH_SHORT).show();
        });

        binding.tvNextRecord.setText("結算");
        binding.tvNextRecord.setOnClickListener(v -> {
            MainActivity6.createIntent(this);
        });

    }

    public static void createIntent(Context context) {
        context.startActivity(new Intent(context, MainActivity5.class));
    }
}