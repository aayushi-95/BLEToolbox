/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;
import com.freescale.bletoolbox.utility.BLEConverter;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RSCActivity extends BaseServiceActivity {

    @Bind(R.id.rsc_instantaneous_speed)
    TextView mRscInstantaneousSpeed;

    @Bind(R.id.rsc_instantaneous_cadence)
    TextView mRscInstantaneousCadence;

    @Bind(R.id.rsc_status)
    TextView mRunningStatus;

    @Bind(R.id.rsc_stride_length)
    TextView mRscStrideLength;

    @Bind(R.id.rsc_total_distance)
    TextView mRscTotalDistance;

    @Bind(R.id.rsc_sensor_location)
    TextView mRscSensorLocation;

    /**
     * Override super class method to clear all UI parts.
     *
     * @param e
     */
    @Override
    public void onEventMainThread(BLEStateEvent.Disconnected e) {
        super.onEventMainThread(e);
        mRscInstantaneousSpeed.setText("---");
        mRscInstantaneousCadence.setText("---");
        mRunningStatus.setText("---");
        mRscStrideLength.setText("---");
        mRscTotalDistance.setText("---");
        mRscSensorLocation.setText("---");
    }

    /**
     * Override super class so we can send request of HeartRate information here.
     *
     * @param e
     */
    @Override
    public void onEvent(BLEStateEvent.ServiceDiscovered e) {
        super.onEvent(e);
        BLEService.INSTANCE.request(BLEAttributes.RUNNING_SPEED, BLEAttributes.SENSOR_LOCATION, BLEService.Request.READ);
        BLEService.INSTANCE.request(BLEAttributes.RUNNING_SPEED, BLEAttributes.RSC_MEASUREMENT, BLEService.Request.NOTIFY);
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
        if (BLEAttributes.RSC_MEASUREMENT == assignedNumber) {
            int flags = e.characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            int instantaneousSpeed = e.characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 1);
            int instantaneousCadence = e.characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 3);

            float realSpeed = instantaneousSpeed / 256f * 3.6f; // convert from m/s of 256 resolution to Km/h
            String strSpeed = String.format(Locale.getDefault(), "%.1f", realSpeed);
            SpannableString spanStrSpeed = new SpannableString(strSpeed);
            spanStrSpeed.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.deep_red)),
                    0, strSpeed.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//            spanStrSpeed.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)),
//                    strSpeed.length() - "Km/h".length(), strSpeed.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mRscInstantaneousSpeed.setText(spanStrSpeed);

            SpannableString spanStrCandence = new SpannableString(String.valueOf(instantaneousCadence));
            spanStrCandence.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.deep_red)),
                    0, spanStrCandence.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//            spanStrCandence.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)),
//                    spanStrCandence.length() - "rpm".length(), spanStrCandence.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mRscInstantaneousCadence.setText(spanStrCandence);

            boolean hasStrideLength = (flags & 0x01) == 1;
            boolean hasTotalDistance = ((flags >> 1) & 0x01) == 1;
            boolean isRunning = ((flags >> 2) & 0x01) == 1;
            if (hasStrideLength) {
                int strideLength = e.characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 4);
                mRscStrideLength.setText(String.format(Locale.getDefault(), "%.2f m", strideLength / 100f));
                if (hasTotalDistance) {
                    int totalDistance = e.characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 6);
                    mRscTotalDistance.setText(String.format(Locale.getDefault(), "%.0f m", totalDistance / 10f));
                }
            } else {
                if (hasTotalDistance) {
                    int totalDistance = e.characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 4);
                    mRscTotalDistance.setText(String.format(Locale.getDefault(), "%.0f m", totalDistance / 10f));
                }
            }
            mRunningStatus.setText(isRunning ? "Run" : "Walk");
        } else if (BLEAttributes.SENSOR_LOCATION == assignedNumber) {
            int sensorLocationValue = e.characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            mRscSensorLocation.setText(BLEConverter.fromSensorLocation(sensorLocationValue));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_speed);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(R.string.app_running_speed);
    }
}
