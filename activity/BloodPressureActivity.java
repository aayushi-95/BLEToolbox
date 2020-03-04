/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;
import com.freescale.bletoolbox.utility.BLEConverter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BloodPressureActivity extends BaseServiceActivity {

    @Bind(R.id.tv_systolic)
    TextView tvSystolic;
    @Bind(R.id.tv_unit_systolic)
    TextView tvUnitSystolic;

    @Bind(R.id.tv_diastolic)
    TextView tvDiastolic;
    @Bind(R.id.tv_unit_diastolic)
    TextView tvUnitDiastolic;

    @Bind(R.id.tv_map)
    TextView tvMAP;
    @Bind(R.id.tv_unit_map)
    TextView tvUnitMAP;

    @Bind(R.id.tv_pulse)
    TextView tvPulseRate;
    @Bind(R.id.tv_date)
    TextView tvDate;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_datetime_disconnected)
    TextView tvDateTimeDisconnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(R.string.app_blood_pressure);
        initLayout(false);
    }

    private void initLayout(boolean fromDisconnected) {
        tvSystolic.setText(R.string.dashx3);
        tvDiastolic.setText(R.string.dashx3);
        tvMAP.setText(R.string.dashx3);
        if (!fromDisconnected) {
            tvUnitSystolic.setText(R.string.dashx3);
            tvUnitDiastolic.setText(R.string.dashx3);
            tvUnitMAP.setText(R.string.dashx3);
        }
        tvPulseRate.setText(R.string.dashx3);
        tvDate.setText("0000 - 00 - 00");
        tvTime.setText("--:--:--");

        tvDateTimeDisconnected.setVisibility(View.VISIBLE);
        tvDate.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);
    }

    @Override
    public void onEvent(BLEStateEvent.ServiceDiscovered e) {
        super.onEvent(e);
        BLEService.INSTANCE.request(BLEAttributes.BLOOD_PRESSURE_SERVICE, BLEAttributes.BLOOD_PRESSURE_MEASUREMENT, BLEService.Request.INDICATE);
        BLEService.INSTANCE.request(BLEAttributes.BLOOD_PRESSURE_SERVICE, BLEAttributes.INTERMEDIATE_CUFF_PRESSURE, BLEService.Request.NOTIFY);
    }

    @Override
    public void onEventMainThread(BLEStateEvent.Disconnected e) {
        super.onEventMainThread(e);
        initLayout(true);
    }

    @Override
    public void onEventMainThread(BLEStateEvent.DataAvailable e) {
        super.onEventMainThread(e);
        if (e == null)
            return;

        BluetoothGattCharacteristic gattCharacteristic = e.characteristic;
        int assignedNumber = BLEConverter.getAssignedNumber(gattCharacteristic.getUuid());

        if (assignedNumber == BLEAttributes.INTERMEDIATE_CUFF_PRESSURE) {
            int offset = 0;
            final int flags = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset++);
            final int unit = flags & 0x01;

            final float systolicVal = gattCharacteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset);

            tvSystolic.setText(Float.toString(systolicVal));
            tvUnitSystolic.setText(unit == 0 ? R.string.bpm_mmHg : R.string.bpm_kpa);
        }
        else if (assignedNumber == BLEAttributes.BLOOD_PRESSURE_MEASUREMENT ) {
            int offset = 0;
            final int flags = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset++);
            final int unit = flags & 0x01;
            final boolean timestampPresent = (flags & 0x02) > 0;
            final boolean pulseRatePresent = (flags & 0x04) > 0;

            //
            final float systolicVal = gattCharacteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset);
            final float diastolicVal = gattCharacteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset + 2);
            final float meanArterialPressure = gattCharacteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset + 4);

            tvSystolic.setText(Float.toString(systolicVal));
            tvDiastolic.setText(Float.toString(diastolicVal));
            tvMAP.setText(Float.toString(meanArterialPressure));

            String strUnit = getString(unit == 0 ? R.string.bpm_mmHg : R.string.bpm_kpa);
            tvUnitSystolic.setText(strUnit);
            tvUnitDiastolic.setText(strUnit);
            tvUnitMAP.setText(strUnit);

            offset += 6;

            // show date time if it present
            if (timestampPresent) {
                // visible date & time text
                tvDateTimeDisconnected.setVisibility(View.GONE);
                tvTime.setVisibility(View.VISIBLE);
                tvDate.setVisibility(View.VISIBLE);

                final int year = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
                final int month = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 2);
                final int dayOfMonth = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 3);
                final int hourOfDay = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 4);
                final int minute = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 5);
                final int second = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 6);

                StringBuilder builderTime = new StringBuilder();
                builderTime.append(hourOfDay >= 10 ? hourOfDay : ("0" + hourOfDay))
                        .append(":").append(minute >= 10 ? minute : ("0" + minute))
                        .append(":").append(second >= 10 ? second : ("0" + second));
                tvTime.setText(builderTime.toString());

                StringBuilder builderDate = new StringBuilder();
                builderDate.append(year).append(" - ")
                        .append(month >= 10 ? month : ("0" + month)).append(" - ")
                        .append(dayOfMonth >= 10 ? dayOfMonth : ("0" + dayOfMonth));
                tvDate.setText(builderDate.toString());

                offset += 7;
            }

            // show pulse rate if it present
            if (pulseRatePresent) {
                final float pulseRate = gattCharacteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset);
                tvPulseRate.setText(Float.toString(pulseRate));
            }
        }
    }
}
