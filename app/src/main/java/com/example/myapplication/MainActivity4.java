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

import com.example.myapplication.adapter.NutrientAdapter;
import com.example.myapplication.databinding.ActivityMain4Binding;
import com.example.myapplication.model.NutritionFacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity4 extends AppCompatActivity {

    private ActivityMain4Binding binding;
    private NutrientAdapter nutrientAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain4Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.includeToolbar.tvTitle.setText("食物範本");
        binding.includeToolbar.cvBlock.setVisibility(View.VISIBLE);
        binding.includeToolbar.ibRight.setVisibility(View.GONE);
        binding.includeExample.tvTitle.setText("現有範本");
        binding.includeFreeInput.tvTitle.setText("自由新增:");
        binding.tvRvTitle.setText("營養素");

        binding.ivQuestion.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Show something here.", Toast.LENGTH_SHORT).show();

        });

        nutrientAdapter = new NutrientAdapter(Collections.singletonList(""));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvNutrients.setLayoutManager(layoutManager);
        binding.rvNutrients.setAdapter(nutrientAdapter);


        List<String> itemList = new ArrayList<>();
        itemList.add("雞肉沙拉");
        itemList.add("茶葉蛋");
        itemList.add("雞胸肉");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.includeExample.spinner.setAdapter(adapter);
        binding.includeExample.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedString = parent.getItemAtPosition(position).toString();
                switch (position) {
                    case 0:
                        NutritionFacts chickenSalad = new NutritionFacts(selectedString,20, 15, 10, 5, 3, 2, 30);
                        nutrientAdapter.updateList(chickenSalad.toStringList());
                        binding.ivFood.setVisibility(View.GONE);
                        binding.includeToolbar.ibRight.setVisibility(View.GONE);
                        binding.includeToolbar.cvBlock.setBackground(ContextCompat.getDrawable(MainActivity4.this, R.drawable.ic_1));
                        binding.ivProfile.setBackground(ContextCompat.getDrawable(MainActivity4.this, R.drawable.ic_1));
                        break;
                    case 1:
                        NutritionFacts teaEgg = new NutritionFacts(selectedString,5, 10, 8, 2, 1, 1, 20);
                        nutrientAdapter.updateList(teaEgg.toStringList());
                        binding.ivFood.setVisibility(View.GONE);
                        binding.includeToolbar.ibRight.setVisibility(View.GONE);
                        binding.includeToolbar.cvBlock.setBackground(ContextCompat.getDrawable(MainActivity4.this, R.drawable.ic_2));
                        binding.ivProfile.setBackground(ContextCompat.getDrawable(MainActivity4.this, R.drawable.ic_2));

                        break;
                    case 2:
                        NutritionFacts chickenBreast = new NutritionFacts(selectedString, 15, 30, 5, 3, 2, 1, 40);
                        nutrientAdapter.updateList(chickenBreast.toStringList());
                        binding.ivFood.setVisibility(View.GONE);
                        binding.includeToolbar.ibRight.setVisibility(View.GONE);
                        binding.includeToolbar.cvBlock.setBackground(ContextCompat.getDrawable(MainActivity4.this, R.drawable.ic_3));
                        binding.ivProfile.setBackground(ContextCompat.getDrawable(MainActivity4.this, R.drawable.ic_3));
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.tvSave.setOnClickListener(v -> {
            finish();
        });

        binding.tvNextRecord.setOnClickListener(v -> {
            MainActivity8.createIntent(this);
        });

    }

    public static void createIntent(Context context) {
        context.startActivity(new Intent(context, MainActivity4.class));
    }
}