package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.NutrientAdapter;
import com.example.myapplication.adapter.NutrientNoInputAdapter;
import com.example.myapplication.databinding.ActivityMain5Binding;
import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.dao.NutritionFactsDao;
import com.example.myapplication.model.NutritionFacts;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MainActivity5 extends AppCompatActivity {


    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP服务UUID号

    private ActivityMain5Binding binding;
    private NutrientNoInputAdapter nutrientAdapter;

    private AppDatabase appDatabase;
    private NutritionFactsDao nutritionFactsDao;

    private String startFrom, bleAddress = "";
    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();    //获取本地蓝牙适配器，即蓝牙设备
    BluetoothSocket _socket = null;      //蓝牙通信socket
    BluetoothDevice _device = null;     //蓝牙设备

    private InputStream is;    //输入流，用来接收蓝牙数据
    boolean bRun = true;
    boolean bThread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain5Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        appDatabase = PetApplication.getDatabase(this);
        nutritionFactsDao = appDatabase.nutritionFactsDao();
        Intent intent = getIntent();
        String startFromValue = intent.getStringExtra("startFrom");
        String bleAddressValue = intent.getStringExtra("bleAddress");
        startFrom = startFromValue;
        bleAddress = bleAddressValue;

        if (startFrom.equals("BTClient")) {
            if (_bluetooth == null) {
                Toast.makeText(this, "无法打开手机蓝牙，请确认手机是否有蓝牙功能！", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            new Thread(() -> {
                if (!_bluetooth.isEnabled()) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    _bluetooth.enable();
                }
            }).start();
            triggerBle();
        }
        initView();
    }

    private void triggerBle() {
        if (bleAddress.equals("")) {
            Toast.makeText(this, "无法連接蓝牙", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        try {
            _device = _bluetooth.getRemoteDevice(bleAddress);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "無權限", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            _socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
        } catch (IOException e) {
            Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            _socket.connect();
            Toast.makeText(this, "连接" + _device.getName() + "成功！", Toast.LENGTH_SHORT).show();
            //	btn.setText("断开");
        } catch (IOException e) {
            try {
                Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
                _socket.close();
                _socket = null;
                finish();
            } catch (IOException ee) {
                Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
                finish();
            }

            return;
        }


        try {
            is = _socket.getInputStream();   //得到蓝牙数据输入流
        } catch (IOException e) {
            Toast.makeText(this, "接收数据失败！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!bThread) {
            ReadThread.start();
            bThread = true;
        } else {
            bRun = true;
        }
    }

    Thread ReadThread = new Thread() {
        public void run() {
            int num = 0;
            byte[] buffer = new byte[1024];
            bRun = true;
            //接收线程
            while (true) {
                try {
                    while (is.available() == 0) {
                        while (bRun == false) {
                        }
                    }
                    while (true) {

                        num = is.read(buffer);         //读入数据
                        Message message = new Message(); // 通知界面
                        message.what = 2;
                        message.obj = buffer;
                        mHandler.sendMessage(message);


                    }
                } catch (IOException e) {
                }
            }
        }
    };

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    byte buffer[] = (byte[])msg.obj;
                    refreshView(buffer); // 接收到数据后显示
                    break;
            }
        }

        @SuppressLint("SetTextI18n")
        private void refreshView(byte[] buffer)
        {
            int z =  (buffer[0]&0xff);
            int l =  (buffer[1]&0xff);
            binding.includeFreeInput.etInput.setText(z + "." + l);
        }
    };


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
                    finish();
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
    public static void createIntent(Context context, String startFrom, String bleAddress) {
        Intent intent = new Intent(context, MainActivity5.class);
        intent.putExtra("startFrom", startFrom);
        intent.putExtra("bleAddress", bleAddress);
        context.startActivity(intent);
    }
}