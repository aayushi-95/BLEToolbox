package com.freescale.bletoolbox.fragment.Sersor_Fragments;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.fragment.BaseFragment;
import com.freescale.bletoolbox.fragment.IActivityToFragment;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;
import com.freescale.bletoolbox.view.FslRotateAnimation;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.io.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nxf42542 on 2018/7/27.
 */

public class SensorECompassFragment extends BaseFragment implements View.OnTouchListener, IActivityToFragment, View.OnClickListener {
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
        View view = inflater.inflate(R.layout.fragment_sensor_ecompass, container, false);
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

    @Override
    public void onEventMainThread(BLEStateEvent.DataWritenFromClient e) {
        if (e == null) return;
        byte[] buffering= e.value;
        convertChipValueToDigree(buffering);
    }

    private void convertChipValueToDigree(byte[] chipValue){

        byte[] iGpxArr = new byte[8];
        byte[] iGpyArr = new byte[8];
        byte[] iGpzArr = new byte[8];
        byte[] iBpxArr = new byte[8];
        byte[] iBpyArr = new byte[8];
        byte[] iBpzArr = new byte[8];
        System.arraycopy(chipValue,10,iGpyArr,0,8);
        System.arraycopy(chipValue,19,iGpxArr,0,8);
        System.arraycopy(chipValue,28,iGpzArr,0,8);
        System.arraycopy(chipValue,37,iBpyArr,0,8);
        System.arraycopy(chipValue,46,iBpxArr,0,8);
        System.arraycopy(chipValue,55,iBpzArr,0,8);

        int iGpx = - byteArrayToInt(iGpxArr);
        int iGpy = - byteArrayToInt(iGpyArr);
        int iGpz =   byteArrayToInt(iGpzArr);
        int iBpx =   byteArrayToInt(iBpxArr);
        int iBpy =   byteArrayToInt(iBpyArr);
        int iBpz = - byteArrayToInt(iBpzArr);

        Log.e("ecompass\n","iGpx:" + iGpx + "\n" + "iGpy:" + iGpy + "\n" + "iGpz:" + iGpz + "\n" + "iBpx:" + iBpx + "\n" + "iBpy:" + iBpy + "\n" + "iBpz:" + iBpz + "\n");

        int angleValue = ecompass_calculate_heading(iBpx,iBpy,iBpz,iGpx,iGpy,iGpz)/100;
        Log.e("","angleValue   :"+ angleValue);
        if (angleValue < 0)
            angleValue = 359 + angleValue;
        currentDegree = angleValue;
        updateCompass(currentDegree);
    }
    private int byteArrayToInt(byte[] b) {
        return b[7] & 0xF |
                (b[6] & 0xF) << 4 |
                (b[5] & 0xF) << 8 |
                (b[4] & 0xF) << 12|
                (b[3] & 0xF) << 16|
                (b[2] & 0xF) << 20|
                (b[1] & 0xF) << 24|
                (b[0] & 0xF) << 28;
    }


    private long converStringToLong(String dataInput) throws IOException {
        InputStream stream = new ByteArrayInputStream(dataInput.getBytes());
        DataInputStream dis = new DataInputStream(stream);
        while (dis.available() > 0){
            int k = dis.readUnsignedShort();
        }

        return 0;
    }
    /* roll pitch and yaw angles computed by iecompass */
    static int iPhi, iThe, iPsi;
    /* magnetic field readings corrected for hard iron effects and PCB orientation */
    static int  iBfx, iBfy, iBfz;
/******************************************************************************
 * Definitions
 ******************************************************************************/
    static final int MINDELTATRIG = 1;       /* final step size for iTrig */
    static final int  MINDELTADIV = 1;       /* final step size for iDivide */

/* fifth order of polynomial approximation giving 0.05 deg max error */
    static final int  K1 = 5701;
    static final int K2 = -1645;
    static final int K3 = 446;

