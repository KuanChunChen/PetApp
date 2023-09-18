package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.NutrientAdapter;
import com.example.myapplication.adapter.NutrientNoInputAdapter;
import com.example.myapplication.databinding.ActivityMain5Binding;
import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.dao.NutritionFactsDao;
import com.example.myapplication.model.NutritionFacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity5 extends AppCompatActivity {


    private ActivityMain5Binding binding;
    private NutrientNoInputAdapter nutrientAdapter;

    private AppDatabase appDatabase;
    private NutritionFactsDao nutritionFactsDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain5Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        appDatabase = PetApplication.getDatabase(this);
        nutritionFactsDao = appDatabase.nutritionFactsDao();
        initView();
    }


    private String[] mapToNameList(List<NutritionFacts> nutritionFactsList) {
        String[] names = new String[nutritionFactsList.size()];
        for (int i = 0; i < nutritionFactsList.size(); i++) {
            names[i] = nutritionFactsList.get(i).name;
        }
        return names;
    }
    Thread initCountDataThread;
    private void initCountData() {
        initCountDataThread = new Thread(() -> {
            int count = nutritionFactsDao.getNutritionFactsCount();
            List<NutritionFacts> nutritionFactsList;

            if (count == 0 || count == 1) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(getApplicationContext(), "尚未新增食物範本，請先至食物範本新增！", Toast.LENGTH_SHORT).show();
                });
                return;
            }

            nutritionFactsList = nutritionFactsDao.getAllNutritionFactsExcept("尚無範本");
            String[] nutritionFactsName = mapToNameList(nutritionFactsList);
            new Handler(Looper.getMainLooper()).post(() -> {
                ArrayAdapter<String> adapter;
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nutritionFactsName);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.includeExample.spinner.setAdapter(adapter);
            });

            nutrientAdapter = new NutrientNoInputAdapter(Collections.singletonList(""));
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            binding.rvNutrients.setLayoutManager(layoutManager);
            binding.rvNutrients.setAdapter(nutrientAdapter);


            binding.includeExample.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedString = parent.getItemAtPosition(position).toString();
                    if (initCountDataThread != null && initCountDataThread.isAlive()) {
                        initCountDataThread.interrupt();
                    }
                    initCountDataThread = new Thread(() -> {
                        NutritionFacts nutritionFacts = nutritionFactsDao.getNutritionFactsByName(selectedString);
                        Log.d("Test", "test:nutritionFacts" + nutritionFacts);
                        Log.d("Test", "test:nutritionFacts" + nutritionFacts.id);
                        Log.d("Test", "test:nutritionFacts" + nutritionFacts.name);

                        new Handler(Looper.getMainLooper()).post(() -> {
                            nutrientAdapter.updateList(nutritionFacts.toStringList());
                            byte[] imageBytes = nutritionFacts.imageBytes;
                            if (imageBytes.length != 0) {
                                setImageByteCodeToImage(binding.ivFood, imageBytes);
                                setImageByteCodeToImage(binding.includeToolbar.ibRight, imageBytes);
                            }
                        });
                    });
                    initCountDataThread.start();
//
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        });
        initCountDataThread.start();
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

        initCountData();

        nutrientAdapter = new NutrientNoInputAdapter(Collections.singletonList(""));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvNutrients.setLayoutManager(layoutManager);
        binding.rvNutrients.setAdapter(nutrientAdapter);


        binding.tvSave.setText("再新增食物");
        binding.tvSave.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "已新增食物", Toast.LENGTH_SHORT).show();
        });

        binding.tvNextRecord.setText("結算");
        binding.tvNextRecord.setOnClickListener(v -> {
            MainActivity6.createIntent(this);
        });

    }

    private void setImageByteCodeToImage(ImageView imageView, byte[] imageBytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        imageView.setImageBitmap(bitmap);
    }
    public static void createIntent(Context context) {
        context.startActivity(new Intent(context, MainActivity5.class));
    }
}