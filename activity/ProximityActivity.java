/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.freescale.bletoolbox.AppConfig;
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

public class ProximityActivity extends BaseServiceActivity implements View.OnClickListener {
    private static final String TAG = "PROXIMITY";
    private static final int ALERT_LEVEL_OFF = 0x00;
    private static final int ALERT_LEVEL_MILD = 0x01;
    private static final int ALERT_LEVEL_HIGH = 0x02;
    @Bind(R.id.prox_tvRssi)
    TextView m_tvRssi;
    @Bind(R.id.layoutTxPower)
    ViewGroup m_layoutTxPower;
    @Bind(R.id.prox_tvTxPower)
    TextView m_tvTxPower;
    @Bind(R.id.prox_tvAlerLevel)
    TextView m_tvAlerLevel;
    @Bind(R.id.prox_btAlertHigh)
    Button m_btAlerHigh;
    @Bind(R.id.prox_btAlertMild)
    Button m_btAlerMild;
    @Bind(R.id.prox_btAlertOff)
    Button m_btAlerOff;
    @Bind(R.id.layoutImmediate)
    ViewGroup m_layoutImmediate;
    @Bind(R.id.prox_btAlertHighImmed)
    Button m_btAlerHighImmed;
    @Bind(R.id.prox_btAlertMildImmed)
    Button m_btAlerMildImmed;
    @Bind(R.id.prox_btAlertOffImmed)
    Button m_btAlerOffImmed;


    private BLEDevice bleDevice;
//    bleDevice.getRssi()

