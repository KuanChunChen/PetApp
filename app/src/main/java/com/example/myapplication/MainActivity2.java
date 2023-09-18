package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.databinding.ActivityMain2Binding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 123;
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
        ArrayAdapter<CharSequence> adapterSpecies = ArrayAdapter.createFromResource(this, R.array.species, android.R.layout.simple_dropdown_item_1line);
        adapterSpecies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.includeProfile.includeSpecies.spinner.setAdapter(adapterSpecies);

        binding.includeProfile.includeGender.tvTitle.setText("性別");
        ArrayAdapter<CharSequence> adapterGender = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_dropdown_item_1line);
        adapterSpecies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.includeProfile.includeGender.spinner.setAdapter(adapterGender);

        binding.includeProfile.includeBirth.tvTitle.setText("生日");
        binding.includeProfile.includeVariety.tvTitle.setText("品種");

        ArrayAdapter<CharSequence> adapterVariety = ArrayAdapter.createFromResource(this, R.array.variety, android.R.layout.simple_dropdown_item_1line);
        adapterSpecies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.includeProfile.includeVariety.spinner.setAdapter(adapterVariety);

        binding.includeProfile.includeBodyShape.tvTitle.setText("體型");
        ArrayAdapter<CharSequence> adapterBodyShape = ArrayAdapter.createFromResource(this, R.array.bodyShape, android.R.layout.simple_dropdown_item_1line);
        adapterSpecies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.includeProfile.includeBodyShape.spinner.setAdapter(adapterBodyShape);
        
        binding.includeProfile.includeWeight.tvTitle.setText("重量");
        binding.includeProfile.tvConfirm.setOnClickListener(v -> {
            getUserInputData();
            MainActivity3.createIntent(this);
        });
        binding.includeProfile.ivQuestion.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "這個欄位通常用於描述一個人的身體結構或體型。", Toast.LENGTH_SHORT).show();
        });
        binding.includeProfile.ivProfile.setOnClickListener(v -> {
            selectImage();
        });
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void getUserInputData() {

        String name = binding.includeProfile.includeName.etInput.getText().toString();
        Drawable petDrawable = binding.includeProfile.ivCenter.getDrawable();
        String species = binding.includeProfile.includeSpecies.spinner.getSelectedItem().toString();
        String gender =  binding.includeProfile.includeGender.spinner.getSelectedItem().toString();
        String birth = binding.includeProfile.includeBirth.etInput.getText().toString();
        String variety = binding.includeProfile.includeVariety.spinner.getSelectedItem().toString();
        String bodyShape = binding.includeProfile.includeBodyShape.spinner.getSelectedItem().toString();
        String weight = binding.includeProfile.includeWeight.etInput.getText().toString();

        Log.d("User Input", "Name: " + name);
        Log.d("User Input", "Species: " + species);
        Log.d("User Input", "Gender: " + gender);
        Log.d("User Input", "Birth: " + birth);
        Log.d("User Input", "Variety: " + variety);
        Log.d("User Input", "Body Shape: " + bodyShape);
        Log.d("User Input", "Weight: " + weight);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data != null ? data.getData() : null;
            if (selectedImageUri != null) {
                int cardViewWidth = binding.includeProfile.ivProfile.getWidth();
                int cardViewHeight = binding.includeProfile.ivProfile.getHeight();

                Glide.with(this)
                        .load(selectedImageUri)
                        .override(cardViewWidth, cardViewHeight)
                        .apply(new RequestOptions().centerCrop())
                        .into(binding.includeProfile.ivCenter);
            }
        }
    }


    public static void createIntent(Context context) {
        context.startActivity(new Intent(context, MainActivity2.class));
    }
}