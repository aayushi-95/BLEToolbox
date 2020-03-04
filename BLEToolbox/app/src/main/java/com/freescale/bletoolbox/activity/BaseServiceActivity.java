/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.freescale.bletoolbox.AppConfig;
import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;
import com.freescale.bletoolbox.utility.BLEConverter;
import com.freescale.bletoolbox.utility.SdkUtils;
import com.freescale.bletoolbox.view.DeviceInfoDialog;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;

public class BaseServiceActivity extends BaseActivity {

    /**
     * Requested application should pass MAC Address of BLE device here.
     */
    public static final String INTENT_KEY_ADDRESS = "intent.key.address";
    public static final String INTENT_KEY_NAME = "intent.key.name";
    public boolean isShowMenuOption;

    @Nullable
    @Bind(R.id.status_connection)
    TextView mStatusConnection;

    @Nullable
    @Bind(R.id.status_battery)
    TextView mStatusBattery;

    @Nullable
    @Bind(R.id.status_view_info)
    View mInfoButton;

    protected String mDeviceAddress;
    protected final Handler mHandler = new Handler();

    /**
     * Preodically check for battery value.
     */
    private final Runnable mBatteryRunner = new Runnable() {

        @Override
        public void run() {
            invokeBatteryCheck();
        }
    };

    public void onEventMainThread(BLEStateEvent.BluetoothStateChanged e) {
        if (e.newState == BluetoothAdapter.STATE_OFF) {
            if (BLEService.INSTANCE.getConnectionState() != BLEService.State.STATE_DISCONNECTED) {
                BLEService.INSTANCE.disconnect();
                new MaterialDialog.Builder(this).title(R.string.error_title)
                        .titleColor(Color.RED)
                        .content(R.string.error_lost_connection)
                        .positiveText(android.R.string.ok).show();
            }
        }
    }

    /**
     * Update connection state to Connected in main thread. Subclasses can override this method to update any other UI part.
     *
     * @param e
     */
    public void onEventMainThread(BLEStateEvent.Connected e) {
        if (mStatusConnection != null) {
            mStatusConnection.setText("Status: " + getString(R.string.state_connected));
        }
        supportInvalidateOptionsMenu();
    }

    /**
     * Update connection state to Connecting in main thread. Subclasses can override this method to update any other UI part.
     *
     * @param e
     */
    public void onEventMainThread(BLEStateEvent.Connecting e) {
        if (mStatusConnection != null) {
            mStatusConnection.setText("Status: " + getString(R.string.state_connecting));
        }
        supportInvalidateOptionsMenu();
    }

    /**
     * Update connection state to Disconnected in main thread. Subclasses can override this method to update any other UI part.
     * Subclasses MUST override this method if they want to clear current values when BLE device is disconnected.
     *
     * @param e
     */
    public void onEventMainThread(BLEStateEvent.Disconnected e) {
        if (mStatusConnection != null) {
            mStatusConnection.setText("Status: " + getString(R.string.state_disconnected));
        }
        if (mStatusBattery != null) {
            mStatusBattery.setVisibility(View.GONE);
        }
        if (mInfoButton != null) {
            mInfoButton.setVisibility(View.INVISIBLE);
        }
        supportInvalidateOptionsMenu();
        mHandler.removeCallbacks(mBatteryRunner);
    }

