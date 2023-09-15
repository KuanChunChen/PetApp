package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
    }
    public void View4(View v)
    {
        Intent it = new Intent(this, MainActivity4.class);
        startActivity(it);
    }
    public void View5(View v)
    {
        Intent it = new Intent(this, MainActivity5.class);
        startActivity(it);
    }
    public void View6(View v)
    {
        Intent it = new Intent(this, MainActivity6.class);
        startActivity(it);
    }
}