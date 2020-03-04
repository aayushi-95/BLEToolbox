package com.freescale.bletoolbox.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.freescale.bletoolbox.AppConfig;
import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;
import com.freescale.bletoolbox.utility.BLEConverter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nxf42542 on 2018/6/22.
 */

public class QppActivity extends BaseServiceActivity implements View.OnClickListener {
    protected static final String TAG = QppActivity.class.getSimpleName();



    @Bind(R.id.text_device_name)
     TextView textDeviceName;
    @Bind(R.id.text_device_address)
     TextView textDeviceAddress;
    @Bind(R.id.text_connection_state)
     TextView textConnectionStatus;

    // / receive data
    @Bind(R.id.text_qpp_notify)
     TextView textQppNotify;
    @Bind(R.id.text_qpp_data_rate)
     TextView textQppDataRate;

    private boolean dataRecvFlag = false;
    private long qppSumDataReceived = 0; // / summary of data received.
    private long qppRecvDataTime = 0;

    // / send
    @Bind(R.id.edit_send)
     EditText editSend;
    @Bind(R.id.btn_qpp_text_send)
     Button btnQppTextSend;

    // / repeat start
    @Bind(R.id.text_repeat_counter)
     TextView textRepeatCounter;
    @Bind(R.id.cb_repeat)
     CheckBox checkboxRepeat;


    private boolean writeWithResponse = false;

    private boolean connect = false;
    private boolean qppSendDataState = false;
    private boolean qppSendRepeat = false;
    private long qppRepeatCounter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qpp);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        qppSendRepeat = checkboxRepeat.isChecked();

        initUI();
    }
    private void initUI()
    {
        btnQppTextSend.setOnClickListener(this);
        checkboxRepeat.setOnClickListener(this);
        String deviceName = getIntent().getStringExtra(INTENT_KEY_NAME);
        textDeviceName.setText(deviceName);
        System.out.print(textDeviceName.toString());
        textDeviceAddress.setText(mDeviceAddress);
        textConnectionStatus.setText("");
        textRepeatCounter.setText(Long.toString(qppRepeatCounter));
    }

    final Handler handlerQppDataRate = new Handler();
    final Runnable runnableQppDataRate = new Runnable() {
        public void run() {
            qppRecvDataTime++;
            textQppDataRate.setText(" " + qppSumDataReceived / qppRecvDataTime + " Bps");

            dataRecvFlag = false;
        }
    };
    final Runnable runnableSend = new Runnable() {
        byte[] getPackageData() {
            String strInput;

            strInput = editSend.getText().toString();
            if (strInput.length() == 0)
                return null;
            if (strInput.length() % 2 == 1) {
                strInput = "0" + strInput;
            }
            return BLEConverter.hexStr2Bytes(strInput);
        }

        private boolean QppSendNextData() {
            byte[] data = getPackageData();
            if (data == null) {
                Log.e(TAG, "data is empty");
                return false;
            }
            if (!connect || !btnQppTextSend.getText().toString().equals("Stop")){
                return false;
            }

            int length = data.length;
            int count = 0;
            int offset = 0;
            while (offset < length) {
                if ((length - offset) < AppConfig.qppServerBufferSize)
                    count = length - offset;
                else
                    count = AppConfig.qppServerBufferSize;
                byte tempArray[] = new byte[count];
                System.arraycopy(data, offset, tempArray, 0, count);
                // PrintBytes(data);

                boolean sendSeccuss = BLEService.INSTANCE.writeData(BLEAttributes.QPP_UUID_SERVICE, BLEAttributes.QPP_WRITE_CHARACTERISTIC,writeWithResponse?1:4,tempArray);



                if (sendSeccuss){
                    offset = offset + count;
                }

                try {
                    Thread.sleep(40);

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return true;
        }
        public void run() {

            do {
                if (QppSendNextData()) {
                    qppRepeatCounter++;
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            textRepeatCounter.setText(Long.toString(qppRepeatCounter));
                        }
                    });
                } else {
                    // add error counter?
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }while (qppSendRepeat);// if writeWithResponse is ture, while statement is invalid
            sendBtnSetTitle("Send");
        }
    };
    private void sendBtnSetTitle(final String str){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnQppTextSend.setText(str);
            }
        });
    }
    private void sendData(){

        final String title = btnQppTextSend.getText().toString().equals("Stop")?"Send":"Stop";
        sendBtnSetTitle(title);
        Thread sendDataThread = new Thread(runnableSend);
        sendDataThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_qpp_text_send:
                Log.e("--------","iiiiiiiiiiii");
                sendData();
                break;
            case R.id.cb_repeat:
                qppSendRepeat = checkboxRepeat.isChecked();
                break;
            default:
                break;
        }
    }


    @Override
    public void onEvent(BLEStateEvent.ServiceDiscovered e){
        super.onEvent(e);
        BLEService.INSTANCE.request(BLEAttributes.QPP_UUID_SERVICE, BLEAttributes.QPP_NOTIFY_CHARACTERISTIC, BLEService.Request.NOTIFY);
    }

    @Override
    public void onEventMainThread(BLEStateEvent.DataAvailable e){
        super.onEventMainThread(e);
        if (e == null)
            return;

        BluetoothGattCharacteristic characteristic = e.characteristic;
        String characteristicUUID = characteristic.getUuid().toString().toUpperCase();

        if (characteristicUUID.equals( BLEAttributes.QPP_NOTIFY_CHARACTERISTIC)) {
            byte[] notifyByte = characteristic.getValue();
            try{
                if (!dataRecvFlag) {
                    handlerQppDataRate.postDelayed(runnableQppDataRate, 1000);
                    dataRecvFlag = true;
                }
                qppSumDataReceived = qppSumDataReceived + notifyByte.length;

                String notifyMessage =  BLEConverter.bytes2HexStr(notifyByte);
                textQppNotify.setText("0x"+ notifyMessage);

            }catch (Exception ex){
            }
        }else if(characteristicUUID.equals( BLEAttributes.QPP_WRITE_CHARACTERISTIC)){
        }else{
            Log.e("0 0 0  0 0 0 0 0 0","y y y y y y y y y y y y");
        }
    }


    @Override
    public void onEventMainThread(BLEStateEvent.Connecting e){
        super.onEventMainThread(e);
        textConnectionStatus.setText("Connecting");
        connect = false;
    }
    @Override
    public void onEventMainThread(BLEStateEvent.Connected e){
        super.onEventMainThread(e);
        textConnectionStatus.setText("Connected");
        connect = true;
        qppRepeatCounter = 0;
        textRepeatCounter.setText(Long.toString(qppRepeatCounter));
    }
    @Override
    public  void onEventMainThread(BLEStateEvent.Disconnected e){
        super.onEventMainThread(e);
        textConnectionStatus.setText("Disconnected");
        connect = false;
        qppRepeatCounter = 0;
        textRepeatCounter.setText(Long.toString(qppRepeatCounter));
    }
}
