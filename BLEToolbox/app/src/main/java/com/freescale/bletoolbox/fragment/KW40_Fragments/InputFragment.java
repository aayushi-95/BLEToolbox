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
import android.widget.ImageView;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;
import com.freescale.bletoolbox.utility.BLEConverter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InputFragment extends BaseFragment implements IActivityToFragment {
    @Bind(R.id.img_sw1)
    ImageView imgSw1;

    @Bind(R.id.img_sw2)
    ImageView imgSw2;

    @Bind(R.id.img_sw3)
    ImageView imgSw3;

    private boolean sW1Off = false;
    private boolean sW2Off = false;
    private boolean sW3Off = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BLEService.INSTANCE.request(BLEAttributes.INPUT_SERVICE, BLEAttributes.INPUT_CHARACTERISTIC, BLEService.Request.NOTIFY);
    }

    private void convertByte(byte value) {
        String binaryString = BLEConverter.convertByteToBinaryString(value);
        updateUI(binaryString);
    }

    private void updateUI(String binaryString) {
        if (binaryString.equals("00000100")) {
            if (!sW1Off) {
                sW1Off = true;
                imgSw1.setBackgroundResource(R.drawable.input_on);
            } else {
                sW1Off = false;
                imgSw1.setBackgroundResource(R.drawable.input_off);
            }
        }
        if (binaryString.equals("00000010")) {
            if (!sW2Off) {
                sW2Off = true;
                imgSw2.setBackgroundResource(R.drawable.input_on);
            } else {
                sW2Off = false;
                imgSw2.setBackgroundResource(R.drawable.input_off);
            }
        }
        if (binaryString.equals("00000001")) {
            if (!sW3Off) {
                sW3Off = true;
                imgSw3.setBackgroundResource(R.drawable.input_on);
            } else {
                sW3Off = false;
                imgSw3.setBackgroundResource(R.drawable.input_off);
            }
        }
    }

    @Override
    public void upDateUI(BLEStateEvent.DataAvailable e) {
        if (e == null) return;
        BluetoothGattCharacteristic gattCharacteristic = e.characteristic;
        String charaterUuid = gattCharacteristic.getUuid().toString();
        if (BLEAttributes.INPUT_CHARACTERISTIC.toUpperCase().equals(charaterUuid.toUpperCase())) {
            convertByte(gattCharacteristic.getValue()[0]);
        }
    }

    @Override
    public void resetDefault() {
        BLEService.INSTANCE.request(BLEAttributes.INPUT_SERVICE,
                BLEAttributes.INPUT_CHARACTERISTIC,
                BLEService.Request.DISABLE_NOTIFY_INDICATE);
        sW1Off = false;
        sW2Off = false;
        sW3Off = false;
        imgSw1.setBackgroundResource(R.drawable.input_off);
        imgSw2.setBackgroundResource(R.drawable.input_off);
        imgSw3.setBackgroundResource(R.drawable.input_off);
    }
}
