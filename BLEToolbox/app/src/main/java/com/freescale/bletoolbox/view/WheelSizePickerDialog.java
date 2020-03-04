/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.freescale.bletoolbox.AppConfig;
import com.freescale.bletoolbox.R;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WheelSizePickerDialog extends MaterialDialog {

    public interface OnSizePickedListener {

        void onSizePicked(String size);
    }

    @Bind(R.id.wheel_sizes)
    RecyclerView mRecyclerView;

    private WheelSizeAdapter mAdapter;
    private String[] mInternalNames;
    private String mSelectedName;

    public WheelSizePickerDialog(MaterialDialog.Builder builder) {
        super(builder);
    }

    public static WheelSizePickerDialog newInstance(Context context, String title, String current, final OnSizePickedListener listener) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(title).titleColor(ContextCompat.getColor(context, R.color.red))
                .customView(R.layout.wheel_size_picker, true)
                .positiveText(R.string.accept).positiveColor(ContextCompat.getColor(context, R.color.red));
        final WheelSizePickerDialog dialog = new WheelSizePickerDialog(builder);
        //Get original Dimension
        float scaleRatio = context.getResources().getDisplayMetrics().density;
        float dimenPix = context.getResources().getDimension(R.dimen.cycling_positive_button_text_size);
        float dimenOrginal = dimenPix/scaleRatio;
        dialog.positiveButton.setTextSize(dimenOrginal);

        dimenPix = context.getResources().getDimension(R.dimen.cycling_title_text_size);
        dimenOrginal = dimenPix/scaleRatio;
        dialog.title.setTextSize(dimenOrginal);
        dialog.mSelectedName = current;
        dialog.positiveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onSizePicked(dialog.mSelectedName);
                }
            }
        });
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        mInternalNames = new String[AppConfig.WHEEL_CIRCUMFERENCES.size()];
        int i = 0;
        int selectedPosition = 0;
        for (Map.Entry<String, Integer> entry : AppConfig.WHEEL_CIRCUMFERENCES.entrySet()) {
            if (entry.getKey().equals(mSelectedName)) {
                selectedPosition = i;
            }
            mInternalNames[i++] = entry.getKey();
        }
        mAdapter = new WheelSizeAdapter();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(selectedPosition);
    }

    private final View.OnClickListener mSizePickerHandler = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int itemPosition = mRecyclerView.getChildAdapterPosition(v);
            if (itemPosition < 0 || itemPosition >= mInternalNames.length) {
                return;
            }
            mSelectedName = mInternalNames[itemPosition];
            mAdapter.notifyDataSetChanged();
        }
    };

    static class WheelSizeHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.wheel_size_picker_name)
        RadioButton name;

        @Bind(R.id.wheel_size_picker_value)
        TextView value;

        public WheelSizeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class WheelSizeAdapter extends RecyclerView.Adapter<WheelSizeHolder> {

        @Override
        public int getItemCount() {
            return mInternalNames.length;
        }

        @Override
        public WheelSizeHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            final View view = getLayoutInflater().inflate(R.layout.wheel_size_picker_item, viewGroup, false);
            view.setOnClickListener(mSizePickerHandler);
            WheelSizeHolder holder = new WheelSizeHolder(view);
            holder.name.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mSizePickerHandler.onClick(view);
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(WheelSizeHolder holder, int i) {
            String key = mInternalNames[i];
            holder.name.setText(key);
            holder.value.setText(AppConfig.WHEEL_CIRCUMFERENCES.get(key).toString());
            holder.name.setChecked(key.equals(mSelectedName));
        }
    }
}