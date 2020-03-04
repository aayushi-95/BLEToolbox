/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.fragment.KW40_Fragments;

import com.freescale.bletoolbox.fragment.IActivityToFragment;
import com.freescale.bletoolbox.fragment.BaseFragment;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;
import com.freescale.bletoolbox.utility.BLEConverter;
import com.freescale.bletoolbox.view.Thermometer;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TemperatureFragment extends BaseFragment implements IActivityToFragment {

    @Bind(R.id.txtTemperature)
    TextView txtTemperature;

    @Bind(R.id.frgmentTemperature)
    FrameLayout frameLayout;

    private Thermometer temperatureCanvas;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_temperature, container, false);
        ButterKnife.bind(this, view);
        temperatureCanvas = new Thermometer(getActivity());
        frameLayout.addView(temperatureCanvas);
        txtTemperature.setVisibility(getView().GONE);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BLEService.INSTANCE.request(BLEAttributes.TEMPERATURE_SERVICE, BLEAttributes.TEMPERATURE_CHARACTERISTIC, BLEService.Request.NOTIFY);
    }

    @Override
    public void upDateUI(BLEStateEvent.DataAvailable e) {
        if (e == null) return;
        int assignedNumber = BLEConverter.getAssignedNumber(e.characteristic.getUuid());
        if (BLEAttributes.TEMPERATURE_CHARACTERISTIC == assignedNumber) {
            int format = BluetoothGattCharacteristic.FORMAT_SINT16;
            float temperatureCPU = (float) (e.characteristic.getIntValue(format, 0) * 0.01);
            temperatureCanvas.changeDrawLine(temperatureCPU);
            temperatureCanvas.invalidate();
            int y = (int) (temperatureCanvas.getTopHeight()) + 20;
            txtTemperature.setPadding(0, y, 0, 0);
            txtTemperature.setVisibility(getView().VISIBLE);
            txtTemperature.setText(getString(R.string.frdm_txt_temperature) + ": " + temperatureCPU + " \u2103");
        }
    }

    @Override
    public void resetDefault() {
        BLEService.INSTANCE.request(BLEAttributes.TEMPERATURE_SERVICE,
                BLEAttributes.TEMPERATURE_CHARACTERISTIC,
                BLEService.Request.DISABLE_NOTIFY_INDICATE);
    }
}


