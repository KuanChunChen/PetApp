package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.databinding.DeviceListBinding;

public class DeviceListActivity extends Activity {

    private static final long SCAN_PERIOD = 10 * 1000;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private ArrayAdapter<String> pairedDevicesArrayAdapter;

    private DeviceListBinding binding;
    private BluetoothAdapter bluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DeviceListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initBLE();
        initView();
    }

    private void initBLE() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 1);
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "此設備不支援藍牙", Toast.LENGTH_SHORT).show();
            finish();
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private Handler handler = new Handler();
    private boolean mScanning;


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            handler.postDelayed(() -> {
                mScanning = false;
                bluetoothAdapter.cancelDiscovery();
                binding.includeFinding.getRoot().setVisibility(View.GONE);
                binding.tvFindDevice.setVisibility(View.VISIBLE);
            }, SCAN_PERIOD);

            mScanning = true;
            binding.tvFindDevice.setVisibility(View.GONE);
            binding.includeFinding.getRoot().setVisibility(View.VISIBLE);
            bluetoothAdapter.startDiscovery();
        } else {
            mScanning = false;
            bluetoothAdapter.cancelDiscovery();
        }
    }

    final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 從Intent中獲取藍芽設備對象
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 將設備的名稱和地址打印出來
                Log.d("Test", "test device name" + device.getName());
                Log.d("Test", "test device address" + device.getAddress());
                pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    };


    private void initView() {
        pairedDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);
        binding.includeToolbar.tvTitle.setText("查找設備");
        binding.pairedDevices.setAdapter(pairedDevicesArrayAdapter);
        binding.pairedDevices.setOnItemClickListener(deviceClickListener);
        binding.tvFindDevice.setOnClickListener(v -> {

            scanLeDevice(true);
        });
        binding.tvCancel.setOnClickListener(v -> {
            if (mScanning) {
                scanLeDevice(false);
            }
            finish();
        });
    }

    private AdapterView.OnItemClickListener deviceClickListener = (av, v, arg2, arg3) -> {

        scanLeDevice(false);

        String info = ((TextView) v).getText().toString();
        String address = info.substring(info.length() - 17);
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
        setResult(Activity.RESULT_OK, intent);
        finish();
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

}
