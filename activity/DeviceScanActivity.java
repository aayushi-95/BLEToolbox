/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.freescale.bletoolbox.AppConfig;
import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.model.BLEApp;
import com.freescale.bletoolbox.model.BLEDevice;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DeviceScanActivity extends BaseScanActivity {

    private BLEDeviceAdapter mAdapter;
    private List<BLEDevice> mDevices = new ArrayList<>();

    private boolean pendingDeviceConnection;
    private Intent pendingIntent;
    private ProgressDialog pDialog;

    @Override
    protected boolean isBeaconScanning() {
        return false;
    }

    @Override
    protected void clearData() {
        mDevices.clear();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void appendDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        BLEDevice bleDevice = new BLEDevice(device);
        if (mDevices.contains(bleDevice)) {
            bleDevice = mDevices.get(mDevices.indexOf(bleDevice));
        } else {
             mDevices.add(bleDevice);
        }
        bleDevice.setRssi(rssi);
        bleDevice.setLastScannedTime(System.nanoTime());
        if (pendingIntent != null && pendingDeviceConnection) {
            String pendingAddress = pendingIntent.getStringExtra(BaseServiceActivity.INTENT_KEY_ADDRESS);
            if (bleDevice.getInternalDevice().getAddress().equals(pendingAddress)) {
                pendingDeviceConnection = false;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                        pDialog = null;
                        if (pendingIntent != null) {
                            startActivity(pendingIntent);
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void initList() {
        mAdapter = new BLEDeviceAdapter();
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    protected void refreshList() {
        final long now = System.nanoTime();
        for (int i = 0; i < mDevices.size(); ++i) {
            if ((now - mDevices.get(i).getLastScannedTime()) / 1000 / 1000 > AppConfig.BEACON_OUT_OF_RANGE_PERIOD) {
                BLEDevice device = mDevices.remove(i);
                if (pendingIntent != null && pendingDeviceConnection) {
                    String pendingAddress = pendingIntent.getStringExtra(BaseServiceActivity.INTENT_KEY_ADDRESS);
                    if (device.getInternalDevice().getAddress().equals(pendingAddress)) {
                        pendingDeviceConnection = false;
                        Toast.makeText(this, "This device is no longer available.", Toast.LENGTH_SHORT).show();
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                        pDialog = null;
                        pendingIntent = null;
                    }
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void toggleScanState(boolean isScanning) {
        super.toggleScanState(isScanning);
        if (!isScanning && pendingIntent != null && pendingDeviceConnection) {
            Toast.makeText(this, "Scanning is no longer active.", Toast.LENGTH_SHORT).show();
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            pDialog = null;
            pendingIntent = null;
            pendingDeviceConnection = false;
        }
    }

    private final View.OnClickListener mDeviceClickHandler = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mDevices == null) {
                return;
            }
            int itemPosition = mRecyclerView.getChildAdapterPosition(v);
            if (itemPosition < 0 || itemPosition >= mDevices.size()) {
                return;
            }
            if (!isScanning) {
                Toast.makeText(DeviceScanActivity.this, "Scanning is no longer active.", Toast.LENGTH_SHORT).show();
                return;
            }
            final int appType = getIntent().getIntExtra(INTENT_KEY_APP, -1);
            pendingIntent = new Intent();
            switch (appType) {
                case BLEApp.Type.HEART_RATE:
                    pendingIntent.setClass(DeviceScanActivity.this, HeartRateActivity.class);
                    break;
                case BLEApp.Type.CYCLING_SPEED:
                    pendingIntent.setClass(DeviceScanActivity.this, CSCActivity.class);
                    break;
                case BLEApp.Type.THERMOMETER:
                    pendingIntent.setClass(DeviceScanActivity.this, HealthThermometerActivity.class);
                    break;
                case BLEApp.Type.RUNNING_SPEED:
                    pendingIntent.setClass(DeviceScanActivity.this, RSCActivity.class);
                    break;
                case BLEApp.Type.BLOOD_PRESSURE:
                    pendingIntent.setClass(DeviceScanActivity.this, BloodPressureActivity.class);
                    break;
                case BLEApp.Type.IPconfig:
                    pendingIntent.setClass(DeviceScanActivity.this, IPconfig.class);
                    break;
                case BLEApp.Type.PROXIMITY:
                    pendingIntent.setClass(DeviceScanActivity.this, ProximityActivity.class);
                    break;
                case BLEApp.Type.GLUCOSE:
                    pendingIntent.setClass(DeviceScanActivity.this, GlucoseActivity.class);
                    break;
                case BLEApp.Type.WUART:
                    pendingIntent.setClass(DeviceScanActivity.this, WuartActivity.class);
                    break;
                case BLEApp.Type.OTAP:
                    pendingIntent.setClass(DeviceScanActivity.this, OtapActivity.class);
                    break;
                case BLEApp.Type.FRDM_DEMO:
                    pendingIntent.setClass(DeviceScanActivity.this, FRMDActivity.class);
                    break;
                case BLEApp.Type.SHELL:
                    pendingIntent.setClass(DeviceScanActivity.this, ThreadShellActivity.class);
                    break;
                case BLEApp.Type.QPP:
                    pendingIntent.setClass(DeviceScanActivity.this,QppActivity.class);
                    break;
                case BLEApp.Type.SENSOR:
                    pendingIntent.setClass(DeviceScanActivity.this,SenorActivity.class);
                    break;
                case BLEApp.Type.ZIGBEE:
                    pendingIntent.setClass(DeviceScanActivity.this,ZigbeeActivity.class);
                    break;
                default:
                    return;
            }
            BLEDevice bleDevice = mDevices.get(itemPosition);
            pendingDeviceConnection = true;
            pendingIntent.putExtra(BaseServiceActivity.INTENT_KEY_ADDRESS, bleDevice.getInternalDevice().getAddress());
            pendingIntent.putExtra(BaseServiceActivity.INTENT_KEY_NAME, bleDevice.getInternalDevice().getName());
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            pDialog = ProgressDialog.show(DeviceScanActivity.this, null, getString(R.string.state_connecting), true, true);
            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    pendingIntent = null;
                    pendingDeviceConnection = false;
                }
            });
        }
    };

    static class BLEDeviceHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.device_name)
        TextView name;

        @Bind(R.id.device_mac_address)
        TextView mac;

        @Bind(R.id.device_bond_state)
        TextView bond;

        @Bind(R.id.device_rssi)
        TextView rssi;

        public BLEDeviceHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class BLEDeviceAdapter extends RecyclerView.Adapter<BLEDeviceHolder> {

        @Override
        public int getItemCount() {
            return mDevices == null ? 0 : mDevices.size();
        }

        @Override
        public BLEDeviceHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.ble_device_item, viewGroup, false);
            view.setOnClickListener(mDeviceClickHandler);
            return new BLEDeviceHolder(view);
        }

        @Override
        public void onBindViewHolder(BLEDeviceHolder holder, int position) {
            BLEDevice bleDevice = mDevices.get(position);
            BluetoothDevice device = bleDevice.getInternalDevice();
            holder.name.setText(TextUtils.isEmpty(device.getName()) ? "Unknown" : device.getName());
            holder.mac.setText(device.getAddress());
            holder.bond.setText(device.getBondState() == BluetoothDevice.BOND_BONDED ? "Bonded" : "Unbonded");
            holder.rssi.setText(bleDevice.getRssi() + " dBm");
        }
    }
}
