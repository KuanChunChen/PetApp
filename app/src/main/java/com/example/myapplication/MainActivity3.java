package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.databinding.ActivityMain2Binding;
import com.example.myapplication.databinding.ActivityMain3Binding;

public class MainActivity3 extends AppCompatActivity {


    private static final int REQUEST_IMAGE_PICK = 124;

    private ActivityMain3Binding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private ImageView currentImageView;

    public void initView() {
        binding.includeToolbar.cvBlock.setVisibility(View.VISIBLE);
        binding.includeToolbar.ibRight.setBackground(ContextCompat.getDrawable(this, R.drawable.plus_circle));
        binding.includeToolbar.ibRight.setOnClickListener(v -> {
            currentImageView = binding.includeToolbar.ibRight;
            selectImage();
        });

        binding.includeAdd.imageView.setOnClickListener(v -> {
            currentImageView = binding.includeAdd.imageView;
            selectImage();
        });
        binding.tvFoodExample.setOnClickListener(v -> {
            MainActivity4.createIntent(this);
        });
        binding.tvCount.setOnClickListener(v -> {
            MainActivity5.createIntent(this);
        });
        binding.includeRecord.tvRecord.setOnClickListener(v -> {
            MainActivity5.createIntent(this);
        });
        binding.includeRecord.tvAnalyze.setOnClickListener(v -> {
            MainActivity8.createIntent(this);
        });
        binding.includeRecord.tvBySelf.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data != null ? data.getData() : null;
            if (selectedImageUri != null) {
                if (currentImageView == binding.includeToolbar.ibRight) {
                    int cardViewWidth = binding.includeToolbar.cvBlock.getWidth();
                    int cardViewHeight = binding.includeToolbar.cvBlock.getHeight();

                    Glide.with(this)
                            .load(selectedImageUri)
                            .override(cardViewWidth, cardViewHeight)
                            .apply(new RequestOptions().centerCrop())
                            .into(binding.includeToolbar.ibRight);
                } else if (currentImageView == binding.includeAdd.imageView) {
                    int cardViewWidth = binding.includeAdd.getRoot().getWidth();
                    int cardViewHeight = binding.includeAdd.getRoot().getHeight();

                    Glide.with(this)
                            .load(selectedImageUri)
                            .override(cardViewWidth, cardViewHeight)
                            .apply(new RequestOptions().centerCrop())
                            .into(binding.includeAdd.imageView);
                }

            }
        }
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }
    public static void createIntent(Context context) {
        context.startActivity(new Intent(context, MainActivity3.class));
    }
}