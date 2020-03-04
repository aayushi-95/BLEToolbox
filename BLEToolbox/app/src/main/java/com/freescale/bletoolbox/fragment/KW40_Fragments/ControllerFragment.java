/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.fragment.KW40_Fragments;

import com.freescale.bletoolbox.fragment.IActivityToFragment;
import com.freescale.bletoolbox.fragment.BaseFragment;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ControllerFragment extends BaseFragment implements View.OnTouchListener, View.OnClickListener, IActivityToFragment {

    @Bind(R.id.controlMute)
    ImageView controlMute;

    @Bind(R.id.controlPower)
    ImageView controlPower;

    @Bind(R.id.controlVolumeUp)
    ImageView controlVolumeUp;

    @Bind(R.id.controlChannelUp)
    ImageView controlChannelUp;

    @Bind(R.id.controlVolumeDown)
    ImageView controlVolumeDown;

    @Bind(R.id.controlChannelDown)
    ImageView controlChannelDown;

    @Bind(R.id.controlSpinner)
    Spinner controlSpinner;

    private int LG = 0;
    private int SAMSUNG = 1;
    private int SONY = 2;
    private boolean chooseTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_controller, container, false);
        ButterKnife.bind(this, view);
        controlMute.setOnClickListener(this);
        controlMute.setOnTouchListener(this);

        controlPower.setOnClickListener(this);
        controlPower.setOnTouchListener(this);

        controlVolumeUp.setOnClickListener(this);
        controlVolumeUp.setOnTouchListener(this);

        controlChannelUp.setOnClickListener(this);
        controlChannelUp.setOnTouchListener(this);

        controlVolumeDown.setOnClickListener(this);
        controlVolumeDown.setOnTouchListener(this);

        controlChannelDown.setOnClickListener(this);
        controlChannelDown.setOnTouchListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chooseTV = false;
        controlSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    writeControlConfiguration(LG);
                } else if (position == 2) {
                    writeControlConfiguration(SAMSUNG);
                } else if (position == 3) {
                    writeControlConfiguration(SONY);
                } else {
                    chooseTV = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        List<String> categories;
        String[] arrCategories = getResources().getStringArray(R.array.remote_control_supported_tv);
        categories = Arrays.asList(arrCategories);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, categories);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        controlSpinner.setAdapter(dataAdapter);
    }

    public void controlCommand(int key) {
        if (chooseTV) {
            BLEService.INSTANCE.writeCharacteristic(BLEAttributes.CONTROLLER_SERVICE, BLEAttributes.CONTROLLER_CHARACTERISTIC_COMMAND, BLEService.Request.WRITE,
                    key, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        } else {
            /*Toast.makeText(getActivity(), R.string.remote_control_msg_select_tv, Toast.LENGTH_SHORT).show();*/
            MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
            builder.content(R.string.remote_control_msg_select_tv);
            builder.titleColor(ContextCompat.getColor(getActivity(), R.color.red));
            builder.positiveText(R.string.btOk);
//            builder.positiveColor(ContextCompat.getColor(getActivity(), R.color.red));
            builder.build().show();
        }
    }

    public void writeControlConfiguration(int key) {
        BLEService.INSTANCE.writeCharacteristic(BLEAttributes.CONTROLLER_SERVICE, BLEAttributes.CONTROLLER_CHARACTERISTIC_CONFIGURATION, BLEService.Request.WRITE,
                key, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
    }

    @Override
    public void upDateUI(BLEStateEvent.DataAvailable e) {
        if (e == null) return;
        BluetoothGattCharacteristic gattCharacteristic = e.characteristic;
        String charaterUuid = gattCharacteristic.getUuid().toString();
        if (BLEAttributes.CONTROLLER_CHARACTERISTIC_CONFIGURATION.toUpperCase().equals(charaterUuid.toUpperCase())) {
            if (gattCharacteristic.getValue()[0] == 0 || gattCharacteristic.getValue()[0] == 1 || gattCharacteristic.getValue()[0] == 2) {
                chooseTV = true;
            }
        } else if (BLEAttributes.CONTROLLER_CHARACTERISTIC_COMMAND.toUpperCase().equals(charaterUuid.toUpperCase())) {
        }
    }

    @Override
    public void resetDefault() {
        chooseTV = false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.controlMute:
                controlCommand(1);
                break;
            case R.id.controlPower:
                controlCommand(0);
                break;
            case R.id.controlVolumeUp:
                controlCommand(2);
                break;
            case R.id.controlChannelUp:
                controlCommand(4);
                break;
            case R.id.controlVolumeDown:
                controlCommand(3);
                break;
            case R.id.controlChannelDown:
                controlCommand(5);
                break;
            default:
                break;
        }
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
