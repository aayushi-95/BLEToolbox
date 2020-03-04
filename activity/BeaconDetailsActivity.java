/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.model.BeaconMessage;
import com.freescale.bletoolbox.model.FSLBeacon;
import com.freescale.bletoolbox.utility.SdkUtils;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class BeaconDetailsActivity extends BaseActivity {

    public static final String INTENT_BEACON = "intent.key.beacon";

    @Bind(R.id.beacon_action)
    Spinner mActionSpinner;

    @Bind(R.id.beacon_message_wrapper)
    View mMessageWrapper;

    @Bind(R.id.beacon_message_content)
    EditText mMessage;

    @Bind(R.id.beacon_message_error)
    TextView mMessageCount;

    @Bind(R.id.beacon_manufacture)
    TextView mManufacture;

    @Bind(R.id.beacon_uuid)
    TextView mUuid;

    @Bind(R.id.beacon_data_a)
    TextView mDataA;

    @Bind(R.id.beacon_data_b)
    TextView mDataB;

    @Bind(R.id.beacon_data_c)
    TextView mDataC;

    @Bind(R.id.beacon_rssi)
    TextView mRssi;

    private FSLBeacon mBeacon;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_beacon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_done) {
            if (TextUtils.isEmpty(mMessage.getText().toString()) &&
                    mActionSpinner.getSelectedItemPosition() == BeaconMessage.ACTION_SHOW_MESSAGE) {
                new MaterialDialog.Builder(this).title(R.string.error_title)
                        .titleColor(Color.RED)
                        .content("Message can not be empty.")
                        .positiveText(android.R.string.ok).show();
                return true;
            }

            String uuid = mBeacon.getUuid().toString();
            Realm realm = Realm.getDefaultInstance();
            RealmQuery<BeaconMessage> query = realm.where(BeaconMessage.class);
            query.equalTo("uuid", uuid);
            query.equalTo("dataA", mBeacon.getDataA());
            query.equalTo("dataB", mBeacon.getDataB());
            query.equalTo("dataC", mBeacon.getDataC());
            RealmResults<BeaconMessage> result = query.findAll();

            realm.beginTransaction();
            BeaconMessage message;
            if (result.isEmpty()) {
                message = realm.createObject(BeaconMessage.class);
                message.setUuid(uuid);
                message.setDataA(mBeacon.getDataA());
                message.setDataB(mBeacon.getDataB());
                message.setDataC(mBeacon.getDataC());
            } else {
                message = result.get(0);
            }
            message.setMessage(mMessage.getText().toString());
            message.setActionType(mActionSpinner.getSelectedItemPosition());
            realm.commitTransaction();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_details);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(R.string.app_beacon);

        mBeacon = getIntent().getParcelableExtra(INTENT_BEACON);
        if (mBeacon == null) {
            finish();
            return;
        }

        String originId = mBeacon.getManufactureId() == 0x0025 ?
                "NXP" : ("0x" + String.format("%04X", mBeacon.getManufactureId() & 0x0FFFF));
        String strManufacturer = "Manufacturer ID: ";
        SpannableString manufactureId = new SpannableString(strManufacturer + originId);
        manufactureId.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)),
                0, strManufacturer.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        manufactureId.setSpan(new StyleSpan(Typeface.BOLD),
                0, strManufacturer.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        manufactureId.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(this, R.color.deep_red)),
                strManufacturer.length(), manufactureId.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mManufacture.setText(manufactureId);

        String strUUID = "UUID: ";
        SpannableString uuid = new SpannableString(
                strUUID + mBeacon.getUuid().toString().toUpperCase(Locale.getDefault()));
        uuid.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)),
                0, strUUID.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        uuid.setSpan(new StyleSpan(Typeface.BOLD),
                0, strUUID.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        uuid.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.deep_red)),
                strUUID.length(), uuid.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mUuid.setText(uuid);

        String strRSSI = "RSSI: ";
        SpannableString rssi = new SpannableString(strRSSI + mBeacon.getRssi() + " dBm");
        rssi.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)),
                0, strRSSI.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        rssi.setSpan(new StyleSpan(Typeface.BOLD),
                0, strRSSI.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        rssi.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.deep_red)),
                strRSSI.length(), rssi.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mRssi.setText(rssi);

        String strA = "A: ";
        SpannableString dataA = new SpannableString(strA + mBeacon.getDataA());
        dataA.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)),
                0, strA.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        dataA.setSpan(new StyleSpan(Typeface.BOLD),
                0, strA.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        dataA.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.deep_red)),
                strA.length(), dataA.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mDataA.setText(dataA);

        String strB = "B: ";
        SpannableString dataB = new SpannableString(strB + mBeacon.getDataB());
        dataB.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)),
                0, strB.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        dataB.setSpan(new StyleSpan(Typeface.BOLD),
                0, strB.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        dataB.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.deep_red)),
                strB.length(), dataB.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mDataB.setText(dataB);

        String strC = "C: ";
        SpannableString dataC = new SpannableString(strC + mBeacon.getDataC());
        dataC.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)),
                0, strC.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        dataC.setSpan(new StyleSpan(Typeface.BOLD),
                0, strC.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        dataC.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.deep_red)),
                strC.length(), dataC.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mDataC.setText(dataC);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                R.layout.action_item, R.id.action_item_text,
                new String[]{"None", "Message"});
        spinnerArrayAdapter.setDropDownViewResource(R.layout.action_item);
        mActionSpinner.setAdapter(spinnerArrayAdapter);
        mActionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(
                        ContextCompat.getColor(BeaconDetailsActivity.this, R.color.deep_red));

                mMessageWrapper.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
                if (position == 0) {
                    hideKeyboard();
                } else if (position == 1) {
                    showKeyboard(mMessage);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mMessage.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mMessageCount.setText(mMessage.getText().length() + " / 160");
            }
        });

        Realm realm = Realm.getDefaultInstance();
        RealmQuery<BeaconMessage> query = realm.where(BeaconMessage.class);
        query.equalTo("uuid", mBeacon.getUuid().toString());
        query.equalTo("dataA", mBeacon.getDataA());
        query.equalTo("dataB", mBeacon.getDataB());
        query.equalTo("dataC", mBeacon.getDataC());
        RealmResults<BeaconMessage> result = query.findAll();
        if (result.isEmpty()) {
            mMessage.setText("Thank you for choosing NXP BLE Solutions.");
            mMessageWrapper.setVisibility(View.GONE);
            mActionSpinner.setSelection(BeaconMessage.ACTION_DEFAULT_DO_NOTHING);
        } else {
            BeaconMessage message = result.get(0);
            mMessage.setText(message.getMessage());
            mMessageWrapper.setVisibility(message.getActionType() ==
                    BeaconMessage.ACTION_SHOW_MESSAGE ? View.VISIBLE : View.GONE);
            mActionSpinner.setSelection(message.getActionType());
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        if (SdkUtils.hasMarshmallow()) {
            if (PackageManager.PERMISSION_GRANTED !=
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(this, R.string.grant_permission,
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }
    }
}