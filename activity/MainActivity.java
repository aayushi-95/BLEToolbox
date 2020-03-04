/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.model.BLEApp;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.utility.SdkUtils;
import com.freescale.bletoolbox.view.AboutDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    private static final int REQUEST_PERMISSION_CODE = 2710;

    @Bind(R.id.main_application)
    RecyclerView mRecyclerView;

    private List<BLEApp> apps;
    private BLEApp m_selectedBLEApp;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_main) {
            AboutDialog.newInstance(this).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        initApps();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(new BLEAppAdapter());
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(5));
    }

    private void initApps() {
        apps = new ArrayList<>();
        //apps.add(new BLEApp(BLEApp.Type.BEACON, R.drawable.ic_beacon, R.string.app_beacon, true));
        apps.add(new BLEApp(BLEApp.Type.BLOOD_PRESSURE, R.drawable.ic_blood_pressure, R.string.app_blood_pressure, true));
        apps.add(new BLEApp(BLEApp.Type.CYCLING_SPEED, R.drawable.ic_cycling_speed_and_cadence, R.string.app_cycling_speed, true));
//        apps.add(new BLEApp(BLEApp.Type.FRDM_DEMO, R.drawable.ic_frdm, R.string.app_frdm_demo, true));
        apps.add(new BLEApp(BLEApp.Type.GLUCOSE, R.drawable.ic_glucose, R.string.app_glucose, true));
        apps.add(new BLEApp(BLEApp.Type.THERMOMETER, R.drawable.ic_health_thermometer, R.string.app_thermometer, true));
        apps.add(new BLEApp(BLEApp.Type.HEART_RATE, R.drawable.ic_heart_rate, R.string.app_heart_rate, true));
        apps.add(new BLEApp(BLEApp.Type.OTAP, R.drawable.ic_otap, R.string.app_otap, true));
        apps.add(new BLEApp(BLEApp.Type.PROXIMITY, R.drawable.ic_proximity, R.string.app_proximity, true));
        apps.add(new BLEApp(BLEApp.Type.IPconfig, R.drawable.ic_proximity,R.string.app_ipconfig,true));
        apps.add(new BLEApp(BLEApp.Type.QPP, R.drawable.ic_frdm, R.string.app_qpp, true));
        apps.add(new BLEApp(BLEApp.Type.RUNNING_SPEED, R.drawable.ic_running_speed_and_cadence, R.string.app_running_speed, true));
        apps.add(new BLEApp(BLEApp.Type.SENSOR, R.drawable.ic_sensor, R.string.sensor_shell, true));
        apps.add(new BLEApp(BLEApp.Type.SHELL, R.drawable.shell, R.string.app_shell, true));
        apps.add(new BLEApp(BLEApp.Type.WUART, R.drawable.ic_wireless_uart, R.string.app_w_console_uart, true));
        apps.add(new BLEApp(BLEApp.Type.ZIGBEE, R.drawable.ic_zigbee, R.string.app_zigbee, true));
    }

    private final View.OnClickListener mAppClickHandler = new View.OnClickListener() {

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
            if (apps == null) {
                return;
            }
            int itemPosition = mRecyclerView.getChildAdapterPosition(v);
            if (itemPosition < 0 || itemPosition >= apps.size()) {
                return;
            }
            m_selectedBLEApp = apps.get(itemPosition);
            if (!m_selectedBLEApp.available) {
                m_selectedBLEApp = null;
                return;
            }

            // if OS >= 6 -> need ask permission access device's location
            if (SdkUtils.hasMarshmallow()) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    // handle "Never ask again"
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        showMessageOKCancel(getString(R.string.grant_permission),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION};
                                        requestPermissions(permissions, REQUEST_PERMISSION_CODE);
                                    }
                                });
                        return;
                    }

                    String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION};
                    requestPermissions(permissions, REQUEST_PERMISSION_CODE);
                } else {
                    gotoBleApplication();
                }
            } else {
                gotoBleApplication();
            }
        }
    };

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_PERMISSION_CODE == requestCode) {
            boolean grantedAccessCoarseLocation = true;
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                if (Manifest.permission.ACCESS_COARSE_LOCATION.equals(permission)) {
                    grantedAccessCoarseLocation = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                }
            }
            //
            if (grantedAccessCoarseLocation) {
                gotoBleApplication();
            } else {
//                Toast.makeText(this, "Please grant permissions", Toast.LENGTH_SHORT).show();
                m_selectedBLEApp = null;
            }
        }
    }

    private void gotoBleApplication() {
        // based on type, we should start corresponding activities here
        switch (m_selectedBLEApp.type) {
           /* case BLEApp.Type.BEACON:
                Intent intent = new Intent(MainActivity.this, BeaconActivity.class);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_TITLE, R.string.app_beacon);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_APP, BLEApp.Type.BEACON);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;*/
            case BLEApp.Type.CYCLING_SPEED:
               Intent intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_TITLE, R.string.app_cycling_speed);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_SERVICE, BLEAttributes.CYCLING_SPEED);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_APP, BLEApp.Type.CYCLING_SPEED);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
                break;
            case BLEApp.Type.HEART_RATE:
                intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_TITLE, R.string.app_heart_rate);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_SERVICE, BLEAttributes.HEART_RATE_SERVICE);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_APP, BLEApp.Type.HEART_RATE);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
                break;
            case BLEApp.Type.RUNNING_SPEED:
                intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_TITLE, R.string.app_running_speed);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_SERVICE, BLEAttributes.RUNNING_SPEED);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_APP, BLEApp.Type.RUNNING_SPEED);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
                break;
            case BLEApp.Type.THERMOMETER:
                intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_TITLE, R.string.app_thermometer);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_SERVICE, BLEAttributes.THERMOMETER);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_APP, BLEApp.Type.THERMOMETER);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
                break;
            case BLEApp.Type.BLOOD_PRESSURE:
                intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_TITLE, R.string.app_blood_pressure);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_SERVICE, BLEAttributes.BLOOD_PRESSURE_SERVICE);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_APP, BLEApp.Type.BLOOD_PRESSURE);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
                break;

            case BLEApp.Type.IPconfig:
                intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_TITLE, R.string.app_ipconfig);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_SERVICE, BLEAttributes.LINK_LOSS_SERVICE);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_APP, BLEApp.Type.IPconfig);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
                break;
            case BLEApp.Type.PROXIMITY:
                intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_TITLE, R.string.app_proximity);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_SERVICE, BLEAttributes.LINK_LOSS_SERVICE);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_APP, BLEApp.Type.PROXIMITY);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
                break;
            case BLEApp.Type.GLUCOSE:
                intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_TITLE, R.string.app_glucose);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_SERVICE, BLEAttributes.GLUCOSE_SERVICE);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_APP, BLEApp.Type.GLUCOSE);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
                break;
            case BLEApp.Type.WUART:
                intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_TITLE, R.string.app_w_console_uart);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_SERVICE, BLEAttributes.WUART);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_APP, BLEApp.Type.WUART);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
                break;
            case BLEApp.Type.OTAP:
                intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_TITLE, R.string.app_otap);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_SERVICE, BLEAttributes.OTAP);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_APP, BLEApp.Type.OTAP);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
                break;
            case BLEApp.Type.FRDM_DEMO:
                intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_TITLE, R.string.app_frdm_demo);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_SERVICE, BLEAttributes.FRDM);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_APP, BLEApp.Type.FRDM_DEMO);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
                break;
            case BLEApp.Type.SHELL:
                intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_TITLE, R.string.app_shell);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_SERVICE, BLEAttributes.SHELL);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_APP, BLEApp.Type.SHELL);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
                break;
            case BLEApp.Type.QPP:
                intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_TITLE, R.string.app_qpp);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_SERVICE, BLEAttributes.QPP_UUID);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_APP, BLEApp.Type.QPP);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
                break;
            case BLEApp.Type.SENSOR:
                intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_TITLE, R.string.app_sensor);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_SERVICE, BLEAttributes.SENSER);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_APP, BLEApp.Type.SENSOR);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
                break;
            case BLEApp.Type.ZIGBEE:
                intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_TITLE, R.string.app_zigbee);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_SERVICE, BLEAttributes.ZIGBEE);
                intent.putExtra(DeviceScanActivity.INTENT_KEY_APP, BLEApp.Type.ZIGBEE);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
                break;
            default:
                break;
        }
        m_selectedBLEApp = null;
    }

    static class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private final int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.top = space;
            outRect.right = space;
            outRect.bottom = space;
        }
    }

    static class BLEAppHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.ble_app_title)
        Button title;

        public BLEAppHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class BLEAppAdapter extends RecyclerView.Adapter<BLEAppHolder> {

        @Override
        public int getItemCount() {
            return apps == null ? 0 : apps.size();
        }

        @Override
        public BLEAppHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.ble_app_item, viewGroup, false);
            view.setOnClickListener(mAppClickHandler);
            return new BLEAppHolder(view);
        }

        @Override
        public void onBindViewHolder(BLEAppHolder bleAppHolder, int i) {
            BLEApp app = apps.get(i);
            bleAppHolder.title.setText(app.title);
            Drawable d = ResourcesCompat.getDrawable(getResources(), app.icon, getTheme());
            if (app.available) {
                d.clearColorFilter();
            } else {
                d.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            }
            bleAppHolder.title.setCompoundDrawablesWithIntrinsicBounds(null, d, null, null);
            bleAppHolder.title.setTransformationMethod(null);
        }
    }
}
