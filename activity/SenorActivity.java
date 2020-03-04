package com.freescale.bletoolbox.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.fragment.BaseFragment;
import com.freescale.bletoolbox.fragment.IActivityToFragment;
import com.freescale.bletoolbox.fragment.Sersor_Fragments.RegisterInterfaceFragment;
import com.freescale.bletoolbox.fragment.Sersor_Fragments.SensorAccelerometerFragment;
import com.freescale.bletoolbox.fragment.Sersor_Fragments.SensorDataFragment;
import com.freescale.bletoolbox.fragment.Sersor_Fragments.SensorECompassFragment;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;

/**
 * Created by nxf42542 on 2018/7/2.
 */

public class SenorActivity extends BaseServiceActivity implements View.OnClickListener {

    @Bind(R.id.list_sensor_item)
    RecyclerView recyclerSENSOR;

    @Bind(R.id.framelayout)
    FrameLayout framelayout;

    @Bind(R.id.lnProgressBar)
    LinearLayout progressBar;

    @Bind(R.id.status_bar)
    View bottom_status_bar;

    @Bind(R.id.register_interface)
    Button registerBtn;

    private String command;

    public static final int MTU_WUART = 247;
    private int currentMTU = 0;
    private byte[] buffering;

    private static final String INTENT_KEY_ADDRESS = "intent.key.address";

    private  List <String> mList;
    private SenorActivity.SensorAdapter sensorAdapter;
    protected String mDeviceName;
    private LinearLayoutManager mLinearLayoutManager;
    private BaseFragment fragment = null;
    private boolean isDataMode = false;
    private boolean ready = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(getString(R.string.app_sensor));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        registerBtn.setOnClickListener(this);

        sensorAdapter = new SenorActivity.SensorAdapter();

        framelayout.setVisibility(View.GONE);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        progressBar.setVisibility(View.GONE);
        recyclerSENSOR.setVisibility(View.VISIBLE);
        recyclerSENSOR.addItemDecoration(new CustomHeightItemDecoration(this, 1));

        recyclerSENSOR.setAdapter(sensorAdapter);
        recyclerSENSOR.setHasFixedSize(true);
        recyclerSENSOR.setLayoutManager(mLinearLayoutManager);

