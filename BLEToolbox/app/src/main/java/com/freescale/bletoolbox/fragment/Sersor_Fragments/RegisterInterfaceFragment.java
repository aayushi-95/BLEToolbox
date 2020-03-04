package com.freescale.bletoolbox.fragment.Sersor_Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.activity.SenorActivity;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.fragment.BaseFragment;
import com.freescale.bletoolbox.fragment.IActivityToFragment;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nxf42542 on 2018/8/3.
 */

public class RegisterInterfaceFragment extends BaseFragment implements View.OnClickListener, View.OnTouchListener,IActivityToFragment {
    @Bind(R.id.register_scrollView)
    ScrollView registerScrollView;
    @Bind(R.id.register_interface_data_view)
    TextView registerDataView;

    @Bind(R.id.register_offset)
    EditText offset;
    @Bind(R.id.register_bytes)
    EditText bytes;

    @Bind(R.id.register_sensor_type)
    Button sensorType;
    @Bind(R.id.register_sensor_operation)
    Button sensorOperation;
    @Bind(R.id.register_go)
    Button go;
    @Bind(R.id.register_clear)
    Button clear;

    private byte[] buffering;
    private List<String> types;
    private List<String> operation;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_register_interface, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        initializeData();
        initializeView();
        return view;
    }

    private void initializeData() {
        types = Arrays.asList(getResources().getStringArray(R.array.sensor_register_type));
        operation = Arrays.asList(getResources().getStringArray(R.array.sensor_register_operation));
    }

    private void initializeView() {
        sensorType.setOnClickListener(this);
        sensorOperation.setOnClickListener(this);
        go.setOnClickListener(this);
        clear.setOnClickListener(this);


        sensorType.setText((CharSequence) types.get(0));
        sensorOperation.setText(operation.get(0));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void upDateUI(BLEStateEvent.DataAvailable e) {
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void resetDefault() {
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.register_sensor_type: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
    public boolean onTouch(View view, MotionEvent motionEvent) {

        return false;
    }

    private void receiveMessage() {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(registerDataView.getText());
        int start = sb.length();
        String value = new String(buffering, Charset.forName("ASCII"));
        if (!value.contains("\n")) {
            value = value + "\n";
        }
        sb.append(value);

        // change text color
        sb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.Deep_Red_Color)), start, sb.length(), 0);
        registerDataView.setText(sb);
        buffering = null;
        registerScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    private void sendRegisterOperationCommad() {
        StringBuffer offsetStr = new StringBuffer(offset.getText());
        if (offsetStr.length() < 2 || !offsetStr.substring(0, 2).equals("0x")) {
            offsetStr.insert(0, "0x");
        }

        StringBuffer bytesStr = new StringBuffer(bytes.getText());
        if (bytesStr.length() < 2 || !bytesStr.substring(0, 2).equals("0x")) {
            bytesStr.insert(0, "0x");
        }

        if (!isNumberRex(offset.getText().toString())|| !isNumberRex(bytes.getText().toString())){
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("notice");
            alert.setMessage("The input is invalid data");
            alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            alert.show();
            return;
        }
        Integer offsetInt = Integer.parseInt(offsetStr.toString().substring(2), 16);
        long byteLong = Long.parseLong(bytesStr.toString().substring(2), 16);

        if (!(0x0 <= offsetInt && offsetInt <= 0xff)) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
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

        if (operation.equals("WRITE")) {
            if (!((0x0 < byteLong) && (byteLong < 0xffffffffL) && (bytesStr.length() % 2 == 0 && bytesStr.length() / 2 <= 5 && bytesStr.length() / 2 >= 2))) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
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
        } else if (operation.equals("READ")) {
            if (!(0x0 < byteLong && byteLong < 0xff)) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
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
        if (sensorTypeStr.equals("BAROMETER")) {
            fragments.add("P");

        } else {
            fragments.add(sensorTypeStr.substring(0, 1));
        }
        fragments.add(operation.substring(0, 1));
        fragments.add(offsetStr.toString().substring(2));
        fragments.add(bytesStr.toString().substring(2));

        StringBuffer cmd = new StringBuffer();
        for (int i = 0; i < fragments.size(); i++) {
            cmd.append(fragments.get(i));
            cmd.append(" ");
        }
        String command = cmd.toString().trim();
        Log.e(" = = = = ", command);

        ((SenorActivity) getActivity()).sendMessage(command);
    }


    private static boolean isNumberRex(String str){
        String octValidate = "\\d+";
        String HexValidate = "(?i)[0-9a-f]+";
        boolean isOctNumber = str.matches(octValidate);
        boolean isHexNumber = str.matches(HexValidate);
        return isOctNumber || isHexNumber;
    }
}
