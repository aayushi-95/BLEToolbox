/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.fragment.KW40_Fragments;

import com.freescale.bletoolbox.fragment.IActivityToFragment;
import com.freescale.bletoolbox.fragment.BaseFragment;

import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;
import com.freescale.bletoolbox.view.FslRotateAnimation;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ECompassFragment extends BaseFragment implements View.OnTouchListener, IActivityToFragment, View.OnClickListener {

    @Bind(R.id.imageViewCompass)
    ImageView imageViewCompass;

    @Bind(R.id.tvCompassStop)
    TextView tvCompassStop;

    @Bind(R.id.tvDigit)
    TextView tvDigit;

    private boolean compassStart;
    private float currentDegree;
    private float currentDegreeNeedles;
    private Dialog dialog;
    private FslRotateAnimation fslRotateAnimation;
    private int responseCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_compass, container, false);
        ButterKnife.bind(this, view);
        currentDegree = 0f;
        compassStart = false;
        tvCompassStop.setText(getString(R.string.ecompass_stop));
        showDialog();
        return view;
    }

    public void setEventOnlick(){
        tvCompassStop.setOnClickListener(this);
        tvCompassStop.setOnTouchListener(this);
    }

    public void showDialog() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.view_custom_dialog);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        dialogButton.setOnTouchListener(new View.OnTouchListener() {
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
        });
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compassStart = true;
                setEventOnlick();
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                compassStart = true;
                setEventOnlick();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BLEService.INSTANCE.request(BLEAttributes.COMPASS_SERVICE, BLEAttributes.COMPASS_CHARACTERISTIC, BLEService.Request.NOTIFY);
        fslRotateAnimation = new FslRotateAnimation(0f, 90f, FslRotateAnimation.RELATIVE_TO_SELF, 0.5f, FslRotateAnimation.RELATIVE_TO_SELF, 0.5f);
        fslRotateAnimation.setFillAfter(true);
        fslRotateAnimation.setInterpolator(getActivity(), android.R.anim.accelerate_decelerate_interpolator);
    }

    @Override
    public void upDateUI(BLEStateEvent.DataAvailable e) {
        if (e == null) return;
        BluetoothGattCharacteristic gattCharacteristic = e.characteristic;
        String charaterUuid = gattCharacteristic.getUuid().toString();
        if (BLEAttributes.COMPASS_CHARACTERISTIC.toUpperCase().equals(charaterUuid.toUpperCase())) {
            if (compassStart) {
                if (0 == responseCount) {
                    responseCount++;
                    float flag = gattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
                    Log.e("COMPASS", "degree " + flag + " time " + System.currentTimeMillis());
                    currentDegree = flag;
                    updateCompass(flag);
                } else {
                    responseCount = 0;
                }
            }
        }
    }

    private void updateCompass(final float newDegreeBase) {
        if (currentDegreeNeedles == newDegreeBase) {
            return;
        }
        int degree = Math.round(newDegreeBase);
        tvDigit.setText(degree + "\u00B0");
        float newDegree = newDegreeBase;
        float dif = Math.abs((newDegree - currentDegreeNeedles));
        if (5 > dif) {
            return;
        } else {
            currentDegreeNeedles = newDegreeBase;
        }
        fslRotateAnimation.updateToDegrees(currentDegreeNeedles);
        imageViewCompass.startAnimation(fslRotateAnimation);
    }

    @Override
    public void resetDefault() {
        dialog.dismiss();
        BLEService.INSTANCE.request(BLEAttributes.COMPASS_SERVICE,
                BLEAttributes.COMPASS_CHARACTERISTIC,
                BLEService.Request.DISABLE_NOTIFY_INDICATE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvCompassStop:
                if (compassStart) {
                    compassStart = false;
                    tvCompassStop.setText(getString(R.string.ecompass_start));
                    BLEService.INSTANCE.request(BLEAttributes.COMPASS_SERVICE,
                            BLEAttributes.COMPASS_CHARACTERISTIC,
                            BLEService.Request.DISABLE_NOTIFY_INDICATE);
                } else {
                    compassStart = true;
                    tvCompassStop.setText(getString(R.string.ecompass_stop));
                    BLEService.INSTANCE.request(BLEAttributes.COMPASS_SERVICE,
                            BLEAttributes.COMPASS_CHARACTERISTIC,
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