    /**
     * Whenever all services inside BLE device are discovered, this event will be fire.
     * Subclasses MUST override this method and can call any required requests to get data from here.
     * Note that this method is called in BLE thread. Invoking UI actions need to be done in main thread.
     *
     * @param e
     */
    public void onEvent(BLEStateEvent.ServiceDiscovered e) {
        invokeBatteryCheck();
        if (BLEService.INSTANCE.getService(BLEAttributes.DEVICE_INFORMATION_SERVICE) != null) {
            if (mInfoButton != null) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mInfoButton.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    /**
     * BLE device has sent us a data package. Parse it and extract assigned number to do action if needed.
     * Subclasses MUST override this method to handle corresponding data.
     *
     * @param e
     */
    @DebugLog
    public void onEventMainThread(BLEStateEvent.DataAvailable e) {
        updateBatteryInfo(e.characteristic);
    }

    /**
     * Subclasses SHOULD have a view with id of {@link R.id#status_view_info} so it can receive the click handler automatically to view device info.
     */
    @OnClick(R.id.status_view_info)
    public void viewDeviceInfo() {
        CharSequence title = getSupportActionBar() != null ? getSupportActionBar().getSubtitle() : getTitle();
        if (TextUtils.isEmpty(title)) {
            title = getString(R.string.app_name);
        }
        DeviceInfoDialog.newInstance(this, title.toString()).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(isShowMenuOption){
            if (menu.findItem(R.id.menu_connect) != null) {
                if (BLEService.INSTANCE.getConnectionState() == BLEService.State.STATE_DISCONNECTED) {
                    menu.findItem(R.id.menu_connect).setTitle(R.string.menu_connect);
                } else {
                    menu.findItem(R.id.menu_connect).setTitle(R.string.menu_disconnect);
                }
            }
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.menu_connect) {
            if (BLEService.INSTANCE.getConnectionState() == BLEService.State.STATE_DISCONNECTED) {
                toggleState(true);
            } else {
                toggleState(false);
                onEventMainThread(new BLEStateEvent.Disconnected());
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                toggleState(true);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isShowMenuOption = true;
        EventBus.getDefault().register(this);
        mDeviceAddress = getIntent().getStringExtra(INTENT_KEY_ADDRESS);
        if (TextUtils.isEmpty(mDeviceAddress)) {
            throw new NullPointerException("Invalid Bluetooth MAC Address");
        }
        // automatically perform connection request
        BLEService.INSTANCE.init(getApplicationContext());
        toggleState(true);
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
    }

    /**
     * Automatically clear and unregister event handler.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        BLEService.INSTANCE.disconnect();
    }

    /**
     * Connect, or disconnect from BLE device.
     *
     * @param connected
     */
    protected void toggleState(boolean connected) {
        if (connected) {
            if (BLEService.INSTANCE.isBluetoothAvailable()) {
                BLEService.INSTANCE.connect(mDeviceAddress, needUartSupport());
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            BLEService.INSTANCE.disconnect();
        }
    }

    /**
     * Update battery info with a piece of data. Will need to check for assigned number first.
     *
     * @param characteristic
     */
    protected void updateBatteryInfo(@NonNull BluetoothGattCharacteristic characteristic) {
        int assignedNumber = BLEConverter.getAssignedNumber(characteristic.getUuid());
        if (BLEAttributes.BATTERY_LEVEL == assignedNumber) {
            if (mStatusBattery != null) {
                int batteryLevel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                int batteryRes = batteryLevel <= 25 ? R.drawable.ic_empty_battery :
                        (batteryLevel >= 75 ? R.drawable.ic_full_battery : R.drawable.ic_half_battery);
                mStatusBattery.setVisibility(View.VISIBLE);
                mStatusBattery.setText(batteryLevel + "%");
                mStatusBattery.setCompoundDrawablesWithIntrinsicBounds(0, 0, batteryRes, 0);
            }
        }
    }

    /**
     * Preodically invoke battery check by {@link AppConfig#BATTERY_UPDATE_INTERVAL}.
     */
    protected void invokeBatteryCheck() {
        boolean batteryServiceFound = BLEService.INSTANCE.request(BLEAttributes.BATTERY_SERVICE,
                BLEAttributes.BATTERY_LEVEL, BLEService.Request.READ);
        if (mStatusBattery != null && !batteryServiceFound) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    mStatusBattery.setVisibility(View.GONE);
                }
            });
        }
        mHandler.removeCallbacks(mBatteryRunner);
        mHandler.postDelayed(mBatteryRunner, AppConfig.BATTERY_UPDATE_INTERVAL);
    }

    protected boolean needUartSupport() {
        return false;
    }
}
