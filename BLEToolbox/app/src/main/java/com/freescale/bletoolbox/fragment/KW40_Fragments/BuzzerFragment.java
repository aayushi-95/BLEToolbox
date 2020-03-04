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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BuzzerFragment extends BaseFragment implements View.OnTouchListener, View.OnClickListener, IActivityToFragment {

    @Bind(R.id.lnBuzzer)
    LinearLayout lnBuzzer;

    @Bind(R.id.imgBuzzer)
    ImageView imgBuzzer;

    @Bind(R.id.txtBuzzer)
    TextView txtBuzzer;

    private boolean isOnBuzzer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_buzzer, container, false);
        ButterKnife.bind(this, view);
        lnBuzzer.setOnClickListener(this);
        lnBuzzer.setOnTouchListener(this);
        isOnBuzzer = false;
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lnBuzzer:
                byte[] value = new byte[1];
                if (isOnBuzzer) {
                    value[0] = 0x00;
                    isOnBuzzer = false;
                } else {
                    value[0] = 0x01;
                    isOnBuzzer = true;
                }
                controlBuzzer(value);
                break;
        }
    }

    private void controlBuzzer(byte[] value) {
        BLEService.INSTANCE.writeData(BLEAttributes.BUZZER_SERVICE, BLEAttributes.BUZZER_CHARACTERISTIC,
                BLEService.Request.WRITE, value);
    }

    public void controlBuzzerUI(int value) {
        if (value == 1) {
            txtBuzzer.setText(R.string.frdm_buzzer_on);
            imgBuzzer.setBackgroundResource(R.drawable.buzzer_on);
        } else if(value == 0) {
            txtBuzzer.setText(R.string.frdm_buzzer_off);
            imgBuzzer.setBackgroundResource(R.drawable.buzzer_off);
        }
    }

    @Override
    public void upDateUI(BLEStateEvent.DataAvailable e) {
        if (e == null) return;
        BluetoothGattCharacteristic gattCharacteristic = e.characteristic;
        String charaterUuid = gattCharacteristic.getUuid().toString();
        if (BLEAttributes.BUZZER_CHARACTERISTIC.toUpperCase().equals(charaterUuid.toUpperCase())) {
            controlBuzzerUI(gattCharacteristic.getValue()[0]);
        }
    }

    @Override
    public void resetDefault() {
        // buzzer off
        byte[] value = new byte[1];
        value[0] = 0x00;
        BLEService.INSTANCE.writeData(BLEAttributes.BUZZER_SERVICE, BLEAttributes.BUZZER_CHARACTERISTIC,
                BLEService.Request.WRITE, value);
        controlBuzzerUI(0);
    }


    public void seeThroughAlpha(View view) {
        view.setAlpha((float) 0.5);
    }

    public void seeTransparent(View view) {
        view.setAlpha((float) 1.0);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                seeThroughAlpha(view);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                seeTransparent(view);
                break;
            }
        }
        return false;
    }
}
