/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.freescale.bletoolbox.AppConfig;
import com.freescale.bletoolbox.BuildConfig;
import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;
import com.freescale.bletoolbox.utility.BLEConverter;
import com.freescale.bletoolbox.view.WheelSizePickerDialog;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CSCActivity extends BaseServiceActivity {

    @Bind(R.id.csc_speed)
    TextView mCscSpeed;

    @Bind(R.id.csc_speed_unit)
    TextView mCscSpeedUnit;

    @Bind(R.id.csc_cadence)
    TextView mCscCadence;

    @Bind(R.id.csc_cadence_unit)
    TextView mCscCadenceUnit;

    @Bind(R.id.csc_sensor_location)
    TextView mCscSensorLocation;

    @Bind(R.id.csc_wheel_size)
    TextView mCscWheelSize;

    @Bind(R.id.csc_wheel_size_picker)
    View mCscWheelPicker;

    @Bind(R.id.csc_wheel_unit)
    View mCscWheelUnit;

    @Bind(R.id.csc_log)
    TextView log;

    private int lastWheelRevolution = -1;
    private int lastWheelEventTime = -1;
    private int lastCrankRevolution = -1;
    private int lastCrankEventTime = -1;

    private float mCurrentSpeed;
    private String mCurrentWheelSize = AppConfig.DEFAULT_WHEEL_CIRCUMFERENCE;
    private WheelSizePickerDialog mWheelSizePickerDialog;

    /**
     * Override super class method to clear all UI parts.
     *
     * @param e
     */
    @Override
    public void onEventMainThread(BLEStateEvent.Disconnected e) {
        super.onEventMainThread(e);
        mCscSpeed.setText("---");
//        mCscSpeedUnit.setVisibility(View.INVISIBLE);
        mCscCadence.setText("---");
//        mCscCadenceUnit.setVisibility(View.INVISIBLE);
        mCscSensorLocation.setText("---");
        mCscWheelSize.setText("---");
        mCscWheelPicker.setVisibility(View.INVISIBLE);
//        mCscWheelUnit.setVisibility(View.INVISIBLE);
        if (mWheelSizePickerDialog != null) {
            mWheelSizePickerDialog.dismiss();
            mWheelSizePickerDialog = null;
        }
    }

    /**
     * Override super class so we can send request of HeartRate information here.
     *
     * @param e
     */
    @Override
    public void onEvent(BLEStateEvent.ServiceDiscovered e) {
        super.onEvent(e);
        BLEService.INSTANCE.request(BLEAttributes.CYCLING_SPEED, BLEAttributes.SENSOR_LOCATION, BLEService.Request.READ);
        BLEService.INSTANCE.request(BLEAttributes.CYCLING_SPEED, BLEAttributes.CSC_MEASUREMENT, BLEService.Request.NOTIFY);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mCscWheelSize.setText(AppConfig.WHEEL_CIRCUMFERENCES.get(mCurrentWheelSize).toString());
                mCscWheelPicker.setVisibility(View.VISIBLE);
                mCscWheelUnit.setVisibility(View.VISIBLE);
            }
        });
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
        if (BLEAttributes.CSC_MEASUREMENT == assignedNumber) {
            int flags = e.characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            boolean hasWheelInfo = (flags & 0x01) == 1;
            boolean hasCrank = ((flags >> 1) & 0x01) == 1;
            if (hasWheelInfo) {
                int revolution = e.characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 1);
                int lastEventTime = e.characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 5);

                if (lastWheelEventTime < 0) {
                    lastWheelEventTime = lastEventTime;
                    lastWheelRevolution = revolution;
                } else {
                    Integer currentValue = AppConfig.WHEEL_CIRCUMFERENCES.get(mCurrentWheelSize);
                    float differRevolution = revolution - lastWheelRevolution;
                    float differTime = lastEventTime - lastWheelEventTime;
                    lastWheelRevolution = revolution;
                    lastWheelEventTime = lastEventTime;
                    if (differTime > 0) {
                        mCurrentSpeed = differRevolution / differTime * currentValue * 1024 * 3600 / 1000 / 1000f;
                        mCscSpeed.setText(String.format(Locale.getDefault(), "%.1f", mCurrentSpeed));
                        mCscSpeedUnit.setVisibility(View.VISIBLE);
                    }
                }

                if (hasCrank) {
                    revolution = e.characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 7);
                    lastEventTime = e.characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 9);

                    if (lastCrankEventTime < 0) {
                        lastCrankRevolution = revolution;
                        lastCrankEventTime = lastEventTime;
                    } else {
                        float differRevolution = revolution - lastCrankRevolution;
                        float differTime = lastEventTime - lastCrankEventTime;
                        lastCrankRevolution = revolution;
                        lastCrankEventTime = lastEventTime;
                        if (differTime > 0) {
                            float rpm = differRevolution / differTime * 1024f * 60;
                            mCscCadence.setText(String.format(Locale.getDefault(), "%.1f", rpm));
                            mCscCadenceUnit.setVisibility(View.VISIBLE);
                        }
                    }
                }
            } else {
                if (hasCrank) {
                    int revolution = e.characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 1);
                    int lastEventTime = e.characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 3);

                    if (lastCrankEventTime < 0) {
                        lastCrankRevolution = revolution;
                        lastCrankEventTime = lastEventTime;
                    } else {
                        float differRevolution = revolution - lastCrankRevolution;
                        float differTime = lastEventTime - lastCrankEventTime;
                        lastCrankRevolution = revolution;
                        lastCrankEventTime = lastEventTime;
                        if (differTime > 0) {
                            float rpm = differRevolution / differTime * 1024f * 60;
                            mCscCadence.setText(String.format(Locale.getDefault(), "%.1f", rpm));
                            mCscCadenceUnit.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
            log.setText(lastWheelRevolution + " " + lastWheelEventTime + " " + lastCrankRevolution + " " + lastCrankEventTime);
        } else if (BLEAttributes.SENSOR_LOCATION == assignedNumber) {
            int sensorLocationValue = e.characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            mCscSensorLocation.setText(BLEConverter.fromSensorLocation(sensorLocationValue));
        }
    }

    @OnClick(R.id.csc_wheel_size_picker)
    public void pickWheelSize() {
        mWheelSizePickerDialog = WheelSizePickerDialog.newInstance(this, getString(R.string.app_cycling_speed), mCurrentWheelSize, new WheelSizePickerDialog.OnSizePickedListener() {

            @Override
            public void onSizePicked(String size) {
                Integer currentValue = AppConfig.WHEEL_CIRCUMFERENCES.get(mCurrentWheelSize);
                Integer newValue = AppConfig.WHEEL_CIRCUMFERENCES.get(size);
                mCurrentWheelSize = size;
                mCurrentSpeed = mCurrentSpeed / currentValue * newValue;
                mCscSpeed.setText(String.format(Locale.getDefault(), "%.1f", mCurrentSpeed));
                mCscWheelSize.setText(AppConfig.WHEEL_CIRCUMFERENCES.get(mCurrentWheelSize).toString());
            }
        });
        mWheelSizePickerDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycling_speed);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(R.string.app_cycling_speed);
        if (!BuildConfig.DEBUG) {
            log.setVisibility(View.GONE);
        }
    }
}
