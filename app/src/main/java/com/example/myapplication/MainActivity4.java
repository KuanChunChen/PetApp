package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.NutrientAdapter;
import com.example.myapplication.databinding.ActivityMain4Binding;
import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.dao.NutritionFactsDao;
import com.example.myapplication.model.NutritionFacts;

import java.util.Collections;
import java.util.List;

public class MainActivity4 extends AppCompatActivity {

    private ActivityMain4Binding binding;
    private NutrientAdapter nutrientAdapter;


    private AppDatabase appDatabase;
    private NutritionFactsDao nutritionFactsDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain4Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        appDatabase = PetApplication.getDatabase(this);
        nutritionFactsDao = appDatabase.nutritionFactsDao();
        initView();

    }

    Thread initExampleThread;
    Thread saveDataThread;
    Thread initTableThread;

    private String[] mapToNameList(List<NutritionFacts> nutritionFactsList) {
        String[] names = new String[nutritionFactsList.size()];
        for (int i = 0; i < nutritionFactsList.size(); i++) {
            names[i] = nutritionFactsList.get(i).name;
        }
        return names;
    }

    private void initTable() {

        initTableThread = new Thread(() -> {
            int count = nutritionFactsDao.getNutritionFactsCount();
            if (count == 0 ){
                NutritionFacts noExample = new NutritionFacts("尚無範本", 0, 0, 0, 0, 0, 0, 0);
                nutritionFactsDao.insert(noExample);
            }

            List<NutritionFacts> nutritionFactsList;
            if (count == 0 || count == 1) {
                nutritionFactsList = nutritionFactsDao.getAllNutritionFacts();
            } else {
                nutritionFactsList = nutritionFactsDao.getAllNutritionFactsExcept("尚無範本");
            }

            String[] nutritionFactsName = mapToNameList(nutritionFactsList);
            new Handler(Looper.getMainLooper()).post(() -> {
                ArrayAdapter<String> adapter;
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nutritionFactsName);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.includeExample.spinner.setAdapter(adapter);
            });

            nutrientAdapter = new NutrientAdapter(Collections.singletonList(""), binding.rvNutrients);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            binding.rvNutrients.setLayoutManager(layoutManager);
            binding.rvNutrients.setAdapter(nutrientAdapter);

            binding.includeExample.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedString = parent.getItemAtPosition(position).toString();
                    if (initExampleThread != null && initExampleThread.isAlive()) {
                        initExampleThread.interrupt();
                    }
                    initExampleThread = new Thread(() -> {
                        NutritionFacts nutritionFacts = nutritionFactsDao.getNutritionFactsByName(selectedString);
                        Log.d("Test", "test:nutritionFacts" + nutritionFacts);
                        Log.d("Test", "test:nutritionFacts" + nutritionFacts.id);
                        Log.d("Test", "test:nutritionFacts" + nutritionFacts.name);

                        new Handler(Looper.getMainLooper()).post(() -> {
                            binding.includeFreeInput.etInput.setText(nutritionFacts.name);
                            nutrientAdapter.updateList(nutritionFacts.toStringList());
                        });
                    });
                    initExampleThread.start();
//
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        });
        initTableThread.start();
    }
    private void initView() {
        binding.includeToolbar.tvTitle.setText("食物範本");
        binding.includeToolbar.cvBlock.setVisibility(View.VISIBLE);
        binding.includeToolbar.ibRight.setVisibility(View.GONE);
        binding.includeExample.tvTitle.setText("現有範本");
        binding.includeFreeInput.tvTitle.setText("自由新增:");
        binding.tvRvTitle.setText("營養素");

        binding.ivQuestion.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "這邊有輸入名字的話會幫你新建一筆範本", Toast.LENGTH_SHORT).show();
        });

        initTable();

        binding.tvSave.setOnClickListener(v -> {

            if ((binding.includeFreeInput.etInput.getText().toString().equals(""))) {
                Toast.makeText(getApplicationContext(), "需輸入自由新增名稱才能儲存", Toast.LENGTH_SHORT).show();
                return;
            }

            saveDataThread = new Thread(() -> {
                String inputName = binding.includeFreeInput.etInput.getText().toString().trim();
                NutritionFacts nutritionFacts = nutrientAdapter.getAllEditTextValues();
                nutritionFacts.name = inputName;

                boolean isExist = nutritionFactsDao.checkIfNameExist(inputName);
                Log.d("test", "tst: isExist " + isExist);
                if (isExist) {
                    nutritionFactsDao.updateNutritionFacts(nutritionFacts.name, nutritionFacts.carbohydrates, nutritionFacts.protein, nutritionFacts.fat, nutritionFacts.vitamins, nutritionFacts.minerals, nutritionFacts.dietaryFiber, nutritionFacts.water);
                } else {
                    nutritionFactsDao.insert(nutritionFacts);
                }
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(getApplicationContext(), "成功儲存", Toast.LENGTH_SHORT).show();

                    finish();
                });
            });
            saveDataThread.start();
        });

        binding.tvNextRecord.setOnClickListener(v -> {
            MainActivity8.createIntent(this);
        });

    }

    public static void createIntent(Context context) {
        context.startActivity(new Intent(context, MainActivity4.class));
    }
}