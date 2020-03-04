/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.activity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;
import com.freescale.bletoolbox.utility.BLEConverter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GlucoseActivity extends BaseServiceActivity implements View.OnClickListener {

    @Bind(R.id.tv_glu)
    TextView tvGMvalue;

    @Bind(R.id.tv_glu_time)
    TextView tvGMTime;

    @Bind(R.id.tv_glu_date)
    TextView tvGMDate;

    @Bind(R.id.btn_glu_get)
    Button btnGet;

    @Bind(R.id.iv_next)
    ImageView ivNext;
    @Bind(R.id.iv_previous)
    ImageView ivPrevious;

    public static final int UNIT_kgpl = 0;
    public static final int UNIT_molpl = 1;
    //
    private static final byte OP_CODE_REPORT_STORED_RECORDS = 0x01; //Operator: Value from Operator Table
    private static final byte OP_CODE_DELETE_STORED_RECORDS = 0x02; //Operator: Value from Operator Table
    private static final byte OP_CODE_ABORT_OPERATION = 0x03; //Operator: Null 'value of 0x00 from Operator Table'
    private static final byte OP_CODE_REPORT_NUMBER_OF_STORED_RECORDS = 0x04; //Operator: Value from Operator Table
    private static final byte OP_CODE_NUMBER_OF_STORED_RECORDS_RESPONSE = 0x05; //Operator: Null 'value of 0x00 from Operator Table'
    private static final byte OP_CODE_RESPONSE_CODE = 0x06; //Operator: Null 'value of 0x00 from Operator Table'
    //
    private static final byte OPERATOR_NULL = 0x00;
    private static final byte OPERATOR_ALL_RECORDS = 0x01;
    private static final byte OPERATOR_LESS_OR_EQUAL = 0x02;
    private static final byte OPERATOR_GREATER_OR_EQUAL = 0x03;
    private static final byte OPERATOR_WITHIN_RANGE = 0x04;
    private static final byte OPERATOR_FIRST_RECORD = 0x05;
    private static final byte OPERATOR_LAST_RECORD = 0x06;
    //
    private static final byte RESPONSE_NUMBER_OF_RECORDS = 0x05;
    private static final byte RESPONSE_REQUEST_OP_CODE_RESPONSE_CODE_VALUE = 0x06;
    private static final byte RESPONSE_CODE_SUCESS = 0x01;

    private List<GMData> mListGMValue;
    private int mCurrentValueIdx = 0;
    private int m_numberOfStoredRecords = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glucose);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(R.string.app_glucose);

        initLayout();
        ivNext.setOnClickListener(this);
        ivPrevious.setOnClickListener(this);

        mListGMValue = new ArrayList<>(3);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_next:
                if (mCurrentValueIdx < mListGMValue.size() - 1)
                    mCurrentValueIdx ++;
                displayGMDataByIndex(mCurrentValueIdx);
                break;
            case R.id.iv_previous:
                if (mCurrentValueIdx > 0)
                    mCurrentValueIdx --;
                displayGMDataByIndex(mCurrentValueIdx);
                break;
            case R.id.btn_glu_get:
                if (btnGet.getText().toString().equals(getString(R.string.glu_ABORT))) {
                    abortFuntion();
                } else {
                    getCountRecords();
                }
                break;
        }
    }

    /**
     * init layout
     */
    private void initLayout() {
        tvGMvalue.setText(R.string.dashx3);
        tvGMTime.setText(R.string.dashx3);
        tvGMDate.setText(R.string.dashx3);
        tvGMDate.setVisibility(View.GONE);

        ivNext.setEnabled(false);
        ivPrevious.setEnabled(false);

        btnGet.setEnabled(false);
        btnGet.setText(getString(R.string.glu_GET));
        btnGet.setOnClickListener(this);
        btnGet.setVisibility(View.INVISIBLE);
    }

    /**
     * request to get Glucose measurement characteristic
     */
    private void getAllRecords() {
        initLayout();
        mListGMValue.clear();
        btnGet.setEnabled(true);
        btnGet.setVisibility(View.VISIBLE);
        byte[] data = new byte[2];
        data[0] = OP_CODE_REPORT_STORED_RECORDS;
        data[1] = OPERATOR_ALL_RECORDS;
        BLEService.INSTANCE.writeDataWithAuthen(BLEAttributes.GLUCOSE_SERVICE, BLEAttributes.GLUCOSE_RECORD_ACCESS_CONTROL_POINT, data);
    }

    private void getCountRecords() {
        byte[] data = new byte[2];
        data[0] = OP_CODE_REPORT_NUMBER_OF_STORED_RECORDS;
        data[1] = OPERATOR_ALL_RECORDS;
        BLEService.INSTANCE.writeDataWithAuthen(BLEAttributes.GLUCOSE_SERVICE, BLEAttributes.GLUCOSE_RECORD_ACCESS_CONTROL_POINT, data);
    }

    private void abortFuntion() {
        byte[] data = new byte[2];
        data[0] = OP_CODE_ABORT_OPERATION;
        data[1] = OPERATOR_NULL;
        BLEService.INSTANCE.writeDataWithAuthen(BLEAttributes.GLUCOSE_SERVICE, BLEAttributes.GLUCOSE_RECORD_ACCESS_CONTROL_POINT, data);
    }

    /**
     *
     * @param currentIdx
     */
    private void displayGMDataByIndex(int currentIdx) {
        GMData data = mListGMValue.get(currentIdx);

        if (data.unit == UNIT_kgpl){
            float gmValue = data.value * 100000.0f;
            tvGMvalue.setText(new DecimalFormat("##.##").format(gmValue) + " " + getString(R.string.glu_unit_mgdl));
        }
        else if(data.unit == UNIT_molpl){
            float gmValue = data.value * 1000f * 18.018018f;
            tvGMvalue.setText(new DecimalFormat("##.##").format(gmValue) + " " + getString(R.string.glu_unit_mgdl));
        }

        // date & time
        if (data.calendar != null){
            StringBuilder builderTime = new StringBuilder();
            builderTime.append(data.calendar.get(Calendar.HOUR_OF_DAY) >= 10 ?
                    data.calendar.get(Calendar.HOUR_OF_DAY) : "0" + data.calendar.get(Calendar.HOUR_OF_DAY));
            builderTime.append(":");
            builderTime.append(data.calendar.get(Calendar.MINUTE) >= 10 ?
                    data.calendar.get(Calendar.MINUTE) : "0" + data.calendar.get(Calendar.MINUTE));
            tvGMTime.setText(builderTime.toString());

            StringBuilder builderDate = new StringBuilder();
            builderDate.append(data.calendar.get(Calendar.YEAR));
            builderDate.append(" - ");
            builderDate.append(data.calendar.get(Calendar.MONTH) + 1 >= 10 ?
                    (data.calendar.get(Calendar.MONTH) + 1) : "0" + (data.calendar.get(Calendar.MONTH) + 1));
            builderDate.append(" - ");
            builderDate.append(data.calendar.get(        Calendar.DAY_OF_MONTH) >= 10 ?
                    data.calendar.get(Calendar.DAY_OF_MONTH) : "0" + data.calendar.get(Calendar.DAY_OF_MONTH));
            tvGMDate.setText(builderDate.toString());
        }

        switch (currentIdx) {
            case 0:
                ivNext.setEnabled(true);
                ivPrevious.setEnabled(false);
                break;
            case 1:
                ivPrevious.setEnabled(true);

                if (mListGMValue.size() >= 3)
                    ivNext.setEnabled(true);
                else
                    ivNext.setEnabled(false);
                break;
            case 2:
                ivNext.setEnabled(false);
                ivPrevious.setEnabled(true);
                break;
        }
    }

    /**
     * always keep maximum 3 values in list
     * @param data
     */
    private void addGMData(GMData data) {
        if (mListGMValue == null)
            mListGMValue = new ArrayList<>(3);

        if (mListGMValue.size() < 3)
            mListGMValue.add(data);
        else {
            mListGMValue.remove(0);
            mListGMValue.add(data);
        }

        tvGMDate.setVisibility(View.VISIBLE);

        // show data
        mCurrentValueIdx = mListGMValue.size() - 1;
        if (data.unit == UNIT_kgpl){
            float gmValue = data.value * 100000.0f;
            tvGMvalue.setText(new DecimalFormat("##.##").format(gmValue) + " " + getString(R.string.glu_unit_mgdl));
        }
        else if(data.unit == UNIT_molpl){
            float gmValue = data.value * 1000f * 18.018018f;
            tvGMvalue.setText(new DecimalFormat("##.##").format(gmValue) + " " + getString(R.string.glu_unit_mgdl));
        }

        // date & time
        if (data.calendar != null){
            StringBuilder builderTime = new StringBuilder();
            builderTime.append(data.calendar.get(Calendar.HOUR_OF_DAY) >= 10 ?
                    data.calendar.get(Calendar.HOUR_OF_DAY) : "0" + data.calendar.get(Calendar.HOUR_OF_DAY));
            builderTime.append(":");
            builderTime.append(data.calendar.get(Calendar.MINUTE) >= 10 ?
                    data.calendar.get(Calendar.MINUTE) : "0" + data.calendar.get(Calendar.MINUTE));
            tvGMTime.setText(builderTime.toString());

            StringBuilder builderDate = new StringBuilder();
            builderDate.append(data.calendar.get(Calendar.YEAR));
            builderDate.append(" - ");
            builderDate.append(data.calendar.get(Calendar.MONTH) + 1 >= 10 ?
                    (data.calendar.get(Calendar.MONTH) + 1) : "0" + (data.calendar.get(Calendar.MONTH) + 1));
            builderDate.append(" - ");
            builderDate.append(data.calendar.get(        Calendar.DAY_OF_MONTH) >= 10 ?
                    data.calendar.get(Calendar.DAY_OF_MONTH) : "0" + data.calendar.get(Calendar.DAY_OF_MONTH));
            tvGMDate.setText(builderDate.toString());
        }

        // check button status
        switch (mListGMValue.size()){
            case 0:
            case 1:
                ivNext.setEnabled(false);
                ivPrevious.setEnabled(false);
                break;
            case 2:
                ivNext.setEnabled(false);
                ivPrevious.setEnabled(true);
                break;
            case 3:
                ivNext.setEnabled(false);
                ivPrevious.setEnabled(true);
                break;
        }

    }

    @Override
    public void onEvent(BLEStateEvent.ServiceDiscovered e) {
        super.onEvent(e);
        BLEService.INSTANCE.request(BLEAttributes.GLUCOSE_SERVICE, BLEAttributes.GLUCOSE_MEASUREMENT, BLEService.Request.NOTIFY);
        BLEService.INSTANCE.request(BLEAttributes.GLUCOSE_SERVICE, BLEAttributes.GLUCOSE_MEASUREMENT_CONTEXT, BLEService.Request.NOTIFY);
        boolean foundRACP = BLEService.INSTANCE.request(BLEAttributes.GLUCOSE_SERVICE, BLEAttributes.GLUCOSE_RECORD_ACCESS_CONTROL_POINT,
                BLEService.Request.INDICATE);
        if (BluetoothDevice.BOND_BONDED == e.bondState) {
            Log.d(GlucoseActivity.class.getSimpleName(), "BONDED");
            // visible GET button
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnGet.setVisibility(View.VISIBLE);
                    btnGet.setEnabled(true);
                }
            });
        } else {
            Log.d(GlucoseActivity.class.getSimpleName(), "UNBONDED");
            // invisible GET button
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnGet.setVisibility(View.INVISIBLE);
                    btnGet.setEnabled(false);
                }
            });
        }
    }

    @Override
    public void onEventMainThread(BLEStateEvent.Disconnected e) {
        super.onEventMainThread(e);
        initLayout();
        mListGMValue.clear();
    }

    @Override
    public void onEventMainThread(BLEStateEvent.Connected e) {
        super.onEventMainThread(e);
    }

    @Override
    public void onEventMainThread(BLEStateEvent.BluetoothStateChanged e) {
        super.onEventMainThread(e);
    }

    @Override
    public void onEventMainThread(BLEStateEvent.DataAvailable e) {
        super.onEventMainThread(e);
        if (e == null)
            return;

        BluetoothGattCharacteristic gattCharacteristic = e.characteristic;
//        Log.e("glu", "onEventMainThread(BLEStateEvent.DataAvailable e) " + gattCharacteristic.getValue());

        int assignedNumber = BLEConverter.getAssignedNumber(gattCharacteristic.getUuid());
//        Log.e("glu", " getUuid assignedNumber " + assignedNumber);

        if (assignedNumber == BLEAttributes.GLUCOSE_MEASUREMENT) {
            int offset = 0;
            Log.e("glu", "GLUCOSE_MEASUREMENT " + gattCharacteristic.getValue());

            final int flags = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            offset += 1;

            final boolean timeOffsetPresent = (flags & 0x01) > 0;
            final boolean typeAndLocaltionPresent = (flags & 0x02) > 0;
            final int concentrationUnit = (flags & 0x03) > 0 ? UNIT_molpl : UNIT_kgpl;
            offset += 2;

            final int year = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
            final int month = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 2);
            final int dayOfMonth = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 3);
            final int hourOfDay = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 4);
            final int minute = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 5);
            final int second = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 6);

