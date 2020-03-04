/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.fragment.KW40_Fragments;

import com.freescale.bletoolbox.fragment.IActivityToFragment;
import com.freescale.bletoolbox.fragment.BaseFragment;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PotentiometerFragment extends BaseFragment implements IActivityToFragment {

    @Bind(R.id.iv_percent)
    ImageView ivPercent;
    @Bind(R.id.tv_percent)
    TextView tvPercent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_potentiometer, container, false);
        ButterKnife.bind(this, view);
        tvPercent.setVisibility(View.GONE);
        tvPercent.setText(BLANK);
        ivPercent.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BLEService.INSTANCE.request(BLEAttributes.POTENTIONMETER_SERVICE, BLEAttributes.POTENTIONMETER_CHARACTERISTIC, BLEService.Request.NOTIFY);
    }

    @Override
    public void upDateUI(BLEStateEvent.DataAvailable e) {
        if (e == null) return;
        BluetoothGattCharacteristic gattCharacteristic = e.characteristic;
        String charaterUuid = gattCharacteristic.getUuid().toString();
        if (BLEAttributes.POTENTIONMETER_CHARACTERISTIC.toUpperCase().equals(charaterUuid.toUpperCase())) {
            int percentValue = gattCharacteristic.getValue()[0];
            if (0 != percentValue) {
                float degree = 180f + (float)percentValue * 180f /100f;
                Log.d("POTENTIOMETER", percentValue + "%, degree " + degree);
                ivPercent.setRotation(degree);
                ivPercent.setVisibility(View.VISIBLE);
                tvPercent.setVisibility(View.VISIBLE);
                tvPercent.setText(BLANK + percentValue);
            } else {
                ivPercent.setRotation(180);
                ivPercent.setVisibility(View.GONE);
                tvPercent.setVisibility(View.VISIBLE);
                tvPercent.setText(BLANK + percentValue);
            }
        }
    }

    @Override
    public void resetDefault() {
        BLEService.INSTANCE.request(BLEAttributes.POTENTIONMETER_SERVICE,
                BLEAttributes.POTENTIONMETER_CHARACTERISTIC,
                BLEService.Request.DISABLE_NOTIFY_INDICATE);
    }
}
