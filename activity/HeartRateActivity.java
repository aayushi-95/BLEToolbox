/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.freescale.bletoolbox.AppConfig;
import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;
import com.freescale.bletoolbox.utility.BLEConverter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HeartRateActivity extends BaseServiceActivity {

    @Bind(R.id.heart_rate_measurement)
    TextView mHeartRateMeasurement;

    @Bind(R.id.heart_rate_measurement_unit)
    TextView mHeartRateUnit;

    @Bind(R.id.heart_rate_sensor_location)
    TextView mHeartRateSensorLocation;

    @Bind(R.id.heart_rate_chart_container)
    View mChartContainer;

    @Bind(R.id.heart_rate_chart)
    LineChart mHeartRateChart;

    // refresh the chart automatically
    private final Handler mHandler = new Handler();
    private final Runnable mChartRefresher = new Runnable() {

        @Override
        public void run() {
            refreshChart();
        }
    };

    /**
     * Last stored heart rate value.
     */
    private int pendingValue = -1;

    /**
     * Override super class method to clear all UI parts.
     *
     * @param e
     */
    @Override
    public void onEventMainThread(BLEStateEvent.Disconnected e) {
        super.onEventMainThread(e);
        mHeartRateMeasurement.setText("--");
        mHeartRateSensorLocation.setText("--");
        mHeartRateChart.getAxisLeft().setAxisMinValue(AppConfig.HEART_RATE_MIN_VALUE);
        mHeartRateChart.getAxisLeft().setAxisMaxValue(AppConfig.HEART_RATE_MAX_VALUE);
        mHeartRateChart.getData().getDataSetByIndex(0).clear();
        mHeartRateChart.getData().getXVals().clear();
        for (int i = 0; i <= AppConfig.HEART_RATE_TIME_RANGE; ++i) {
            if (i % 5 == 0) {
                mHeartRateChart.getData().getXVals().add(i + "");
            } else {
                mHeartRateChart.getData().getXVals().add("");
            }
        }
        mHeartRateChart.invalidate();
        mChartContainer.setVisibility(View.INVISIBLE);
        pendingValue = -1;
        mHandler.removeCallbacks(mChartRefresher);
    }

    /**
     * Override super class so we can send request of HeartRate information here.
     *
     * @param e
     */
    @Override
    public void onEvent(BLEStateEvent.ServiceDiscovered e) {
        super.onEvent(e);
        BLEService.INSTANCE.request(BLEAttributes.HEART_RATE_SERVICE, BLEAttributes.BODY_SENSOR_LOCATION, BLEService.Request.READ);
        BLEService.INSTANCE.request(BLEAttributes.HEART_RATE_SERVICE, BLEAttributes.HEART_RATE_MEASUREMENT, BLEService.Request.NOTIFY);
    }

    /**
     * Override super class to get data from received packet.
     *
     * @param e
     */
    @Override
    public void onEventMainThread(BLEStateEvent.DataAvailable e) {
        super.onEventMainThread(e);
        int assignedNumber = BLEConverter.getAssignedNumber(e.characteristic.getUuid());
        if (BLEAttributes.HEART_RATE_MEASUREMENT == assignedNumber) {
            int flag = e.characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            int format;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
            }
            int heartRate = e.characteristic.getIntValue(format, 1);
            mHeartRateMeasurement.setText(heartRate + "");
            if (pendingValue < 0) {
                pendingValue = heartRate;
                refreshChart();
            } else {
                pendingValue = heartRate;
            }
        } else if (BLEAttributes.BODY_SENSOR_LOCATION == assignedNumber) {
            int sensorLocationValue = e.characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            mHeartRateSensorLocation.setText(BLEConverter.fromBodySensorLocation(sensorLocationValue));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(R.string.app_heart_rate);
        configChart();
    }

    private void configChart() {
        LineDataSet dataSet = new LineDataSet(new ArrayList<Entry>(), null);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setLineWidth(2);
        dataSet.setColor(ContextCompat.getColor(this, R.color.deep_orange));

        List<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        LineData data = new LineData(new ArrayList<String>(), dataSets);
        // x values start from 0 to 20 (21 values total)
        for (int i = 0; i <= AppConfig.HEART_RATE_TIME_RANGE; ++i) {
            if (i % 5 == 0) {
                data.getXVals().add(i + "");
            } else {
                data.getXVals().add("");
            }
        }
        mHeartRateChart.setData(data);

        mHeartRateChart.setBackgroundColor(Color.TRANSPARENT);
        mHeartRateChart.setDrawGridBackground(false);
        mHeartRateChart.getLegend().setEnabled(false);
        mHeartRateChart.getAxisRight().setEnabled(false);
        mHeartRateChart.getAxisLeft().setStartAtZero(false);
        mHeartRateChart.getAxisLeft().setAxisMinValue(AppConfig.HEART_RATE_MIN_VALUE);
        mHeartRateChart.getAxisLeft().setAxisMaxValue(AppConfig.HEART_RATE_MAX_VALUE);
        //Get original Dimension
        float scaleRatio = getResources().getDisplayMetrics().density;
        float dimenPix = getResources().getDimension(R.dimen.heart_rate_chart_text_size);
        float dimenOrginal = dimenPix/scaleRatio;
        mHeartRateChart.getAxisLeft().setTextSize(dimenOrginal);
        mHeartRateChart.getAxisLeft().setLabelCount(5, false);
        mHeartRateChart.getAxisLeft().setValueFormatter(new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {
                if (value > 0) {
                    int intValue = (int) value;
                    if (intValue % 20 == 0) {
                        return intValue + "";
                    }
                }
                return "";
            }
        });
        mHeartRateChart.getAxisLeft().setDrawGridLines(false);
        mHeartRateChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mHeartRateChart.getXAxis().setTextSize(dimenOrginal);
        mHeartRateChart.getXAxis().setDrawGridLines(false);
        mHeartRateChart.setDescription("");
        mHeartRateChart.setTouchEnabled(false);
        mHeartRateChart.invalidate();
        mChartContainer.setVisibility(View.INVISIBLE);
    }

    private void addValueToChart(int value) {
        LineData lineData = mHeartRateChart.getData();
        LineDataSet lineDataSet = lineData.getDataSetByIndex(0);
        int count = lineDataSet.getEntryCount();
        if (count > AppConfig.HEART_RATE_TIME_RANGE) {
            if (count % 5 == 0) {
                lineData.getXVals().add(count + "");
            } else {
                lineData.getXVals().add("");
            }
        }
        if (value > mHeartRateChart.getAxisLeft().getAxisMaxValue()) {
            mHeartRateChart.getAxisLeft().setAxisMaxValue(value);
        }
        if (value < mHeartRateChart.getAxisLeft().getAxisMinValue()) {
            mHeartRateChart.getAxisLeft().setAxisMinValue(value);
        }

        lineData.addEntry(new Entry(value, count), 0);
    }

    private void refreshChart() {
        addValueToChart(pendingValue);
        mHeartRateChart.notifyDataSetChanged();
        mHeartRateChart.invalidate();
        mChartContainer.setVisibility(View.VISIBLE);
        mHandler.postDelayed(mChartRefresher, AppConfig.HEART_RATE_UPDATE_INTERVAL);
    }
}