    private int ecompass_calculate_heading(int iBpx, int iBpy, int iBpz,
                                           int iGpx, int iGpy, int iGpz)
    {
        int iSin, iCos; /* sine and cosine */

    /* calculate current roll angle Phi */
        iPhi = iHundredAtan2Deg(iGpy, iGpz);/* Eq 13 */

    /* calculate sin and cosine of roll angle Phi */
        iSin = iTrig(iGpy, iGpz); /* Eq 13: sin = opposite / hypotenuse */
        iCos = iTrig(iGpz, iGpy); /* Eq 13: cos = adjacent / hypotenuse */

    /* de-rotate by roll angle Phi */
        iBfy = (int)((iBpy * iCos - iBpz * iSin) >> 15);/* Eq 19 y component */
        iBpz = (int)((iBpy * iSin + iBpz * iCos) >> 15);/* Bpy*sin(Phi)+Bpz*cos(Phi)*/
        iGpz = (int)((iGpy * iSin + iGpz * iCos) >> 15);/* Eq 15 denominator */

    /* calculate current pitch angle Theta */
        iThe = iHundredAtan2Deg((int)-iGpx, iGpz);/* Eq 15 */

    /* restrict pitch angle to range -90 to 90 degrees */
        if (iThe > 9000) iThe = (int) (18000 - iThe);
        if (iThe < -9000) iThe = (int) (-18000 - iThe);

    /* calculate sin and cosine of pitch angle Theta */
        iSin = (int)-iTrig((int) iGpx, (int) iGpz); /* Eq 15: sin = opposite / hypotenuse */
        iCos = iTrig((int)iGpz, (int)iGpx); /* Eq 15: cos = adjacent / hypotenuse */

    /* correct cosine if pitch not in range -90 to 90 degrees */
        if (iCos < 0) iCos = (int)-iCos;

    /* de-rotate by pitch angle Theta */
        iBfx = (int)((iBpx * iCos + iBpz * iSin) >> 15); /* Eq 19: x component */
        iBfz = (int)((-iBpx * iSin + iBpz * iCos) >> 15);/* Eq 19: z component */

    /* calculate current yaw = e-compass angle Psi */
        iPsi = iHundredAtan2Deg((int)-iBfy, iBfx); /* Eq 22 */

        return iPsi;
    }
    /* calculates 100*atan2(iy/ix)=100*atan2(iy,ix) in deg for ix, iy in range -32768 to 32767 */
    private int  iHundredAtan2Deg(int iy, int ix)
    {
        int iResult; /* angle in degrees times 100 */

    /* check for -32768 which is not handled correctly */
        if (ix == -32768) ix = -32767;
        if (iy == -32768) iy = -32767;

    /* check for quadrants */
        if ((ix >= 0) && (iy >= 0)) /* range 0 to 90 degrees */
            iResult = iHundredAtanDeg(iy, ix);

        else if ((ix <= 0) && (iy >= 0)) /* range 90 to 180 degrees */
            iResult = (int)(18000 - (int)iHundredAtanDeg(iy, (int)-ix));

        else if ((ix <= 0) && (iy <= 0)) /* range -180 to -90 degrees */
            iResult = (int)((int)-18000 + iHundredAtanDeg((int)-iy, (int)-ix));

        else /* ix >=0 and iy <= 0 giving range -90 to 0 degrees */
            iResult = (int)(-iHundredAtanDeg((int)-iy, ix));

        return (iResult);
    }
    /* calculates 100*atan(iy/ix) range 0 to 9000 for all ix, iy positive in range 0 to 32767 */
    private int iHundredAtanDeg(int iy, int ix)
    {
        int iAngle; /* angle in degrees times 100 */
        int iRatio; /* ratio of iy / ix or vice versa */
        int iTmp; /* temporary variable */

    /* check for pathological cases */
        if ((ix == 0) && (iy == 0)) return (0);
        if ((ix == 0) && (iy != 0)) return (9000);

    /* check for non-pathological cases */
        if (iy <= ix)
            iRatio = iDivide(iy, ix); /* return a fraction in range 0. to 32767 = 0. to 1. */
        else
            iRatio = iDivide(ix, iy); /* return a fraction in range 0. to 32767 = 0. to 1. */

    /* first, third and fifth order polynomial approximation */
        iAngle = (int) K1 * (int) iRatio;
        iTmp = ((int) iRatio >> 5) * ((int) iRatio >> 5) * ((int) iRatio >> 5);
        iAngle += (iTmp >> 15) * (int) K2;
        iTmp = (iTmp >> 20) * ((int) iRatio >> 5) * ((int) iRatio >> 5);
        iAngle += (iTmp >> 15) * (int) K3;
        iAngle = iAngle >> 15;

    /* check if above 45 degrees */
        if (iy > ix) iAngle = (int)(9000 - iAngle);

    /* for tidiness, limit result to range 0 to 9000 equals 0.0 to 90.0 degrees */
        if (iAngle < 0) iAngle = 0;
        if (iAngle > 9000) iAngle = 9000;

        return ((int) iAngle);
    }
    /* function to calculate ir = iy / ix with iy <= ix, and ix, iy both > 0 */
    private int iDivide(int iy, int ix)
    {
        int itmp; /* scratch */
        int ir; /* result = iy / ix range 0., 1. returned in range 0 to 32767 */
        int idelta; /* delta on candidate result dividing each stage by factor of 2 */

    /* set result r to zero and binary search step to 16384 = 0.5 */
        ir = 0;
        idelta = 16384; /* set as 2^14 = 0.5 */

    /* to reduce quantization effects, boost ix and iy to the maximum signed 16 bit value */
        while ((ix < 16384) && (iy < 16384))
        {
            ix = (int)(ix + ix);
            iy = (int)(iy + iy);
        }

    /* loop over binary sub-division algorithm solving for ir*ix = iy */
        do
        {
        /* generate new candidate solution for ir and test if we are too high or too low */
            itmp = (int)(ir + idelta); /* itmp=ir+delta, the candidate solution */
            itmp = (int)((itmp * ix) >> 15);
            if (itmp <= iy) ir += idelta;
            idelta = (int)(idelta >> 1); /* divide by 2 using right shift one bit */
        }
        while (idelta >= MINDELTADIV); /* last loop is performed for idelta=MINDELTADIV */

        return (ir);
    }

