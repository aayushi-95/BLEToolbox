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
import android.widget.Toast;
import android.widget.ToggleButton;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LedFragment extends BaseFragment implements View.OnClickListener, IActivityToFragment {

    @Bind(R.id.togLed2)
    ToggleButton togLed2;

    @Bind(R.id.togLed3)
    ToggleButton togLed3;

    @Bind(R.id.togLed4)
    ToggleButton togLed4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_led, container, false);
        ButterKnife.bind(this, view);
        togLed2.setOnClickListener(this);
        togLed3.setOnClickListener(this);
        togLed4.setOnClickListener(this);
        return view;
    }

    private void setLedStatus(int indexLed, int statusLed) {
        switch (indexLed) {
            case 0:
                setLed(togLed2,statusLed);
                break;
            case 1:
                setLed(togLed3,statusLed);
                break;
            case 2:
                setLed(togLed4,statusLed);
                break;
            default:
                break;
        }
    }

    private void setLed(ToggleButton view ,int statusLed){
        if (statusLed == 1) {
            view.setChecked(true);
        } else {
            view.setChecked(false);
        }
    }

    @Override
    public void onClick(View view) {
        if (BLEService.State.STATE_CONNECTED == BLEService.INSTANCE.getConnectionState()) {
            byte[] value = new byte[2];
            switch (view.getId()) {
                case R.id.togLed2:
                    value[0] = 0x00;
                    ledProcessData(togLed2,value);
                    break;
                case R.id.togLed3:
                    value[0] = 0x01;
                    ledProcessData(togLed3,value);
                    break;
                case R.id.togLed4:
                    value[0] = 0x02;
                    ledProcessData(togLed4,value);
                    break;
                default:
                    break;
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_lost_connection), Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

    }

    private void ledProcessData(ToggleButton toggleButton,byte[] value){
        if (toggleButton.isChecked() == true) {
            toggleButton.setChecked(false);
            value[1] = 0x01;
        } else {
            toggleButton.setChecked(true);
            value[1] = 0x00;
        }
        ledPosData(value);
    }

    private void ledPosData(byte[] value) {
        BLEService.INSTANCE.writeData(BLEAttributes.LED_SERVICE, BLEAttributes.LED_CHARACTERISTIC_CONTROL,
                BLEService.Request.WRITE, value);
    }

    @Override
    public void upDateUI(BLEStateEvent.DataAvailable e) {
        BluetoothGattCharacteristic gattCharacteristic = e.characteristic;
        String charaterUuid = gattCharacteristic.getUuid().toString();
        if (BLEAttributes.LED_CHARACTERISTIC_CONTROL.toUpperCase().equals(charaterUuid.toUpperCase())) {
            final byte[] data = gattCharacteristic.getValue();
            setLedStatus(data[0], data[1]);
        }
    }

    @Override
    public void resetDefault() {

        if (togLed4.isChecked() == true) {
            byte[] value = new byte[2];
            value[0] = 0x02;
            value[1] = 0x00;
            ledPosData(value);
        }

        if (togLed3.isChecked() == true) {
            byte[] value = new byte[2];
            value[0] = 0x01;
            value[1] = 0x00;
            ledPosData(value);
        }

        if (togLed2.isChecked() == true) {
            byte[] value = new byte[2];
            value[0] = 0x00;
            value[1] = 0x00;
            ledPosData(value);
        }
    }
}
