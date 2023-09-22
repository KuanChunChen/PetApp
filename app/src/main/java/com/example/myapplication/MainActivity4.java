package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.adapter.NutrientAdapter;
import com.example.myapplication.databinding.ActivityMain4Binding;
import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.dao.NutritionFactsDao;
import com.example.myapplication.model.NutritionFacts;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

public class MainActivity4 extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 125;

    private ActivityMain4Binding binding;
    private NutrientAdapter nutrientAdapter;

    private ImageView currentImageView;

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
            if (count == 0) {
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
                        if (nutritionFacts != null) {
                            Log.d("Test", "test:nutritionFacts" + nutritionFacts);
                            Log.d("Test", "test:nutritionFacts" + nutritionFacts.id);
                            Log.d("Test", "test:nutritionFacts" + nutritionFacts.name);

                            new Handler(Looper.getMainLooper()).post(() -> {
                                binding.includeFreeInput.etInput.setText(nutritionFacts.name);
                                nutrientAdapter.updateList(nutritionFacts.toStringList());
                                byte[] imageBytes = nutritionFacts.imageBytes;
                                if (imageBytes != null && imageBytes.length != 0) {
                                    setImageByteCodeToImage(binding.ivFood, imageBytes);
                                    setImageByteCodeToImage(binding.includeToolbar.ibRight, imageBytes);
                                }
                            });
                        } else {
                            Log.d("Test", "test:nutritionFacts is null");
                            Toast.makeText(getApplicationContext(), "NutritionFacts is null", Toast.LENGTH_SHORT).show();
                        }

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
        binding.includeToolbar.ibRight.setVisibility(View.VISIBLE);
        binding.includeToolbar.ibRight.setOnClickListener(v -> {
            selectImage();
            currentImageView = binding.includeToolbar.ibRight;
        });
        binding.includeExample.tvTitle.setText("現有範本");
        binding.includeFreeInput.tvTitle.setText("自由新增:");
        binding.tvRvTitle.setText("營養素");
        binding.ivProfile.setOnClickListener(v -> {
            selectImage();
            currentImageView = binding.ivFood;
        });

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
                byte[] imageBytes = getImageByteCode(binding.ivFood);
                NutritionFacts nutritionFacts = nutrientAdapter.getAllEditTextValues();
                nutritionFacts.name = inputName;
                nutritionFacts.imageBytes = imageBytes;

                boolean isExist = nutritionFactsDao.checkIfNameExist(inputName);
                Log.d("test", "tst: isExist " + isExist);
                if (isExist) {
                    nutritionFactsDao.updateNutritionFacts(nutritionFacts.name, nutritionFacts.carbohydrates, nutritionFacts.protein, nutritionFacts.fat, nutritionFacts.vitamins, nutritionFacts.minerals, nutritionFacts.dietaryFiber, nutritionFacts.water, imageBytes);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data != null ? data.getData() : null;
            if (selectedImageUri != null) {
                int cardViewWidth = 0;
                int cardViewHeight = 0;
                if (currentImageView == binding.includeToolbar.ibRight) {
                    cardViewWidth = binding.includeToolbar.cvBlock.getWidth();
                    cardViewHeight = binding.includeToolbar.cvBlock.getHeight();
                } else if (currentImageView == binding.ivFood) {
                    cardViewWidth = binding.ivProfile.getWidth();
                    cardViewHeight = binding.ivProfile.getHeight();
                }
                Glide.with(this)
                        .load(selectedImageUri)
                        .override(cardViewWidth, cardViewHeight)
                        .apply(new RequestOptions().centerCrop())
                        .into(binding.includeToolbar.ibRight);

                Glide.with(this)
                        .load(selectedImageUri)
                        .override(cardViewWidth, cardViewHeight)
                        .apply(new RequestOptions().centerCrop())
                        .into(binding.ivFood);
            }
        }
    }

    private byte[] getImageByteCode(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable == null) {
            return new byte[0];
        }
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat || drawable instanceof VectorDrawable) {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } else {
            return new byte[0];
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        return imageBytes;
    }
    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void setImageByteCodeToImage(ImageView imageView, byte[] imageBytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        imageView.setImageBitmap(bitmap);
    }
    public static void createIntent(Context context) {
        context.startActivity(new Intent(context, MainActivity4.class));
    }
}