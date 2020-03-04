/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;

import java.nio.charset.Charset;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;

public class WuartActivity extends BaseServiceActivity {
    // MTU
    public static final int MTU_WUART = 247;
    private int currentMTU = 0;
    //
    private static final String TAG = "WuartActivity";

    @Bind(R.id.wuart_field)
    EditText mMessageField;
    @Bind(R.id.wuart_all_messages)
    TextView wuartAllMessages;
    @Bind(R.id.sv_wuart)
    ScrollView scrollView;
    private MenuItem itemUart;

    private byte[] buffering;
    private boolean isConsoleMode;

    @Override
    public void onEventMainThread(BLEStateEvent.Disconnected e) {
        super.onEventMainThread(e);
        itemUart.setVisible(false);
        mMessageField.setEnabled(false);
        mMessageField.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideKeyboard();
            }
        }, 400);
        hideKeyboard();
        // request mtu
        currentMTU = 0;
    }

    @Override
    public void onEvent(BLEStateEvent.ServiceDiscovered e) {
        super.onEvent(e);
    }

    @DebugLog
    public void onEvent(BLEStateEvent.MTUUpdated mtuUpdated) {
        Log.d(TAG, "mtuUpdated = " + mtuUpdated.mtuSize + " success " + mtuUpdated.success);
        // request mtu start
        if (currentMTU != mtuUpdated.mtuSize) {
            BLEService.INSTANCE.requestMTU(mtuUpdated.mtuSize);
            currentMTU = mtuUpdated.mtuSize;
        }
        // request mtu end
    }

    public void onEventMainThread(BLEStateEvent.ServiceDiscovered e) {
        if (BLEService.INSTANCE.getService(BLEAttributes.DEVICE_INFORMATION_SERVICE) != null) {
            mMessageField.setEnabled(true);
            mMessageField.requestFocus();
            mMessageField.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showKeyboard(mMessageField);
                }
            }, 400);
            setConsoleMode(true);
            itemUart.setVisible(true);
            mInfoButton.setVisibility(View.VISIBLE);
        }
    }

    public void onEventMainThread(BLEStateEvent.DataWritenFromClient e) {
        if (buffering == null) {
            buffering = e.value;
        } else {
            byte[] destination = new byte[buffering.length + e.value.length];
            System.arraycopy(buffering, 0, destination, 0, buffering.length);
            System.arraycopy(e.value, 0, destination, buffering.length, e.value.length);
            buffering = destination;
        }
        receiveMessage();
    }

    @Override
    public void onEventMainThread(BLEStateEvent.DataAvailable e) {
        super.onEventMainThread(e);
        if (e == null) return;
        BluetoothGattCharacteristic gattCharacteristic = e.characteristic;
        final String charaterUuid = gattCharacteristic.getUuid().toString();
        //Log.d(TAG, "uuid = " + gattCharacteristic.getUuid().toString());
        if (BLEAttributes.CHAR_WUART_STREAM.equalsIgnoreCase(charaterUuid)) {
            Log.d(TAG, "CHAR_WUART_STREAM uuid = " + gattCharacteristic.getUuid().toString());
            final byte[] data = gattCharacteristic.getValue();
            if (null != data && 0 < data.length) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                Log.d(TAG, "WUART_STREAM data " + stringBuilder.toString());
            }
        }
        // request mtu start
        if (currentMTU == 0) {
            BLEService.INSTANCE.requestMTU(MTU_WUART);
            currentMTU = MTU_WUART;
        }
        // request mtu end
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wuart);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*getSupportActionBar().setSubtitle(R.string.app_w_console_uart);*/

        mMessageField.setEnabled(false);
        mMessageField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (isConsoleMode) {
                        sendMessage(mMessageField.getText().toString() + "\n");
                    }
                    return true;
                }
                return false;
            }
        });

        mMessageField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isConsoleMode) {
                    return;
                }
                if (s.length() > 0) {
                    sendMessage(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        wuartAllMessages.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_uart, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean result = super.onPrepareOptionsMenu(menu);
        itemUart = menu.findItem(R.id.menu_uart);
        itemUart.setVisible(false);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_uart) {
            setConsoleMode(!WuartActivity.this.isConsoleMode);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setConsoleMode(boolean isConsoleMode) {
        if (isConsoleMode) {
            getSupportActionBar().setSubtitle(R.string.app_w_console);
            itemUart.setTitle(R.string.uart_mode_uart);
        } else {
            getSupportActionBar().setSubtitle(R.string.app_w_uart);
            itemUart.setTitle(R.string.uart_mode_console);
        }
        itemUart.setVisible(true);
        wuartAllMessages.setText("");
        mMessageField.setText("");
        this.isConsoleMode = isConsoleMode;
    }

    @Override
    protected boolean needUartSupport() {
        return true;
    }

    private void sendMessage(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        // the new UI on May 30 2016
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(wuartAllMessages.getText());
        int start = sb.length();
        sb.append(content);
        // change text color
        sb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.Deep_Red_Color)), start, sb.length(), 0);
        wuartAllMessages.setText(sb);
        // send data
        byte[] c = content.getBytes();
        byte[] data = new byte[c.length + 1];
        System.arraycopy(c, 0, data, 0, c.length);
        data[c.length] = 0x0D;
        int chunkCount = data.length / 20 + 1;
        for (int i = 0; i < chunkCount; ++i) {
            byte[] chunk = Arrays.copyOfRange(data, i * 20, Math.min(data.length, i * 20 + 20));
            Log.e(" CMD SEND :",Arrays.toString(chunk));
            BLEService.INSTANCE.requestWrite(BLEAttributes.WUART, BLEAttributes.UART_STREAM, chunk);
        }
        mMessageField.setText("");
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    private void receiveMessage() {

//        if (isConsoleMode) {
//            if (buffering.length >= AppConfig.MAX_WUART_BUFFER ||
//                    buffering[buffering.length - 1] == '\r' || buffering[buffering.length - 1] == '\n') {
//                /*String consoleMsg = new String(buffering, Charset.forName("ASCII"));
//                // reset data
//                buffering = null;*/
//            } else {
//                return;
//            }
//        }
        // the new UI on May 30 2016
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(wuartAllMessages.getText());
        int start = sb.length();
        String value = new String(buffering, Charset.forName("ASCII"));
        if (isConsoleMode && !value.contains("\n")) {
            value = value + "\n";
        }
        sb.append(value);
        // change text color
        sb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.Philips_Blue_Color)), start, sb.length(), 0);
        wuartAllMessages.setText(sb);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        buffering = null;
    }
}