        mList = Arrays.asList(getResources().getStringArray(R.array.sensor_type_list));
        sensorAdapter.setData(mList);
    }

    static class CustomHeightItemDecoration extends RecyclerView.ItemDecoration {

        private final int mHeight;
        private final Drawable mDivider;

        public CustomHeightItemDecoration(@NonNull Context context, int mVerticalSpaceHeight) {
            this.mHeight = mVerticalSpaceHeight;
            this.mDivider = ContextCompat.getDrawable(context, R.drawable.list_divider);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                outRect.bottom = mHeight;
            }
        }

        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
    private void placeholderRragmentSetting(){
        recyclerSENSOR.setVisibility(View.GONE);
        bottom_status_bar.setVisibility(View.GONE);
        registerBtn.setVisibility(View.GONE);
        framelayout.setVisibility(View.VISIBLE);
        isShowMenuOption = false;
        // update status MenuOption
        supportInvalidateOptionsMenu();
    }
    @Override
    public void onClick(View view){
        if (!ready)
            return;

        switch (view.getId()){
            case R.id.register_interface: {
                placeholderRragmentSetting();

                fragment = new RegisterInterfaceFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.framelayout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
                break;
            case R.id.sensor_data_mode: {
                isDataMode = true;
                View cell = (View) view.getParent();
                int itemPosition = recyclerSENSOR.getChildAdapterPosition(cell);
                placeholderRragmentSetting();
                sendCommandByAdapterPosition(itemPosition);

                fragment = new SensorDataFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
                break;
            case R.id.sensor_graphics_mode: {
                isDataMode = false;
                View cell = (View) view.getParent();
                int itemPosition = recyclerSENSOR.getChildAdapterPosition(cell);
                placeholderRragmentSetting();
                sendCommandByAdapterPosition(itemPosition);

                String sensorType = mList.get(itemPosition);
                if (sensorType.equals("Accelerometer") || sensorType.equals("Magnetometer") || sensorType.equals("Gyroscope") || sensorType.equals("Barometer"))
                {
                    if (!(fragment instanceof SensorAccelerometerFragment) || fragment == null){
                        fragment = new SensorAccelerometerFragment();
                    }
                    ((SensorAccelerometerFragment)fragment).subTitle = sensorType;
                }
                else if(sensorType.equals("eCompass"))
                {
                    if (!(fragment instanceof SensorECompassFragment) || fragment == null){
                        fragment = new SensorECompassFragment();
                    }
                }
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
                break;
            default:
                break;
        }
    }
    @DebugLog
    public void onEvent(BLEStateEvent.MTUUpdated mtuUpdated) {
        Log.d("","mtuUpdated = " + mtuUpdated.mtuSize + " success " + mtuUpdated.success);
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

        ready = true;

        if (currentMTU == 0) {
            BLEService.INSTANCE.requestMTU(MTU_WUART);
            currentMTU = MTU_WUART;
        }


        BluetoothGattCharacteristic characteristic = e.characteristic;
        String characteristicUUID = characteristic.getUuid().toString().toUpperCase();

        if (characteristicUUID.equals( BLEAttributes.SENSOR_CHARACTERISTIC)) {
        }
    }
    @Override
    public void onBackPressed() {
        getSupportActionBar().setSubtitle(getString(R.string.app_sensor));
        isShowMenuOption = true;
        // update status MenuOption
        supportInvalidateOptionsMenu();
        if (framelayout.getVisibility() == View.VISIBLE) {
            framelayout.setVisibility(View.GONE);
            bottom_status_bar.setVisibility(View.VISIBLE);
            recyclerSENSOR.setVisibility(View.VISIBLE);
            registerBtn.setVisibility(View.VISIBLE);
            getFragmentManager().popBackStackImmediate();
            ((IActivityToFragment) fragment).resetDefault();
            fragment = null;
            sendMessage("CMD STANDBY");
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onEventMainThread(BLEStateEvent.Connecting e){
        super.onEventMainThread(e);

    }
    @Override
    public void onEventMainThread(BLEStateEvent.Connected e){
        super.onEventMainThread(e);
        // request mtu start

    }
    @Override
    public  void onEventMainThread(BLEStateEvent.Disconnected e){
        super.onEventMainThread(e);
        ready = false;
    }

    @Override
    protected boolean needUartSupport() {
        return true;
    }
    public void onEventMainThread(BLEStateEvent.DataWritenFromClient e) {//get client values
        if (fragment != null){
            fragment.onEventMainThread(e);
        }
    }


    class SensorAdapter extends RecyclerView.Adapter<SenorActivity.SensorAdapter.SensorAdapterHoler> {
        List<String> listService;

        public SensorAdapter() {
        }

        public void setData(List<String> _listService) {
            listService = _listService;
            notifyDataSetChanged();
        }

        public class SensorAdapterHoler extends RecyclerView.ViewHolder {
            public TextView txtName;
            public Button dataBtn;
            public Button graphicsBtn;
            public SensorAdapterHoler(View view) {
                super(view);
                txtName = (TextView) view.findViewById(R.id.txtName);
                dataBtn = (Button)view.findViewById(R.id.sensor_data_mode);
                graphicsBtn = (Button)view.findViewById(R.id.sensor_graphics_mode);
                dataBtn.setOnClickListener(SenorActivity.this);
                graphicsBtn.setOnClickListener(SenorActivity.this);
            }
        }

        @Override
        public int getItemCount() {
            if (null != listService) {
                return listService.size();
            } else {
                return 0;
            }
        }

        @Override
        public SenorActivity.SensorAdapter.SensorAdapterHoler onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.list_sensors_item, viewGroup, false);
            return new SensorAdapterHoler(view);
        }

        @Override
        public void onBindViewHolder(SenorActivity.SensorAdapter.SensorAdapterHoler bleAppHolder, int i) {
            String uuid = listService.get(i);

            if (!uuid.isEmpty()) {
                bleAppHolder.txtName.setText(uuid);
            }
            if (i > 4){
                bleAppHolder.graphicsBtn.setVisibility(View.INVISIBLE);
            }else{
                bleAppHolder.graphicsBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    public void dataModeDataType(boolean normal){
        if (normal){
            sendMessage("CMD NORMAL");
        }else{
            sendMessage("CMD RAW");
        }
    }


     private void sendCommandByAdapterPosition(int itemPosition){

        String sensorType = mList.get(itemPosition);
        if (sensorType.equals("Accelerometer")) {
            command = "CMD ACCELEROMETER";
        } else if (sensorType.equals("Magnetometer")) {
            command = "CMD MAGNETOMETER";
        } else if (sensorType.equals("Gyroscope")) {
            command = "CMD GYROSCOPE";
        } else if (sensorType.equals("eCompass")) {
            command = "CMD ECOMPASS";
        } else if (sensorType.equals("Barometer")) {
            command = "CMD BAROMETER";
        } else if (sensorType.equals("Altimeter")) {
            command = "CMD ALTIMETER";
        } else if (sensorType.equals("Pedometer")) {
            command = "CMD PEDOMETER";
        } else if (sensorType.equals("Freefall")) {
            command = "CMD FREEFALL";
        }else if (sensorType.equals("Orientation")) {
            command = "CMD ORIENTATION";
        }
        if (!isDataMode && sensorType.equals("eCompass")){
            sendMessage("CMD STREAM");
        }else{
            sendMessage("CMD NORMAL");
        }
        sendMessage(command);

        getSupportActionBar().setSubtitle(sensorType);
    }

    public void sendMessage(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        Log.e("Sensor command ",content);
        byte[] c = content.getBytes();
        byte[] data = new byte[c.length + 2];
        System.arraycopy(c, 0, data, 0, c.length);
        data[c.length] = 0x0A;
        data[c.length + 1] = 0x0D;
        int chunkCount = data.length / 20 + 1;
        for (int i = 0; i < chunkCount; i++) {
            final byte[] chunk = Arrays.copyOfRange(data, i * 20, Math.min(data.length, i * 20 + 20));
            boolean success =  BLEService.INSTANCE.requestWrite(BLEAttributes.SENSOR_UUID.toLowerCase(), BLEAttributes.SENSOR_CHARACTERISTIC.toLowerCase(), chunk);
            if (success)
                Log.e("Sensor activity ","send commad successfully");
        }
    }
}