//            Log.e("glu", "y : " + year
//                            + "\nm : " + month
//                            + "\nd : " + dayOfMonth
//                            + "\nh : " + hourOfDay
//                            + "\nminute : " + minute
//                            + "\nsecond : " + second
//            );

            offset += 7;

            int timeOffset = 0;
            if (timeOffsetPresent) {
                timeOffset = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, offset);
//                Log.e("glu", "timeOffset : " + timeOffset);
                offset += 2;
            }

            final Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, dayOfMonth, hourOfDay, minute + timeOffset, second);

            float glucoseConcentration = 0;
            if (typeAndLocaltionPresent) {
                glucoseConcentration  = gattCharacteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset);
//                Log.e("glu", "glucoseConcentration : " + glucoseConcentration);
            }

            addGMData(new GMData(glucoseConcentration, concentrationUnit, calendar));
            if (btnGet.getText().toString().equals(getString(R.string.glu_ABORT)) && m_numberOfStoredRecords == mListGMValue.size()) {
                // complete getting
                btnGet.setText(R.string.glu_GET);
            }
        } else if (BLEAttributes.GLUCOSE_RECORD_ACCESS_CONTROL_POINT == assignedNumber) {
            handleWriteRecordAccessControlPoint(e);
        } else if (BLEAttributes.GLUCOSE_MEASUREMENT_CONTEXT == assignedNumber) {
            // TODO handle measurement context
        }
    }

    private void handleWriteRecordAccessControlPoint(BLEStateEvent.DataAvailable e) {
        BluetoothGattCharacteristic gattCharacteristic = e.characteristic;
        int offset = 0;
        final int value = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
        // For all other profiles, writes the data formatted in HEX.
        final byte[] data = gattCharacteristic.getValue();
        if (4 > data.length) {
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                Log.d(GlucoseActivity.class.getSimpleName(), stringBuilder.toString());
//                Toast.makeText(this, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
            }
            return;
        }
        switch (data[0]) {
            case RESPONSE_NUMBER_OF_RECORDS:
                receiveResponseNumberOfRecords(data);
                break;
            case RESPONSE_REQUEST_OP_CODE_RESPONSE_CODE_VALUE:
                receiveResponseRequestOpCodeResponseCodeValue(data);
                break;
        }
    }

    private void receiveResponseNumberOfRecords(byte[] data) {
        if (null == data || 4 > data.length) {
            return;
        }
        m_numberOfStoredRecords = data[2];
        if (0 >= m_numberOfStoredRecords) {
            Toast.makeText(this, R.string.glu_NotFoundRecord, Toast.LENGTH_SHORT).show();
        } else {
            //
            // fix maximum of number records = 3
            //
            if (3 < m_numberOfStoredRecords) {
                m_numberOfStoredRecords = 3;
            }
            String meg = getString(R.string.glu_Downloading);
            Toast.makeText(this, String.format(meg, m_numberOfStoredRecords), Toast.LENGTH_SHORT).show();
            getAllRecords();
            btnGet.setText(R.string.glu_ABORT);
        }
    }

    private void receiveResponseRequestOpCodeResponseCodeValue(byte[] data) {
        if (null == data || 4 > data.length) {
            return;
        }
        switch (data[2]) {
            case OP_CODE_REPORT_STORED_RECORDS:
                if (RESPONSE_CODE_SUCESS == data[3]) {
                    // do nothing
                }
                break;
            case OP_CODE_ABORT_OPERATION:
                if (RESPONSE_CODE_SUCESS == data[3]) {
                    btnGet.setText(R.string.glu_GET);
                }
                break;
        }
    }

    private class GMData {
        public final float value;
        public final int unit;
        public final Calendar calendar;

        public GMData(float value, int unit, Calendar calendar){
            this.value  = value;
            this.unit   = unit;
            this.calendar = calendar;
        }
    }

    /**
     * This instance handles bonding state event to correctly execute last failed task.
     *
     * @param e
     */
    public void onEvent(BLEStateEvent.DeviceBondStateChanged e) {
        // check connection when bond state changed
        if (BLEService.State.STATE_CONNECTED != BLEService.INSTANCE.getConnectionState()) {
            return;
        }
        //
        if (BluetoothDevice.BOND_BONDED == e.bondState) {
            Log.d(GlucoseActivity.class.getSimpleName(), "BONDED");
            // visible GET button
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(GlucoseActivity.this, "BONDED", Toast.LENGTH_SHORT).show();
                    btnGet.setVisibility(View.VISIBLE);
                    btnGet.setEnabled(true);
                }
            });
        } else if (e.bondState == BluetoothDevice.BOND_BONDING) {
            Log.d(GlucoseActivity.class.getSimpleName(), "BOND_BONDING");
            // invisible GET button
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(GlucoseActivity.this, "BOND_BONDING", Toast.LENGTH_SHORT).show();
                    btnGet.setVisibility(View.INVISIBLE);
                    btnGet.setEnabled(false);
                }
            });
        } else if (e.bondState == BluetoothDevice.BOND_NONE) {
            Log.d(GlucoseActivity.class.getSimpleName(), "BOND_NONE");
            // invisible GET button
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(GlucoseActivity.this, "BOND_NONE", Toast.LENGTH_SHORT).show();
                    btnGet.setVisibility(View.INVISIBLE);
                    btnGet.setEnabled(false);
                }
            });
        } else {
            Log.d(GlucoseActivity.class.getSimpleName(), "UN-KNOW STATE");
            // invisible GET button
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(GlucoseActivity.this, "UN-KNOW STATE", Toast.LENGTH_SHORT).show();
                    btnGet.setVisibility(View.INVISIBLE);
                    btnGet.setEnabled(false);
                }
            });
        }
    }
}
