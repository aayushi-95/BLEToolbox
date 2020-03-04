/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.freescale.bletoolbox.AppConfig;
import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.utility.BLEConverter;
import com.freescale.bletoolbox.utility.SdkUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;

public abstract class BaseScanActivity extends BaseActivity {

    /**
     * Requested application should pass title for this activity.
     */
    public static final String INTENT_KEY_TITLE = "intent.key.title";

    /**
     * Requested application should pass service UUID for this activity to scan.
     */
    public static final String INTENT_KEY_SERVICE = "intent.key.service";

    /**
     * This should be one of {@link com.freescale.bletoolbox.model.BLEApp.Type}.
     */
    public static final String INTENT_KEY_APP = "intent.key.app";

    @Bind(R.id.device_list)
    RecyclerView mRecyclerView;

    private final Handler mHandler = new Handler();
    private final Runnable mListRefresher = new Runnable() {

        @Override
        public void run() {
            refreshList();
            mHandler.postDelayed(this, 1000);
        }
    };

    private final Runnable mContinuouslyScan = new Runnable() {

        @Override
        public void run() {
            internalToggleScanState(false);
            internalToggleScanState(true);
            mHandler.postDelayed(mContinuouslyScan, AppConfig.DEVICE_CONTINUOUSLY_SCAN);
        }
    };

    private final Runnable mScanStopScheduler = new Runnable() {

        @Override
        public void run() {
            toggleScanState(false);
        }
    };

    // legacy BLE stack
    protected BluetoothAdapter mBluetoothAdapter;
    protected final BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (device != null && isRequestedUuidExist(scanRecord)) {
                        appendDevice(device, rssi, scanRecord);
                    }
                }
            };

    // those callbacks will be used in Lollipop and above
    protected BluetoothLeScanner mScanner;
    protected ScanCallback mCallback;

    protected boolean isScanning;
    protected boolean requestingBluetoothEnable;

    protected UUID targetUuid;

    public void onEventMainThread(BLEStateEvent.BluetoothStateChanged e) {
        if (e.newState == BluetoothAdapter.STATE_OFF) {
            toggleScanState(false);
        } else if (e.newState == BluetoothAdapter.STATE_ON) {
            toggleScanState(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_scan);
        if (item != null) {
            item.setTitle(isScanning ? R.string.menu_scan_stop : R.string.menu_scan_start);
        }
        View loading = findViewById(R.id.toolbar_loading);
        if (loading != null) {
            loading.setVisibility(isScanning ? View.VISIBLE : View.GONE);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.menu_scan) {
            toggleScanState(!isScanning);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                toggleScanState(true);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(getIntent().getIntExtra(INTENT_KEY_TITLE, R.string.app_name));

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new CustomHeightItemDecoration(this, 2));
        initList();

        int targetService = getIntent().getIntExtra(INTENT_KEY_SERVICE, 0);
        if (targetService != 0) {
            targetUuid = BLEConverter.uuidFromAssignedNumber(targetService);
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (SdkUtils.hasLollipop()) {
            mCallback = new ScanCallback() {

                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    if (result != null && result.getDevice() != null && result.getScanRecord() != null) {
                        if (isRequestedUuidExist(result.getScanRecord().getBytes())) {
                            appendDevice(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
                        }
                    }
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    if (results != null) {
                        for (ScanResult result : results) {
                            if (result != null && result.getDevice() != null && result.getScanRecord() != null) {
                                if (isRequestedUuidExist(result.getScanRecord().getBytes())) {
                                    appendDevice(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
                                }
                            }
                        }
                    }
                }

                @Override
                public void onScanFailed(int errorCode) {
                }
            };
        }

        EventBus.getDefault().register(this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        if (SdkUtils.hasMarshmallow()) {
            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(this, R.string.grant_permission, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }
        //
        if (requestingBluetoothEnable) {
            requestingBluetoothEnable = false;
        } else {
            toggleScanState(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        toggleScanState(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    protected abstract boolean isBeaconScanning();

    protected abstract void clearData();

    protected abstract void appendDevice(BluetoothDevice device, int rssi, byte[] scanRecord);

    protected abstract void initList();

    protected abstract void refreshList();

    protected void toggleScanState(boolean isScanning) {
        if (isScanning && !mBluetoothAdapter.isEnabled()) {
            requestingBluetoothEnable = true;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }
        this.isScanning = isScanning;
        supportInvalidateOptionsMenu();
        if (isScanning) {
            mHandler.post(mListRefresher);
            if (isBeaconScanning()) {
                clearData();
            } else {
                clearData();
                mHandler.postDelayed(mScanStopScheduler, AppConfig.DEVICE_SCAN_PERIOD);
            }

            internalToggleScanState(true);
            mHandler.postDelayed(mContinuouslyScan, AppConfig.DEVICE_CONTINUOUSLY_SCAN);
        } else {
            mHandler.removeCallbacks(mScanStopScheduler);
            mHandler.removeCallbacks(mListRefresher);
            mHandler.removeCallbacks(mContinuouslyScan);
            internalToggleScanState(false);
        }
    }

    protected boolean isRequestedUuidExist(byte[] scanRecord) {
        if (isBeaconScanning()) {
            // beacon scanning has no UUIDs
            return true;
        }
        return BLEConverter.uuidsFromScanRecord(scanRecord).contains(targetUuid);
    }

    @DebugLog
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void internalToggleScanState(boolean isScanning) {
        if (isScanning) {
            if (SdkUtils.hasLollipop()) {
                if (mScanner == null) {
                    mScanner = mBluetoothAdapter.getBluetoothLeScanner();
                }
                // Scanner instance can be null here, if BLE has been disabled right after we perform check in toggleScanState()
                // this can be happened on devices which Bluetooth turning on/off takes a few seconds
                if (mScanner == null) {
                    toggleScanState(false);
                    return;
                }
                mScanner.startScan(Collections.<ScanFilter>emptyList(),
                        new ScanSettings.Builder()
                                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                                .build(), mCallback);
            } else {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        } else {
            if (SdkUtils.hasLollipop()) {
                if (mScanner != null && mBluetoothAdapter.isEnabled()) {
                    mScanner.stopScan(mCallback);
                }
                mScanner = null;
            } else {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }

    static class CustomHeightItemDecoration extends RecyclerView.ItemDecoration {

        private final int mHeight;
        private final Drawable mDivider;

        public CustomHeightItemDecoration(@NonNull Context context, int mVerticalSpaceHeight) {
            this.mHeight = mVerticalSpaceHeight;
            this.mDivider = ContextCompat.getDrawable(context, R.drawable.list_divider);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                outRect.bottom = mHeight;
            }
        }

        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
