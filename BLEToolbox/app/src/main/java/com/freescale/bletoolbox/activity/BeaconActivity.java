/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freescale.bletoolbox.AppConfig;
import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.model.BeaconMessage;
import com.freescale.bletoolbox.model.FSLBeacon;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class BeaconActivity extends BaseScanActivity {

    private BeaconAdapter mBeaconAdapter;
    private List<FSLBeacon> mBeacons = new ArrayList<>();

    @Override
    protected boolean isBeaconScanning() {
        return true;
    }

    @Override
    protected void clearData() {
        mBeacons.clear();
        if (mBeaconAdapter != null) {
            mBeaconAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void appendDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        FSLBeacon beacon = FSLBeacon.fromScanRecord(device, rssi, scanRecord);
        if (beacon != null) {
            if (mBeacons.contains(beacon)) {
                beacon = mBeacons.get(mBeacons.indexOf(beacon));
                beacon.setLastScannedTime(System.nanoTime());
            } else {
                mBeacons.add(beacon);
            }
            beacon.setRssi(rssi);
        }
    }

    @Override
    protected void initList() {
        mBeaconAdapter = new BeaconAdapter();
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mBeaconAdapter);
        }
    }

    @Override
    protected void refreshList() {
        final long now = System.nanoTime();
        for (int i = 0; i < mBeacons.size(); ++i) {
            if ((now - mBeacons.get(i).getLastScannedTime()) / 1000 / 1000 > AppConfig.BEACON_OUT_OF_RANGE_PERIOD) {
                mBeacons.remove(i);
            }
        }
        mBeaconAdapter.notifyDataSetChanged();
    }

    private final View.OnClickListener mDeviceClickHandler = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mBeacons == null) {
                return;
            }
            int itemPosition = mRecyclerView.getChildAdapterPosition(v);
            if (itemPosition < 0 || itemPosition >= mBeacons.size()) {
                return;
            }
            FSLBeacon beacon = mBeacons.get(itemPosition);
            Intent intent = new Intent(BeaconActivity.this, BeaconDetailsActivity.class);
            intent.putExtra(BeaconDetailsActivity.INTENT_BEACON, beacon);
            startActivity(intent);
        }
    };

    static class BeaconHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.beacon_manufacture)
        TextView manufacture;

        @Bind(R.id.beacon_uuid)
        TextView uuid;

        @Bind(R.id.beacon_data_a)
        TextView dataA;

        @Bind(R.id.beacon_data_b)
        TextView dataB;

        @Bind(R.id.beacon_data_c)
        TextView dataC;

        @Bind(R.id.beacon_rssi)
        TextView rssi;

        @Bind(R.id.beacon_message)
        TextView message;

        public BeaconHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class BeaconAdapter extends RecyclerView.Adapter<BeaconHolder> {

        @Override
        public int getItemCount() {
            return mBeacons == null ? 0 : mBeacons.size();
        }

        @Override
        public BeaconHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.beacon_item, viewGroup, false);
            view.setOnClickListener(mDeviceClickHandler);
            return new BeaconHolder(view);
        }

        @Override
        public void onBindViewHolder(BeaconHolder holder, int position) {
            FSLBeacon beacon = mBeacons.get(position);
//            String originId = beacon.getManufactureId() == 0x01ff ? "Freescale" : ("0x" + String.format("%04X", beacon.getManufactureId() & 0x0FFFF));
            String strMenufacturer = "Manufacturer ID: ";
            String originId = beacon.getManufactureId() == 0x0025 ? "NXP" :
                    ("0x" + String.format("%04X", beacon.getManufactureId() & 0x0FFFF));
            SpannableString manufactureId = new SpannableString( strMenufacturer + originId);
            manufactureId.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BeaconActivity.this, R.color.red)),
                    0, strMenufacturer.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            manufactureId.setSpan(new StyleSpan(Typeface.BOLD),
                    0, strMenufacturer.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            manufactureId.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BeaconActivity.this, R.color.deep_red)),
                    strMenufacturer.length()-1, manufactureId.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            holder.manufacture.setText(manufactureId);

            String strUUID = "UUID: ";
            SpannableString uuid = new SpannableString(strUUID + beacon.getUuid().toString().toUpperCase(Locale.getDefault()));
            uuid.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BeaconActivity.this, R.color.red)),
                    0, strUUID.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            uuid.setSpan(new StyleSpan(Typeface.BOLD),
                    0, strUUID.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            uuid.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BeaconActivity.this, R.color.deep_red)),
                    strUUID.length(), uuid.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            holder.uuid.setText(uuid);

            String strRSSI = "RSSI: ";
            SpannableString rssi = new SpannableString( strRSSI + beacon.getRssi() + " dBm");
            rssi.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BeaconActivity.this, R.color.red)),
                    0, strRSSI.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            rssi.setSpan(new StyleSpan(Typeface.BOLD),
                    0, strRSSI.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            rssi.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BeaconActivity.this, R.color.deep_red)),
                    strRSSI.length(), rssi.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            holder.rssi.setText(rssi);

            String strA = "A: ";
            SpannableString dataA = new SpannableString(strA + beacon.getDataA());
            dataA.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BeaconActivity.this, R.color.red)),
                    0, strA.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            dataA.setSpan(new StyleSpan(Typeface.BOLD),
                    0, strA.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            dataA.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BeaconActivity.this, R.color.deep_red)),
                    strA.length(), dataA.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            holder.dataA.setText(dataA);

            String strB = "B: ";
            SpannableString dataB = new SpannableString(strB + beacon.getDataB());
            dataB.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BeaconActivity.this, R.color.red)),
                    0, strB.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            dataB.setSpan(new StyleSpan(Typeface.BOLD),
                    0, strB.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            dataB.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BeaconActivity.this, R.color.deep_red)),
                    strB.length(), dataB.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            holder.dataB.setText(dataB);

            String strC = "C: ";
            SpannableString dataC = new SpannableString(strC + beacon.getDataC());
            dataC.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BeaconActivity.this, R.color.red)),
                    0, strC.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            dataC.setSpan(new StyleSpan(Typeface.BOLD),
                    0, strC.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            dataC.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BeaconActivity.this, R.color.deep_red)),
                    strC.length(), dataC.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            holder.dataC.setText(dataC);

            holder.message.setVisibility(View.GONE);
            Realm realm = Realm.getDefaultInstance();
            RealmQuery<BeaconMessage> query = realm.where(BeaconMessage.class);
            query.equalTo("uuid", beacon.getUuid().toString());
            query.equalTo("dataA", beacon.getDataA());
            query.equalTo("dataB", beacon.getDataB());
            query.equalTo("dataC", beacon.getDataC());
            RealmResults<BeaconMessage> result = query.findAll();
            if (!result.isEmpty()) {
                BeaconMessage message = result.get(0);
                if (message.getActionType() == BeaconMessage.ACTION_SHOW_MESSAGE) {
                    holder.message.setVisibility(View.VISIBLE);
                    holder.message.setText(message.getMessage());
                }
            }
        }
    }
}
