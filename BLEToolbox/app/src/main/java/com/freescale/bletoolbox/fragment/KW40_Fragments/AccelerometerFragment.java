/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.fragment.KW40_Fragments;

import com.freescale.bletoolbox.fragment.IActivityToFragment;
import com.freescale.bletoolbox.fragment.BaseFragment;


import android.bluetooth.BluetoothGattCharacteristic;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AccelerometerFragment extends BaseFragment implements View.OnTouchListener, IActivityToFragment, View.OnClickListener {

    @Bind(R.id.chart)
    LineChart lineChart;

    @Bind(R.id.tvAcclerometterStart)
    TextView tvAcclerometterStart;

    private boolean startAcceloremeter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_accelerometer, container, false);
        ButterKnife.bind(this, view);
        configChart();
        tvAcclerometterStart.setOnClickListener(this);
        tvAcclerometterStart.setOnTouchListener(this);
        tvAcclerometterStart.setText(getString(R.string.accelerometer_stop));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startAcceloremeter = true;
        BLEService.INSTANCE.request(BLEAttributes.ACCELEROMETER_SERVICE, BLEAttributes.ACCELEROMETER_CHARACTERISTIC_READING, BLEService.Request.NOTIFY);
    }

    @Override
    public void upDateUI(BLEStateEvent.DataAvailable e) {
        if (e == null) return;
        BluetoothGattCharacteristic gattCharacteristic = e.characteristic;
        Log.e("accelerometer",gattCharacteristic.toString());
        String charaterUuid = gattCharacteristic.getUuid().toString();
        if (BLEAttributes.ACCELEROMETER_CHARACTERISTIC_SCALE.toUpperCase().equals(charaterUuid.toUpperCase())) {
        } else if (BLEAttributes.ACCELEROMETER_CHARACTERISTIC_READING.toUpperCase().equals(charaterUuid.toUpperCase())) {
            if (startAcceloremeter) {
                int x = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 0);
                int y = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 2);
                int z = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 4);
                addValueXYZ(x, y, z);
                Log.d("ACCELEROMETER", "x = " + x + " , y = " + y + " , z = " + z);
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
                lineChart.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BLEService.INSTANCE.request(BLEAttributes.ACCELEROMETER_SERVICE,
                BLEAttributes.ACCELEROMETER_CHARACTERISTIC_READING,
                BLEService.Request.DISABLE_NOTIFY_INDICATE);
    }

    private void addValueXYZ(int x, int y, int z) {
        LineData lineData = lineChart.getData();
        LineDataSet lineDataSetX = lineData.getDataSetByIndex(0);
        LineDataSet lineDataSetY = lineData.getDataSetByIndex(1);
        LineDataSet lineDataSetZ = lineData.getDataSetByIndex(2);
        int count = lineDataSetX.getEntryCount();
        if (lineData.getXValCount() < count) {
            int index_time = count / 10;
            lineData.getXVals().add("" + index_time);
            lineData.getXVals().remove(0);
            for (int i = 0; i < count; i++) {
                Entry e = lineDataSetX.getEntryForXIndex(i);
                if (e == null) continue;
                e.setXIndex(e.getXIndex() - 1);

                Entry eY = lineDataSetY.getEntryForXIndex(i);
                if (e == null) continue;
                eY.setXIndex(eY.getXIndex() - 1);

                Entry eZ = lineDataSetZ.getEntryForXIndex(i);
                if (eZ == null) continue;
                eZ.setXIndex(eZ.getXIndex() - 1);
            }
        }
        lineData.addEntry(new Entry(x, count), 0);
        lineData.addEntry(new Entry(y, count), 1);
        lineData.addEntry(new Entry(z, count), 2);
    }

    @Override
    public void resetDefault() {
    }

    private void configChart() {
        LineDataSet dataSetX = new LineDataSet(new ArrayList<Entry>(), null);
        dataSetX.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetX.setDrawCircles(false);
        dataSetX.setDrawValues(false);
        dataSetX.setLineWidth(2);
        dataSetX.setColor(Color.RED);

        LineDataSet dataSetY = new LineDataSet(new ArrayList<Entry>(), null);
        dataSetY.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetY.setDrawCircles(false);
        dataSetY.setDrawValues(false);
        dataSetY.setLineWidth(2);
        dataSetY.setColor(Color.GREEN);

        LineDataSet dataSetZ = new LineDataSet(new ArrayList<Entry>(), null);
        dataSetZ.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetZ.setDrawCircles(false);
        dataSetZ.setDrawValues(false);
        dataSetZ.setLineWidth(2);
        dataSetZ.setColor(Color.BLUE);

        List<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetX);
        dataSets.add(dataSetY);
        dataSets.add(dataSetZ);

        LineData data = new LineData(new ArrayList<String>(), dataSets);
        for (int i = 0; i <= 100; ++i) {
            int index_time = i / 10;
            data.getXVals().add(index_time + "");
        }
        lineChart.setData(data);
        lineChart.setBackgroundColor(Color.TRANSPARENT);
        lineChart.setDrawGridBackground(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setStartAtZero(false);
        lineChart.getAxisLeft().setAxisMinValue(-8000);
        lineChart.getAxisLeft().setAxisMaxValue(8000);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.setDescription("");
        lineChart.setTouchEnabled(false);
        lineChart.getAxisLeft().setLabelCount(8000, false);
        lineChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) Math.floor(value));
            }
        });
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setTextColor(Color.BLACK);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setDrawGridLines(true);
        lineChart.setAutoScaleMinMaxEnabled(true);
        lineChart.animateY(1);
        lineChart.animateX(1);
        lineChart.getXAxis().setLabelsToSkip(9);
        lineChart.invalidate();
        lineChart.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvAcclerometterStart:
                if (startAcceloremeter) {
                    startAcceloremeter = false;
                    tvAcclerometterStart.setText(getString(R.string.accelerometer_start));
                    BLEService.INSTANCE.request(BLEAttributes.ACCELEROMETER_SERVICE,
                            BLEAttributes.ACCELEROMETER_CHARACTERISTIC_READING,
                            BLEService.Request.DISABLE_NOTIFY_INDICATE);
                } else {
                    startAcceloremeter = true;
                    tvAcclerometterStart.setText(getString(R.string.accelerometer_stop));
                    BLEService.INSTANCE.request(BLEAttributes.ACCELEROMETER_SERVICE,
                            BLEAttributes.ACCELEROMETER_CHARACTERISTIC_READING,
                            BLEService.Request.NOTIFY);
                }
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
