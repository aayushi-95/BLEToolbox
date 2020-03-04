package com.freescale.bletoolbox.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nxf42542 on 2018/7/16.
 */

public class RegisterInterfaceActivity extends BaseServiceActivity implements View.OnClickListener{

    ScrollView registerScrollView;
    TextView registerDataView;

    EditText offset;
    EditText bytes;

    Button sensorType;
    Button sensorOperation;
    Button go;
    Button clear;

    private byte[] buffering;
    private List <String> types;
    private List <String> operation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_interface);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(getString(R.string.sensor_register_interface));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initializeData();
        initializeView();
    }
    private void initializeData(){
        types = Arrays.asList(getResources().getStringArray(R.array.sensor_register_type));
        operation = Arrays.asList(getResources().getStringArray(R.array.sensor_register_operation));
    }
    private void initializeView()
    {
        registerScrollView = (ScrollView)findViewById(R.id.register_scrollView);
        registerDataView = (TextView)findViewById(R.id.register_interface_data_view);

        offset = (EditText) findViewById(R.id.register_offset);
        bytes = (EditText)findViewById(R.id.register_bytes);

        sensorType = (Button) findViewById(R.id.register_sensor_type);
        sensorOperation = (Button)findViewById(R.id.register_sensor_operation);
        go = (Button)findViewById(R.id.register_go);
        clear = (Button)findViewById(R.id.register_clear);

        sensorType.setOnClickListener(this);
        sensorOperation.setOnClickListener(this);
        go.setOnClickListener(this);
        clear.setOnClickListener(this);


        sensorType.setText((CharSequence) types.get(0));
        sensorOperation.setText(operation.get(0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.register_sensor_type: {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterInterfaceActivity.this);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("Sensor Selection");
                final CharSequence[] titles = types.toArray(new CharSequence[types.size()]);
                builder.setItems(titles, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sensorType.setText(types.get(i));
                    }
                });
                builder.show();
            }
                break;
            case R.id.register_sensor_operation: {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterInterfaceActivity.this);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("Operation Selection");
                final CharSequence[] titles = operation.toArray(new CharSequence[operation.size()]);
                builder.setItems(titles, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sensorOperation.setText(operation.get(i));
                    }
                });
                builder.show();
            }
                break;
            case R.id.register_go:
                sendRegisterOperationCommad();
                break;
            case R.id.register_clear:
                registerDataView.setText("");
                break;
            default:
                break;
        }
    }

    @Override
    public void onEvent(BLEStateEvent.ServiceDiscovered e){
        super.onEvent(e);
    }

    @Override
    public void onEventMainThread(BLEStateEvent.DataAvailable e){
        super.onEventMainThread(e);

        if (e == null)
            return;

        BluetoothGattCharacteristic characteristic = e.characteristic;
        String characteristicUUID = characteristic.getUuid().toString().toUpperCase();
        Log.e("register------",Arrays.toString(e.characteristic.getValue()));

        if (characteristicUUID.equals( BLEAttributes.SENSOR_CHARACTERISTIC)) {
        }
    }
    @Override
    public void onBackPressed() {
        getSupportActionBar().setSubtitle(getString(R.string.app_sensor));
        isShowMenuOption = true;
        // update status MenuOption
        supportInvalidateOptionsMenu();
        super.onBackPressed();
    }
    @Override
    public void onEventMainThread(BLEStateEvent.Connecting e){
        super.onEventMainThread(e);
    }
    @Override
    public void onEventMainThread(BLEStateEvent.Connected e){
        super.onEventMainThread(e);

    }
    @Override
    public  void onEventMainThread(BLEStateEvent.Disconnected e){
        super.onEventMainThread(e);
    }

    @Override
    protected boolean needUartSupport() {
        return true;
    }
    public void onEventMainThread(BLEStateEvent.DataWritenFromClient e) {//get client values

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
    private void receiveMessage(){
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(registerDataView.getText());
        int start = sb.length();
        String value = new String(buffering, Charset.forName("ASCII"));
        if (!value.contains("\n")) {
            value = value + "\n";
        }
        sb.append(value);

        // change text color
        sb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.Deep_Red_Color)), start, sb.length(), 0);
        registerDataView.setText(sb);
        buffering = null;
        registerScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    private void sendRegisterOperationCommad(){
        StringBuffer offsetStr = new StringBuffer(offset.getText());
        if (!offsetStr.substring(0,2).equals("0x")){
            offsetStr.insert(0,"0x");
        }

        StringBuffer bytesStr = new StringBuffer(bytes.getText());
        if (!bytesStr.substring(0,2).equals("0x")){
            bytesStr.insert(0,"0x");
        }

        Integer offsetInt = Integer.parseInt(offsetStr.toString().substring(2),16);
        long byteLong = Long.parseLong(bytesStr.toString().substring(2),16);

        if (!(0x0 <= offsetInt && offsetInt <= 0xff && offsetStr.length() == 4)){
            AlertDialog.Builder alert = new AlertDialog.Builder(RegisterInterfaceActivity.this);
            alert.setTitle("notice");
            alert.setMessage("offset range (00 - ff)");
            alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            alert.show();
            return;
        }

        String operation = sensorOperation.getText().toString();

        if (operation.equals("WRITE")){
            if(!((0x0 < byteLong) && (byteLong < 0xffffffffL) && (bytesStr.length()%2 == 0 && bytesStr.length()/2<=5 && bytesStr.length()/2>= 2))){
                AlertDialog.Builder alert = new AlertDialog.Builder(RegisterInterfaceActivity.this);
                alert.setTitle("notice");
                alert.setMessage("write value range (00 - ffffffff)");
                alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alert.show();
                return;
            }
        }else if(operation.equals("READ")){
            if (!(0x0 < byteLong && byteLong <0xff && bytesStr.length() == 4)){
                AlertDialog.Builder alert = new AlertDialog.Builder(RegisterInterfaceActivity.this);
                alert.setTitle("notice");
                alert.setMessage("read value range (00 - ff)");
                alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alert.show();
                return;
            }
        }

        List<String> fragments = new ArrayList();
        fragments.add("RLI");

        String sensorTypeStr = sensorType.getText().toString();
        if (sensorTypeStr.equals("BAROMETER")){
            fragments.add("P");

        }else {
            fragments.add(sensorTypeStr.substring(0,1));
        }
        fragments.add(operation.substring(0,1));
        fragments.add(offset.getText().toString());
        fragments.add(bytes.getText().toString());

        StringBuffer cmd = new StringBuffer();
        for (int i = 0; i<fragments.size(); i++){
            cmd.append(fragments.get(i));
            cmd.append(" ");
        }
        String command = cmd.toString().trim();
        Log.e(" = = = = ",command);

        sendMessage(command);
    }


    private void sendMessage(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }

        byte[] c = content.getBytes();
        byte[] data = new byte[c.length + 2];
        System.arraycopy(c, 0, data, 0, c.length);
        data[c.length] = 0x0A;
        data[c.length + 1] = 0x0D;
        int chunkCount = data.length / 20 + 1;
        for (int i = 0; i < chunkCount; ++i) {
            byte[] chunk = Arrays.copyOfRange(data, i * 20, Math.min(data.length, i * 20 + 20));
            BLEService.INSTANCE.requestWrite(BLEAttributes.SENSOR_UUID, BLEAttributes.SENSOR_CHARACTERISTIC, chunk);
        }
    }
}
