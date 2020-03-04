/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.activity;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.fragment.KW40_Fragments.*;
import com.freescale.bletoolbox.fragment.IActivityToFragment;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.utility.BLEConverter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FRMDActivity extends BaseServiceActivity {
    @Bind(R.id.list_frdm_item)
    RecyclerView recyclerFRDM;

    @Bind(R.id.framelayout)
    FrameLayout framelayout;

    @Bind(R.id.lnProgressBar)
    LinearLayout progressBar;

    private FragmentTransaction fragmentTransaction;
    private static final String INTENT_KEY_ADDRESS = "intent.key.address";

    private BLEAppAdapter bleAppAdapter;
    private List<BluetoothGattService> listService;
    protected String mDeviceAddress;
    private LinearLayoutManager mLinearLayoutManager;
    private Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frdm);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        managerTitleToolBar(getString(R.string.app_frdm_demo));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        recyclerFRDM.setVisibility(View.GONE);
        recyclerFRDM.setHasFixedSize(true);
        recyclerFRDM.addItemDecoration(new CustomHeightItemDecoration(this, 1));
        mDeviceAddress = getIntent().getStringExtra(INTENT_KEY_ADDRESS);
        if (TextUtils.isEmpty(mDeviceAddress)) {
            throw new NullPointerException("Invalid Bluetooth MAC Address");
        }
        bleAppAdapter = new BLEAppAdapter();

        framelayout.setVisibility(View.GONE);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
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

    // set Title for ToolBar
    public void managerTitleToolBar(String strTitle) {
        getSupportActionBar().setSubtitle(strTitle);
    }

    @Override
    public void onEventMainThread(BLEStateEvent.Disconnected e) {
        super.onEventMainThread(e);
        if (framelayout.getVisibility() == View.VISIBLE) {
            framelayout.setVisibility(View.GONE);

            managerTitleToolBar(getString(R.string.app_frdm_demo));
            isShowMenuOption = true;
            // update status MenuOption
            supportInvalidateOptionsMenu();
        }
        if (listService != null && bleAppAdapter != null) {
            listService.clear();
            bleAppAdapter.setData(listService);
        }
    }

    @Override
    public void onEventMainThread(BLEStateEvent.DataAvailable e) {
        super.onEventMainThread(e);
        if (e == null) return;
        BluetoothGattCharacteristic gattCharacteristic = e.characteristic;
        final String charaterUuid = gattCharacteristic.getUuid().toString();
        if (BLEAttributes.LED_CHARACTERISTIC_CONTROL.toUpperCase().equals(charaterUuid.toUpperCase())) {
            ((IActivityToFragment) fragment).upDateUI(e);
        } else if (BLEAttributes.INPUT_CHARACTERISTIC.toUpperCase().equals(charaterUuid.toUpperCase())) {
            ((IActivityToFragment) fragment).upDateUI(e);
        } else if (BLEAttributes.BUZZER_CHARACTERISTIC.toUpperCase().equals(charaterUuid.toUpperCase())) {
            ((IActivityToFragment) fragment).upDateUI(e);
        } else if (BLEAttributes.POTENTIONMETER_CHARACTERISTIC.toUpperCase().equals(charaterUuid.toUpperCase())) {
            ((IActivityToFragment) fragment).upDateUI(e);
        } else if (BLEAttributes.ACCELEROMETER_CHARACTERISTIC_SCALE.toUpperCase().equals(charaterUuid.toUpperCase())) {
            ((IActivityToFragment) fragment).upDateUI(e);
        } else if (BLEAttributes.ACCELEROMETER_CHARACTERISTIC_READING.toUpperCase().equals(charaterUuid.toUpperCase())) {
            ((IActivityToFragment) fragment).upDateUI(e);
        } else if (BLEAttributes.COMPASS_CHARACTERISTIC.toUpperCase().equals(charaterUuid.toUpperCase())) {
            ((IActivityToFragment) fragment).upDateUI(e);
        } else if (BLEAttributes.CONTROLLER_CHARACTERISTIC_COMMAND.toUpperCase().equals(charaterUuid.toUpperCase())) {
            ((IActivityToFragment) fragment).upDateUI(e);
        } else if (BLEAttributes.CONTROLLER_CHARACTERISTIC_CONFIGURATION.toUpperCase().equals(charaterUuid.toUpperCase())) {
            ((IActivityToFragment) fragment).upDateUI(e);
        } else {
            int assignedNumber = BLEConverter.getAssignedNumber(e.characteristic.getUuid());
            if (BLEAttributes.TEMPERATURE_CHARACTERISTIC == assignedNumber) {
                ((IActivityToFragment) fragment).upDateUI(e);
            }
        }
    }

    @Override
    public void onEventMainThread(BLEStateEvent.Connected e) {
        super.onEventMainThread(e);
    }

    @Override
    public void onEvent(BLEStateEvent.ServiceDiscovered e) {
        super.onEvent(e);
    }

    public void onEventMainThread(BLEStateEvent.DataAvailableFRMD e) {
        listService = new ArrayList<BluetoothGattService>();

        for (BluetoothGattService item : e.characteristic.getServices()) {
            if (item.getUuid().toString().toUpperCase().startsWith("02FF56")) {
                listService.add(item);
            }
        }

        if (null != listService && listService.size() > 0) {
            progressBar.setVisibility(View.GONE);
            recyclerFRDM.setVisibility(View.VISIBLE);
            recyclerFRDM.setAdapter(bleAppAdapter);
            recyclerFRDM.setHasFixedSize(true);
            recyclerFRDM.setLayoutManager(mLinearLayoutManager);
            bleAppAdapter.setData(listService);
        }
    }

    @Override
    public void onBackPressed() {
        managerTitleToolBar(getString(R.string.app_frdm_demo));
        isShowMenuOption = true;
        // update status MenuOption
        supportInvalidateOptionsMenu();
        if (framelayout.getVisibility() == View.VISIBLE) {
            framelayout.setVisibility(View.GONE);
            getFragmentManager().popBackStackImmediate();
            ((IActivityToFragment) fragment).resetDefault();
        } else {
            super.onBackPressed();
        }
    }

    private final View.OnClickListener mAppClickHandler = new View.OnClickListener() {

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
            int itemPosition = recyclerFRDM.getChildAdapterPosition(v);
            BluetoothGattService item = listService.get(itemPosition);
            String uuid = item.getUuid().toString();
            Intent intent;
            String subTitle = "";

            if (uuid.toUpperCase().startsWith("02FF5600")) {
                fragment = new LedFragment();
                subTitle = getString(R.string.frdm_led);
            } else if (uuid.toUpperCase().startsWith("02FF5601")) {
                fragment = new InputFragment();
                subTitle = getString(R.string.frdm_switch);
            } else if (uuid.toUpperCase().startsWith("02FF5602")) {
                fragment = new BuzzerFragment();
                subTitle = getString(R.string.frdm_buzzer);
            } else if (uuid.toUpperCase().startsWith("02FF5603")) {
                fragment = new TemperatureFragment();
                subTitle = getString(R.string.frdm_temperature);
            } else if (uuid.toUpperCase().startsWith("02FF5604")) {
                fragment = new PotentiometerFragment();
                subTitle = getString(R.string.frdm_potentiometer);
            } else if (uuid.toUpperCase().startsWith("02FF5605")) {
                fragment = new AccelerometerFragment();
                subTitle = getString(R.string.frdm_accelerometer);
            } else if (uuid.toUpperCase().startsWith("02FF5606")) {
                fragment = new ECompassFragment();
                subTitle = getString(R.string.frdm_e_compass);
            } else if (uuid.toUpperCase().startsWith("02FF5607")) {
                fragment = new ControllerFragment();
                subTitle = getString(R.string.frdm_remote_control);
            }
            if (fragment != null) {
                managerTitleToolBar(subTitle);
                framelayout.setVisibility(View.VISIBLE);
                isShowMenuOption = false;
                // update status MenuOption
                supportInvalidateOptionsMenu();

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }
    };

    class BLEAppAdapter extends RecyclerView.Adapter<BLEAppAdapter.BLEAppHolder> {
        List<BluetoothGattService> listService;

        public BLEAppAdapter() {
        }

        public void setData(List<BluetoothGattService> _listService) {
            listService = _listService;
            notifyDataSetChanged();
        }

        public class BLEAppHolder extends RecyclerView.ViewHolder {
            public TextView txtName;

            public BLEAppHolder(View view) {
                super(view);
                txtName = (TextView) view.findViewById(R.id.txtName);
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
        public BLEAppHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.list_sensors_item, viewGroup, false);
            view.setOnClickListener(mAppClickHandler);
            return new BLEAppHolder(view);
        }

        @Override
        public void onBindViewHolder(BLEAppHolder bleAppHolder, int i) {
            BluetoothGattService app = listService.get(i);
            String uuid = app.getUuid().toString();
            String strName = "";
            if (uuid.toUpperCase().startsWith("02FF5600")) {
                strName = getString(R.string.frdm_led);
            } else if (uuid.toUpperCase().startsWith("02FF5601")) {
                strName = getString(R.string.frdm_switch);
            } else if (uuid.toUpperCase().startsWith("02FF5602")) {
                strName = getString(R.string.frdm_buzzer);
            } else if (uuid.toUpperCase().startsWith("02FF5603")) {
                strName = getString(R.string.frdm_chip_temperature);
            } else if (uuid.toUpperCase().startsWith("02FF5604")) {
                strName = getString(R.string.frdm_potentiometer);
            } else if (uuid.toUpperCase().startsWith("02FF5605")) {
                strName = getString(R.string.frdm_accelerometer);
            } else if (uuid.toUpperCase().startsWith("02FF5606")) {
                strName = getString(R.string.frdm_e_compass);
            } else if (uuid.toUpperCase().startsWith("02FF5607")) {
                strName = getString(R.string.frdm_remote_control);
            }
            if (!strName.isEmpty()) {
                bleAppHolder.txtName.setText(strName);
            }
        }
    }
}