/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.model.BLEDevice;
import com.freescale.bletoolbox.service.BLEService;
import com.freescale.bletoolbox.utility.BLEConverter;

import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;

public class IPconfig extends BaseServiceActivity implements View.OnClickListener {
    private static final String TAG = "IPConfig";

    @Bind(R.id.Submit)
    Button submit;
    @Bind(R.id.IP)
    EditText IP;
    @Bind(R.id.Subnet)
    EditText Subnet;
    @Bind(R.id.Gateway)
    EditText Gateway;

    private BLEDevice bleDevice;
    private UUID m_alertLevelUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(R.string.app_ipconfig);
        initLayout();
    }

    private void initLayout() {
        submit.setOnClickListener(this);
     /*   m_btAlerHighImmed.setOnClickListener(this);
        m_btAlerMildImmed.setOnClickListener(this);
        m_btAlerOffImmed.setOnClickListener(this);
        m_btAlerHighImmed.setEnabled(false);
        m_btAlerMildImmed.setEnabled(false);
        m_btAlerOffImmed.setEnabled(false); */
    }

    public int rssi =0;
    @DebugLog
    public void onEventMainThread(BLEStateEvent.DeviceRssiUpdated e) {
       // m_tvRssi.setText(e.rssi + "");
     //   rssi = e.rssi;
    }

    /**
     * Override super class method to clear all UI parts.
     * @param e
     */
    @Override
    public void onEventMainThread(BLEStateEvent.Disconnected e) {
        super.onEventMainThread(e);
        initLayout();
    }

    /**
     * Override super class so we can send request of PROXIMITY information here.
     * @param e
     */
    @Override
    public void onEvent(BLEStateEvent.ServiceDiscovered e) {
        super.onEvent(e);

    }

    /**
     * Override super class to get data from received packet.
     * @param e
     */
    @Override
    public void onEventMainThread(BLEStateEvent.DataAvailable e) {
        super.onEventMainThread(e);
        BluetoothGattCharacteristic gattCharacteristic = e.characteristic;
        int assignedNumber = BLEConverter.getAssignedNumber(gattCharacteristic.getUuid());
        switch (assignedNumber) {
           /* case BLEAttributes.ALERT_LEVEL:
                handleAlertLevel(gattCharacteristic);
                m_alertLevelUUID = gattCharacteristic.getUuid();
                break; */

            default:
                Log.d(TAG, Integer.toHexString(assignedNumber));
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.Submit:
                setIPconfig();
                break;
            default:
        }
    }
    private void showToast(String text) {
        Toast.makeText(IPconfig.this,text,Toast.LENGTH_SHORT).show();
    }

    private void setIPconfig() {
        sendData(IP.getText().toString());
        sendData(Subnet.getText().toString());
        sendData(Gateway.getText().toString());
        //showToast(text);
    }
        //editTextIP.setVisibility(1);
    private void sendData(String text)
    {
        String[] token = text.split("\\.");
        for(int i=0;i<token.length;i++) {
            int ip = Integer.parseInt(token[i]);
            Log.d("Check", "" +ip);
            BLEService.INSTANCE.writeCharacteristic(BLEAttributes.IMMEDIATE_ALERT_SERVICE, BLEAttributes.ALERT_LEVEL, BLEService.Request.WRITE_NO_RESPONSE,
                    ip, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        }
    }

}