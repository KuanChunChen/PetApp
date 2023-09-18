package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.databinding.ActivityMain2Binding;
import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.db.dao.PetDao;
import com.example.myapplication.db.entity.Pet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity2 extends AppCompatActivity {


    /*****/

    private final static int REQUEST_CONNECT_DEVICE = 1;    //宏定义查询设备句柄

    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP服务UUID号

    private InputStream is;    //输入流，用来接收蓝牙数据
    BluetoothDevice _device = null;     //蓝牙设备
    BluetoothSocket _socket = null;      //蓝牙通信socket
    boolean _discoveryFinished = false;
    boolean bRun = true;
    boolean bThread = false;
    int t, h;
    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();    //获取本地蓝牙适配器，即蓝牙设备




    /*****/


    private static final int REQUEST_IMAGE_PICK = 123;
    private ActivityMain2Binding binding;

    private AppDatabase appDatabase;
    private PetDao petDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        appDatabase = PetApplication.getDatabase(this);
        petDao = appDatabase.petDao();

        Intent intent = getIntent();
        String value = intent.getStringExtra("startFrom");
        if (value.equals("Main")) {

        } else if (value.equals("BTClient")) {

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

    public void initView() {
        initPet();
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
        });
        binding.includeProfile.ivQuestion.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "這個欄位通常用於描述一個動物的身體結構或體型。", Toast.LENGTH_SHORT).show();
        });
        binding.includeProfile.ivProfile.setOnClickListener(v -> {
            selectImage();
        });
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    Thread insertPetThread;

    private void getUserInputData() {

        String name = binding.includeProfile.includeName.etInput.getText().toString();
        String species = binding.includeProfile.includeSpecies.spinner.getSelectedItem().toString();
        String gender =  binding.includeProfile.includeGender.spinner.getSelectedItem().toString();
        String birth = binding.includeProfile.includeBirth.etInput.getText().toString();
        String variety = binding.includeProfile.includeVariety.spinner.getSelectedItem().toString();
        String bodyShape = binding.includeProfile.includeBodyShape.spinner.getSelectedItem().toString();
        String weight = binding.includeProfile.includeWeight.etInput.getText().toString();

        Pet pet = new Pet();
        pet.name = name;
        pet.imageBytes = getImageByteCode(binding.includeProfile.ivCenter);
        pet.species = species;
        pet.gender = gender;
        pet.birth = birth;
        pet.variety = variety;
        pet.bodyShape = bodyShape;
        pet.weight = weight;

        insertPetThread = new Thread(() -> {
            boolean isExist = petDao.checkIfNameExist(name);
            long id;
            if (isExist) {
                petDao.updateByName(pet.name, pet.species, pet.gender, pet.birth, pet.variety, pet.bodyShape, pet.weight,pet.imageBytes);
                id = petDao.getPetIdByName(pet.name);
            } else {
                id = petDao.insert(pet);
            }
            new Handler(Looper.getMainLooper()).post(() -> MainActivity3.createIntent(this, id));
        });
        insertPetThread.start();
    }

    Thread initPetThread;
    private void initPet() {
        initPetThread = new Thread(() -> {

            final Pet latestPet = petDao.getLatestPet();

            new Handler(Looper.getMainLooper()).post(() -> {

                if (latestPet != null) {
                    binding.includeProfile.includeName.etInput.setText(latestPet.name);
                    binding.includeProfile.includeBirth.etInput.setText(latestPet.birth);
                    binding.includeProfile.includeWeight.etInput.setText(latestPet.weight);
                    binding.includeProfile.includeSpecies.spinner.setSelection(findSpinnerIndex(R.array.species, latestPet.species));
                    binding.includeProfile.includeGender.spinner.setSelection(findSpinnerIndex(R.array.gender, latestPet.gender));
                    binding.includeProfile.includeVariety.spinner.setSelection(findSpinnerIndex(R.array.variety, latestPet.variety));
                    binding.includeProfile.includeBodyShape.spinner.setSelection(findSpinnerIndex(R.array.bodyShape, latestPet.bodyShape));

                    byte[] imageBytes = latestPet.imageBytes;
                    if (imageBytes.length != 0) {
                        setImageByteCodeToImage(binding.includeProfile.ivCenter, imageBytes);
                    }

                }
            });
        });
        initPetThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (initPetThread != null && initPetThread.isAlive()) {
            initPetThread.interrupt();
        }
        if (insertPetThread != null && insertPetThread.isAlive()) {
            insertPetThread.interrupt();
        }
    }

    private int findSpinnerIndex(int arraySource, String defaultOption) {
        String[] options = getResources().getStringArray(arraySource);
        int defaultIndex = 0;
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(defaultOption)) {
                defaultIndex = i;
                break;
            }
        }
        return defaultIndex;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Test", "\"requestCode:\"+requestCode" + "requestCode:" + requestCode + "\"resultCode:" + requestCode);

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
        } else if (requestCode == REQUEST_CONNECT_DEVICE) {
            if (data == null || data.getExtras() == null) {
                Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            String address = data.getExtras()
                    .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
            Log.d("Test", "\"address:" + address);

            _device = _bluetooth.getRemoteDevice(address);
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                _socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
            } catch (IOException e) {
                Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
                finish();
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
            binding.includeProfile.includeWeight.etInput.setText(z + "." + l);
        }
    };



    private byte[] getImageByteCode(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable == null) {
            return new byte[0];
        }
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        return imageBytes;
    }

    private void setImageByteCodeToImage(ImageView imageView, byte[] imageBytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        imageView.setImageBitmap(bitmap);
    }

    public static void createIntent(Context context,String value) {
        Intent intent = new Intent(context, MainActivity2.class);
        intent.putExtra("startFrom", value);
        context.startActivity(intent);
    }

    public void triggerBle() {

        if (!_bluetooth.isEnabled()) {
            Toast.makeText(MainActivity2.this, " 打开蓝牙中...", Toast.LENGTH_LONG).show();
            return;
        }


        //如未连接设备则打开DeviceListActivity进行设备搜索
        //	Button btn = (Button) findViewById(R.id.Button03);
        if (_socket == null) {
            Intent serverIntent = new Intent(MainActivity2.this, DeviceListActivity.class); //跳转程序设置
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //设置返回宏定义
        } else {
            //关闭连接socket
            try {

                is.close();
                _socket.close();
                _socket = null;
                bRun = false;
                //	btn.setText("连接");
            } catch (IOException e) {
            }
        }


    }

}