    private UUID m_alertLevelUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(R.string.app_proximity);
        initLayout();
    }

    private void initLayout() {
        m_tvRssi.setText(R.string.dashx3);
        m_tvTxPower.setText(R.string.dashx3);
        m_tvAlerLevel.setText(R.string.dashx3);
        m_btAlerHigh.setOnClickListener(this);
        m_btAlerMild.setOnClickListener(this);
        m_btAlerOff.setOnClickListener(this);
        m_btAlerHigh.setEnabled(false);
        m_btAlerMild.setEnabled(false);
        m_btAlerOff.setEnabled(false);
        m_btAlerHighImmed.setOnClickListener(this);
        m_btAlerMildImmed.setOnClickListener(this);
        m_btAlerOffImmed.setOnClickListener(this);
        m_btAlerHighImmed.setEnabled(false);
        m_btAlerMildImmed.setEnabled(false);
        m_btAlerOffImmed.setEnabled(false);
    }

    public int rssi =0;
    @DebugLog
    public void onEventMainThread(BLEStateEvent.DeviceRssiUpdated e) {
        m_tvRssi.setText(e.rssi + "");
        rssi = e.rssi;


    }

    /**
     * Override super class method to clear all UI parts.
     * @param e
     */
    @Override
    public void onEventMainThread(BLEStateEvent.Disconnected e) {
        super.onEventMainThread(e);
        initLayout();
        m_tvAlerLevel.setVisibility(View.INVISIBLE);
        mHandler.removeCallbacks(mTxPowerServiceRunner);
    }

    /**
     * Override super class so we can send request of PROXIMITY information here.
     * @param e
     */
    @Override
    public void onEvent(BLEStateEvent.ServiceDiscovered e) {
        super.onEvent(e);
        //
        boolean linkLossServiceFound = BLEService.INSTANCE.getService(BLEAttributes.LINK_LOSS_SERVICE) != null;
        if (!linkLossServiceFound) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    toggleState(false);
                }
            });
            mHandler.removeCallbacks(mTxPowerServiceRunner);
            return;
        }
        //
        boolean immediateAlertServiceFound = BLEService.INSTANCE.getService(BLEAttributes.IMMEDIATE_ALERT_SERVICE) != null;
        if (!immediateAlertServiceFound) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    /*m_layoutImmediate.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));*/
                    m_btAlerHighImmed.setEnabled(false);
                    m_btAlerMildImmed.setEnabled(false);
                    m_btAlerOffImmed.setEnabled(false);
                }
            });
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    m_btAlerHighImmed.setEnabled(true);
                    m_btAlerMildImmed.setEnabled(true);
                    m_btAlerOffImmed.setEnabled(true);
                }
            });
        }
        //
        boolean txPowerServiceFound = BLEService.INSTANCE.getService(BLEAttributes.TX_POWER_SERVICE) != null;
        if (!txPowerServiceFound) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    /*\m_layoutTxPower.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));*/
                }
            });
        }
        //
        requestLinkLossService();
        if (txPowerServiceFound) {
            requestTxPowerService();
        }
        requestRssi();
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
            case BLEAttributes.ALERT_LEVEL:
                handleAlertLevel(gattCharacteristic);
                m_alertLevelUUID = gattCharacteristic.getUuid();
                break;
            case BLEAttributes.TX_POWER_LEVEL:
                handleTxPower(gattCharacteristic);
                break;
            default:
                Log.d(TAG, Integer.toHexString(assignedNumber));
        }
    }

    /**
     * 0x00 Value 0, meaning “No Alert”
     * 0x01 Value 1, meaning “Mild Alert”
     * 0x02 Value 2, meaning “High Alert
     */
    private void handleAlertLevel(@NonNull BluetoothGattCharacteristic gattCharacteristic) {
        int alertLevel = -1;
        try {
            alertLevel = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            Log.d("PRINTING", "First Print : alertLevel" + alertLevel);
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        UUID serviceUuid = gattCharacteristic.getService().getUuid();
        if (BLEConverter.getAssignedNumber(serviceUuid) == BLEAttributes.IMMEDIATE_ALERT_SERVICE) {
            StringBuilder meg = new StringBuilder("Service IMMEDIATE ALERT return alert ");
            switch (alertLevel) {
                case ALERT_LEVEL_OFF:
                    meg.append("0 NO ALERT");
                    break;
                case ALERT_LEVEL_MILD:
                    meg.append("1 MILD ALERT");
                    break;
                case ALERT_LEVEL_HIGH:
                    meg.append("2 HIGH ALERT");
                    break;
                default:
                    meg.append(Integer.toHexString(alertLevel));
            }
            /*Toast.makeText(this, meg.toString(), Toast.LENGTH_SHORT).show();*/
            return;
        } else if (!(BLEConverter.getAssignedNumber(serviceUuid) == BLEAttributes.LINK_LOSS_SERVICE)) {
            StringBuilder meg = new StringBuilder("Service ");
            meg.append(gattCharacteristic.getService().getUuid());
            meg.append(" return alert ");
            meg.append(Integer.toHexString(alertLevel));
            Log.d(TAG, meg.toString());
            return;
        }

        m_tvAlerLevel.setVisibility(View.VISIBLE);
        // 0x00 Value 0, meaning “No Alert”
        // 0x01 Value 1, meaning “Mild Alert”
        // 0x02 Value 2, meaning “High Alert
        switch (alertLevel) {
            case ALERT_LEVEL_OFF:
                m_tvAlerLevel.setText("0 NO ALERT");
                m_tvAlerLevel.setBackground(ContextCompat.getDrawable(this, R.drawable.no_alert_bg));
                m_btAlerHigh.setEnabled(true);
                m_btAlerMild.setEnabled(true);
                m_btAlerOff.setEnabled(false);
                if (Build.VERSION.SDK_INT >= 23)
                    m_tvAlerLevel.setTextColor(getColor(R.color.philips_blue));
                else
                    m_tvAlerLevel.setTextColor(getResources().getColor(R.color.philips_blue));
                break;
            case ALERT_LEVEL_MILD:
                m_tvAlerLevel.setText("1 MILD ALERT");
                m_tvAlerLevel.setBackground(ContextCompat.getDrawable(this, R.drawable.mild_alert_bg));
                m_btAlerHigh.setEnabled(true);
                m_btAlerMild.setEnabled(false);
                m_btAlerOff.setEnabled(true);
                if (Build.VERSION.SDK_INT >= 23)
                    m_tvAlerLevel.setTextColor(getColor(R.color.deep_orange));
                else
                    m_tvAlerLevel.setTextColor(getResources().getColor(R.color.deep_orange));
                break;
            case ALERT_LEVEL_HIGH:
                m_tvAlerLevel.setText("2 HIGH ALERT");
                m_tvAlerLevel.setBackground(ContextCompat.getDrawable(this, R.drawable.high_alert_bg));
                m_btAlerHigh.setEnabled(false);
                m_btAlerMild.setEnabled(true);
                m_btAlerOff.setEnabled(true);
                if (Build.VERSION.SDK_INT >= 23)
                    m_tvAlerLevel.setTextColor(getColor(R.color.text_red));
                else
                    m_tvAlerLevel.setTextColor(getResources().getColor(R.color.text_red));
                break;
            default:
                m_tvAlerLevel.setText(Integer.toHexString(alertLevel));
                m_btAlerHigh.setEnabled(true);
                m_btAlerMild.setEnabled(true);
                m_btAlerOff.setEnabled(true);
        }
    }

    /**
     * value 0x12 is interpreted as +18dBm
     * value 0xEE is interpreted as -18dBm
     * value = -100 --> +20
     */
    private void handleTxPower(@NonNull BluetoothGattCharacteristic gattCharacteristic) {
        //value 0x12 is interpreted as +18dBm
        //value 0xEE is interpreted as -18dBm
        int txPowerLevel = -101;
        try {
            txPowerLevel = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT8, 0); // -100 --> +20
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        m_tvTxPower.setText(String.valueOf(txPowerLevel));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prox_btAlertOff:
                setAlertLevel(ALERT_LEVEL_OFF);
                break;
            case R.id.prox_btAlertMild:
                setAlertLevel(ALERT_LEVEL_MILD);
                break;
            case R.id.prox_btAlertHigh:
                setAlertLevel(ALERT_LEVEL_HIGH);
                break;
            case R.id.prox_btAlertOffImmed:
                setAlertLevelImmediate(ALERT_LEVEL_OFF);
                break;
            case R.id.prox_btAlertMildImmed:
                setAlertLevelImmediate(ALERT_LEVEL_MILD);
                break;
            case R.id.prox_btAlertHighImmed:
                setAlertLevelImmediate(ALERT_LEVEL_HIGH);
                break;
            default:
                //
        }
    }


    private void setAlertLevel(int alertLevel) {
       BLEService.INSTANCE.writeCharacteristic(BLEAttributes.LINK_LOSS_SERVICE, BLEAttributes.ALERT_LEVEL, BLEService.Request.WRITE,
                alertLevel, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
    }

    private void setAlertLevelImmediate(int alertLevel) {
      //  BLEService.INSTANCE.writeCharacteristic(BLEAttributes.IMMEDIATE_ALERT_SERVICE, BLEAttributes.ALERT_LEVEL, BLEService.Request.WRITE_NO_RESPONSE,
             //   alertLevel, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
       // Log.d("PRINTING", "In Gatt" + alertLevel);
    }

    private void requestLinkLossService() {
        boolean linkLossServiceFound = BLEService.INSTANCE.request(BLEAttributes.LINK_LOSS_SERVICE, BLEAttributes.ALERT_LEVEL, BLEService.Request.READ);
        if (!linkLossServiceFound) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    toggleState(false);
                }
            });
            mHandler.removeCallbacks(mTxPowerServiceRunner);
        }
    }

    private final Runnable mTxPowerServiceRunner = new Runnable() {
        @Override
        public void run() {
            requestTxPowerService();
            requestLinkLossService();
        }
    };

    private void requestTxPowerService() {
        //
        boolean txPowerServiceFound = BLEService.INSTANCE.request(BLEAttributes.TX_POWER_SERVICE, BLEAttributes.TX_POWER_LEVEL, BLEService.Request.READ);
        if (!txPowerServiceFound) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    /*m_layoutTxPower.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));*/
                }
            });
        }
        //
        mHandler.removeCallbacks(mTxPowerServiceRunner);
        mHandler.postDelayed(mTxPowerServiceRunner, AppConfig.PROXIMITY_TX_POWER_UPDATE_INTERVAL);
    }

    private final Runnable mRssiRunner = new Runnable() {
        @Override
        public void run() {
            requestRssi();
            Log.d("RSSI","rssi written is" +rssi);
           // BLEService.INSTANCE.writeCharacteristic(BLEAttributes.LINK_LOSS_SERVICE, BLEAttributes.ALERT_LEVEL, BLEService.Request.WRITE,
             //       rssi, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        }
    };


    private void requestRssi() {
        //
        boolean canGetRssi = BLEService.INSTANCE.requestRemoteRssi();
        if (!canGetRssi) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    /*m_tvRssi.setBackgroundColor(ContextCompat.getColor(ProximityActivity.this, android.R.color.darker_gray));*/
                    m_tvRssi.setText(R.string.dashx3);
                }
            });
        }
        //
        mHandler.removeCallbacks(mRssiRunner);
        mHandler.postDelayed(mRssiRunner, AppConfig.PROXIMITY_RSSI_UPDATE_INTERVAL);
    }
}