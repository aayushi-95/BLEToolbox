/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;
import com.freescale.bletoolbox.utility.BLEConverter;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HealthThermometerActivity extends BaseServiceActivity {

    @Bind(R.id.health_value)
    TextView mHealthValue;

    @Bind(R.id.health_sensor_location)
    TextView mHealthSensorLocation;

    /**
     * Override super class method to clear all UI parts.
     *
     * @param e
     */
    @Override
    public void onEventMainThread(BLEStateEvent.Disconnected e) {
        super.onEventMainThread(e);
        mHealthValue.setText("---");
        mHealthSensorLocation.setText("---");
    }

    /**
     * Override super class so we can send request of HeartRate information here.
     *
     * @param e
     */
    @Override
    public void onEvent(BLEStateEvent.ServiceDiscovered e) {
        super.onEvent(e);
        BLEService.INSTANCE.request(BLEAttributes.THERMOMETER, BLEAttributes.TEMPERATURE_MEASUREMENT, BLEService.Request.INDICATE);
        //BLEService.INSTANCE.request(BLEAttributes.THERMOMETER, BLEAttributes.TEMPERATURE_TYPE, BLEService.Request.READ);
        BLEService.INSTANCE.request(BLEAttributes.THERMOMETER, BLEAttributes.INTERMEDIATE_TEMPERATURE, BLEService.Request.NOTIFY);
    }

    /**
     * Override super class to get data from received packet.
     *
     * @param e
     */
    @Override
    public void onEventMainThread(BLEStateEvent.DataAvailable e) {
        super.onEventMainThread(e);
        int assignedNumber = BLEConverter.getAssignedNumber(e.characteristic.getUuid());
        if (assignedNumber == BLEAttributes.TEMPERATURE_MEASUREMENT || assignedNumber == BLEAttributes.INTERMEDIATE_TEMPERATURE) {
            int flags = e.characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            float value = e.characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 1);
            boolean isFahrenheit = (flags & 0x01) == 1;
            boolean hasTimeStamp = ((flags >> 1) & 0x01) == 1;
            boolean hasTemperatureType = ((flags >> 2) & 0x01) == 1;

            mHealthValue.setText(String.format(Locale.getDefault(), "%.1f", value) + (isFahrenheit ? " °F" : " °C"));
            if (hasTemperatureType) {
                int type = e.characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, hasTimeStamp ? 12 : 5);
                mHealthSensorLocation.setText(BLEConverter.fromTemperatureType(type));
            }
        } else if (assignedNumber == BLEAttributes.TEMPERATURE_TYPE) {
            int type = e.characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            mHealthSensorLocation.setText(BLEConverter.fromTemperatureType(type));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_thermometer);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(R.string.app_thermometer);
    }
}