    private int iTrig(int ix, int iy)
    {
        int itmp; /* scratch */
        int ixsq; /* ix * ix */
        int isignx; /* storage for sign of x. algorithm assumes x >= 0 then corrects later */
        int ihypsq; /* (ix * ix) + (iy * iy) */
        int ir; /* result = ix / sqrt(ix*ix+iy*iy) range -1, 1 returned as signed int16_t */
        int idelta; /* delta on candidate result dividing each stage by factor of 2 */

    /* stack variables */
    /* ix, iy: signed 16 bit integers representing sensor reading in range -32768 to 32767 */
    /* function returns signed int16_t as signed fraction (ie +32767=0.99997, -32768=-1.0000) */
    /* algorithm solves for ir*ir*(ix*ix+iy*iy)=ix*ix */
    /* correct for pathological case: ix==iy==0 */
        if ((ix == 0) && (iy == 0)) ix = iy = 1;

    /* check for -32768 which is not handled correctly */
        if (ix == -32768) ix = -32767;
        if (iy == -32768) iy = -32767;

    /* store the sign for later use. algorithm assumes x is positive for convenience */
        isignx = 1;
        if (ix < 0)
        {
            ix = (int)-ix;
            isignx = -1;
        }

    /* for convenience in the boosting set iy to be positive as well as ix */
        if(iy < 0){
            iy -= (2*iy);
        }

    /* to reduce quantization effects, boost ix and iy but keep below maximum signed 16 bit */
        while ((ix < 16384) && (iy < 16384))
        {
            ix = (int)(ix + ix);
            iy = (int)(iy + iy);
        }

    /* calculate ix*ix and the hypotenuse squared */
        ixsq = (int)(ix * ix); /* ixsq=ix*ix: 0 to 32767^2 = 1073676289 */
        ihypsq = (int)(ixsq + iy * iy); /* ihypsq=(ix*ix+iy*iy) 0 to 2*32767*32767=2147352578 */

    /* set result r to zero and binary search step to 16384 = 0.5 */
        ir = 0;
        idelta = 16384; /* set as 2^14 = 0.5 */

    /* loop over binary sub-division algorithm */
        do
        {
        /* generate new candidate solution for ir and test if we are too high or too low */
        /* itmp=(ir+delta)^2, range 0 to 32767*32767 = 2^30 = 1073676289 */
            itmp = (int)((ir + idelta) * (ir + idelta));

        /* itmp=(ir+delta)^2*(ix*ix+iy*iy), range 0 to 2^31 = 2147221516 */
            itmp = (itmp >> 15) * (ihypsq >> 15);

            if (itmp <= ixsq) ir += idelta;
            idelta = (int)(idelta >> 1); /* divide by 2 using right shift one bit */
        }

        while (idelta >= MINDELTATRIG); /* last loop is performed for idelta=MINDELTATRIG */

    /* correct the sign before returning */
        return (int)(ir * isignx);
    }
}
