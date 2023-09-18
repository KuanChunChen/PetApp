package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
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

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "此設備不支援藍牙", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private Handler handler = new Handler();
    private boolean mScanning;


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            handler.postDelayed(() -> {
                mScanning = false;
                if (ActivityCompat.checkSelfPermission(DeviceListActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "請開啟權限", Toast.LENGTH_SHORT).show();
                    return;
                }
                bluetoothAdapter.stopLeScan(leScanCallback);
                binding.includeFinding.getRoot().setVisibility(View.GONE);
                binding.tvFindDevice.setVisibility(View.VISIBLE);
            }, SCAN_PERIOD);

            mScanning = true;
            binding.tvFindDevice.setVisibility(View.GONE);
            binding.includeFinding.getRoot().setVisibility(View.VISIBLE);
            bluetoothAdapter.startLeScan(leScanCallback);
        } else {
            mScanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(() -> {
                        if (ActivityCompat.checkSelfPermission(DeviceListActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    });
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
