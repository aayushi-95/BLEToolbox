package com.freescale.bletoolbox.fragment.Sersor_Fragments;

import android.bluetooth.BluetoothGattCharacteristic;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.fragment.BaseFragment;
import com.freescale.bletoolbox.fragment.IActivityToFragment;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nxf42542 on 2018/7/27.
 */

public class SensorAccelerometerFragment extends BaseFragment implements View.OnTouchListener, IActivityToFragment,View.OnClickListener {
    @Bind(R.id.chart)
    LineChart lineChart;

    @Bind(R.id.tvAcclerometterStart)
    TextView tvAcclerometterStart;

    private boolean startAcceloremeter;

    public String subTitle;

    private int yAxisMax = 2;
    private int yAxisMin = yAxisMax;


    @Bind(R.id.xAxis)
    TextView xAxis;
    @Bind(R.id.xAxisColor)
    ImageView xAxisColor;

    @Bind(R.id.yAxis)
    TextView yAxis;
    @Bind(R.id.yAxisColor)
    ImageView yAxisColor;

    @Bind(R.id.zAxis)
    TextView zAxis;
    @Bind(R.id.zAxisColor)
    ImageView zAxisColor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_sensor_accelerometer, container, false);
        ButterKnife.bind(this, view);


        configChartMark(3);
        if (subTitle.equals("Accelerometerrr")){
            yAxisMax = 2;
            yAxisMin = yAxisMax;
        }else if(subTitle.equals("Magnetometer")){
            yAxisMax = 800;
            yAxisMin = yAxisMax;
        }else if (subTitle.equals("Gyroscope")){
            yAxisMax = 250;
            yAxisMin = yAxisMax;
        }else if (subTitle.equals("Barometer")){
            yAxisMax = 5;
            yAxisMin = 2;
            configChartMark(1);

        }

        configChart();
        tvAcclerometterStart.setOnClickListener(this);
        tvAcclerometterStart.setOnTouchListener(this);
        tvAcclerometterStart.setText(subTitle + " Stop");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startAcceloremeter = true;
    }

    @Override
    public void upDateUI(BLEStateEvent.DataAvailable e) {
        if (e == null) return;
        BluetoothGattCharacteristic gattCharacteristic = e.characteristic;
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
    public void onEventMainThread(BLEStateEvent.DataWritenFromClient e) {
        if (e == null || !startAcceloremeter) return;
        byte[] buffering= e.value;
        String value = new String(buffering, Charset.forName("ASCII"));
        if (subTitle.equals("Accelerometer")){
            parseAccelerometerValue(value);
        }else if(subTitle.equals("Magnetometer")){
            parseMagnetometerValue(value);
        }else if (subTitle.equals("Gyroscope")){
            parseGyroscopeValue(value);
        }else if (subTitle.equals("Barometer")){
            parseBarometerValue(value);
        }
    }
    private void parseMagnetometerValue(String value){
        StringBuffer buffer = new StringBuffer(value);
        String strH = "MAG: X=";
        String strY = "uT Y=";
        String strZ = "uT Z=";
        String strE = "uT\n";
        buffer.delete(buffer.indexOf(strH),buffer.indexOf(strH) + strH.length());
        buffer.replace(buffer.indexOf(strY),buffer.indexOf(strY) + strY.length(),",");
        buffer.replace(buffer.indexOf(strZ),buffer.indexOf(strZ) + strZ.length(),",");
        buffer.delete(buffer.indexOf(strE),buffer.indexOf(strE) + strE.length());

        String[] values = buffer.toString().split(",");

        float x = Float.parseFloat(values[0]);
        float y = Float.parseFloat(values[1]);
        float z = Float.parseFloat(values[2]);
        addValueXYZ(x, y, z);
        Log.d("ACCELEROMETER", "x = " + x + " , y = " + y + " , z = " + z);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
        lineChart.setVisibility(View.VISIBLE);
    }
    private void parseGyroscopeValue(String value){
        StringBuffer buffer = new StringBuffer(value);
        String strH = "GYRO: X=";
        String strY = "d/s Y=";
        String strZ = "d/s Z=";
        String strE = "d/s\n";
        buffer.delete(buffer.indexOf(strH),buffer.indexOf(strH) + strH.length());
        buffer.replace(buffer.indexOf(strY),buffer.indexOf(strY) + strY.length(),",");
        buffer.replace(buffer.indexOf(strZ),buffer.indexOf(strZ) + strZ.length(),",");
        buffer.delete(buffer.indexOf(strE),buffer.indexOf(strE) + strE.length());

        String[] values = buffer.toString().split(",");

        float x = Float.parseFloat(values[0]);
        float y = Float.parseFloat(values[1]);
        float z = Float.parseFloat(values[2]);
        addValueXYZ(x, y, z);
        Log.d("ACCELEROMETER", "x = " + x + " , y = " + y + " , z = " + z);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
        lineChart.setVisibility(View.VISIBLE);
    }
    private void parseBarometerValue(String value){
        StringBuffer buffer = new StringBuffer(value);
        String strH = "PRESSURE: ";
        String strE = "Bar\n";
        buffer.delete(buffer.indexOf(strH),buffer.indexOf(strH) + strH.length());
        buffer.delete(buffer.indexOf(strE),buffer.indexOf(strE) + strE.length());


        float x = Float.parseFloat(buffer.toString());
        float y = Float.parseFloat(buffer.toString());
        float z = Float.parseFloat(buffer.toString());
        addValueXYZ(x, y, z);
        Log.d("PRESSURE", "x = " + x + " , y = " + y + " , z = " + z);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
        lineChart.setVisibility(View.VISIBLE);
    }

    private void parseAccelerometerValue(String value){
        StringBuffer buffer = new StringBuffer(value);
        String strH = "ACCEL: X=";
        String strY = "g Y=";
        String strZ = "g Z=";
        String strE = "g\n";
        buffer.delete(buffer.indexOf(strH),buffer.indexOf(strH) + strH.length());
        buffer.replace(buffer.indexOf(strY),buffer.indexOf(strY) + strY.length(),",");
        buffer.replace(buffer.indexOf(strZ),buffer.indexOf(strZ) + strZ.length(),",");
        buffer.delete(buffer.indexOf(strE),buffer.indexOf(strE) + strE.length());

        String[] values = buffer.toString().split(",");

        float x = Float.parseFloat(values[0]);
        float y = Float.parseFloat(values[1]);
        float z = Float.parseFloat(values[2]);
        addValueXYZ(x, y, z);
        Log.d("ACCELEROMETER", "x = " + x + " , y = " + y + " , z = " + z);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
        lineChart.setVisibility(View.VISIBLE);
    }
    private float convertByteArrToFloat(byte[] value, int  index){
        int l;
        l = value[index + 0];
        l &= 0xff;
        l |= ((long) value[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) value[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) value[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void addValueXYZ(float x, float y, float z) {
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

    private void configChartMark(int lineNumber){
        if (lineNumber == 3){
            xAxis.setVisibility(View.VISIBLE);
            xAxisColor.setVisibility(View.VISIBLE);

            yAxisColor.setBackgroundColor(getResources().getColor(R.color.text_green));
            yAxis.setText("Y-Axis");

            zAxis.setVisibility(View.VISIBLE);
            zAxisColor.setVisibility(View.VISIBLE);
        }else if(lineNumber == 1) {
            xAxis.setVisibility(View.INVISIBLE);
            xAxisColor.setVisibility(View.INVISIBLE);

            yAxisColor.setBackgroundColor(getResources().getColor(R.color.philips_blue));
            yAxis.setText("Z-Axis");

            zAxis.setVisibility(View.INVISIBLE);
            zAxisColor.setVisibility(View.INVISIBLE);
        }
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
        lineChart.getAxisLeft().setAxisMinValue(-yAxisMin);
        lineChart.getAxisLeft().setAxisMaxValue(yAxisMax);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.setDescription("");
        lineChart.setTouchEnabled(false);
        lineChart.getAxisLeft().setLabelCount(yAxisMax, false);
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
                    tvAcclerometterStart.setText(subTitle + " Start");

                } else {
                    startAcceloremeter = true;
                    tvAcclerometterStart.setText(subTitle + " Stop");
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
