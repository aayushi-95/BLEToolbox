/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.MyMigration;
import com.freescale.bletoolbox.utility.Constants;
import com.freescale.bletoolbox.utility.SharedPreferencesUtil;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class BLEToolbox extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // init Realm for storing Beacons's action and message
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .schemaVersion(1) // Must be bumped when the schema changes
                .migration(new MyMigration()) // Migration to run instead of throwing an exception
                .deleteRealmIfMigrationNeeded()//to do remove for production
                .build();
        Realm.setDefaultConfiguration(config);
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(this);
        if (sharedPreferencesUtil.getBoolean(Constants.THE_FIRST_RUN, true) == true) {
            sharedPreferencesUtil.saveBoolean(Constants.THE_FIRST_RUN, true);
        }
        if (sharedPreferencesUtil.getBoolean(Constants.ZB_COORDINATER_FIRST_RUN, true) == true) {
            sharedPreferencesUtil.saveBoolean(Constants.ZB_COORDINATER_FIRST_RUN, true);
        }
        if (sharedPreferencesUtil.getBoolean(Constants.ZB_ROUTER_FIRST_RUN, true) == true) {
            sharedPreferencesUtil.saveBoolean(Constants.ZB_ROUTER_FIRST_RUN, true);
        }
        if (sharedPreferencesUtil.getBoolean(Constants.ZB_ENDDEVICE_FIRST_RUN, true) == true) {
            sharedPreferencesUtil.saveBoolean(Constants.ZB_ENDDEVICE_FIRST_RUN, true);
        }
        // handle Bluetooth state change event at application-level, transform to internal event
        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    EventBus.getDefault().post(new BLEStateEvent.BluetoothStateChanged(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)));
                }
            }
        }, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        // handle Bluetooth bonding event at application-level, transform to internal event
        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(final Context context, final Intent intent) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                if (device != null && bondState != -1) {
                    EventBus.getDefault().post(new BLEStateEvent.DeviceBondStateChanged(device, bondState));
                }
            }
        }, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
    }
}
