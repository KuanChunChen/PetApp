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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.myapplication.databinding.ActivityBleBinding;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BTClient extends Activity {

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

    private ActivityBleBinding binding;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();

        //如果打开本地蓝牙设备不成功，提示信息，结束程序
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
    }

    private void initView() {
        binding.includeToolbar.tvTitle.setText("藍芽電子秤");
        binding.lianjie.setOnClickListener(new lianjie());

    }


    class lianjie implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (!_bluetooth.isEnabled()) {
                Toast.makeText(BTClient.this, " 打开蓝牙中...", Toast.LENGTH_LONG).show();
                return;
            }


            //如未连接设备则打开DeviceListActivity进行设备搜索
            //	Button btn = (Button) findViewById(R.id.Button03);
            if (_socket == null) {
                Intent serverIntent = new Intent(BTClient.this, DeviceListActivity.class); //跳转程序设置
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
            return;

        }


    }


    //接收活动结果，响应startActivityForResult()
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    _device = _bluetooth.getRemoteDevice(address);
                    try {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        _socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                    } catch (IOException e) {
                        Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
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
                        } catch (IOException ee) {
                            Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
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
                break;
            default:
                break;
        }
    }

    //接收数据线程
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
            binding.zhong.setText(z+".");
            binding.liang.setText(l+"Kg");
        }
    };




    //关闭程序掉用处理部分
    public void onDestroy(){
        super.onDestroy();
        if(_socket!=null)  //关闭连接socket
            try{
                _socket.close();
            }catch(IOException e){}
        //	_bluetooth.disable();  //关闭蓝牙服务
    }



    public static void createIntent(Context context) {
        context.startActivity(new Intent(context, BTClient.class));
    }
}


