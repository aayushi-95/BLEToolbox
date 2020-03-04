/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */

package com.freescale.bletoolbox.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.freescale.bletoolbox.R;

public class SemiCircleProgressBar extends View {

    private Context mContext;

    private Path mPathD;
    private Bitmap mBitmapD;

    private Path mPathA;
    private Bitmap mBitmapA;

    private float mPivotXA;
    private float mPivotYA;

    private Path mPathB;
    private Bitmap mBitmapB;

    private Path mPathC;
    private Bitmap mBitmapC;

    private Bitmap bitmap;
    private float angle;
    private float getWidth_Screen;
    private float getHeight_Screen;
    private DisplayMetrics metrics;
    private RectF oval;

    public SemiCircleProgressBar(Context context) {
        this(context, null);
    }

    public SemiCircleProgressBar(Context context, float x) {
        this(context, null);
    }

    public SemiCircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SemiCircleProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        getWidth_Screen = getWidthScreen();
        getHeight_Screen = getHeightScreen();
        initImageA();
        initImageB();
        initImageC();
        initImageD();

        setClippingA(100);
        setClippingB(100);
        setClippingC(100);
    }


    private void initImageA() {
        mPathA = new Path();
        mPivotXA = getWidth_Screen * 3;
        mPivotYA = getHeight_Screen;
        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.circle_a);
        int imageWidth = (int) (getWidth_Screen * 10) + 120;
        int imageHeight = (int) (getWidth_Screen * 10) + 120;
        mBitmapA = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, false);
    }

    private void initImageB() {
        mPathB = new Path();
        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.circle_b);
        int imageWidth = (int) (getWidth_Screen * 10) + 80;
        int imageHeight = (int) (getWidth_Screen * 10) + 80;
        mBitmapB = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, false);
    }

    private void initImageC() {
        mPathC = new Path();
        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.circle_c);
        int imageWidth = (int) (getWidth_Screen * 10);
        int imageHeight = (int) (getWidth_Screen * 10);
        mBitmapC = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, false);
    }

    private void initImageD() {
        mPathD = new Path();
        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.circle_d);
        int imageWidth = (int) (getWidth_Screen * 10);
        int imageHeight = (int) (getWidth_Screen * 10);
        mBitmapD = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, false);
    }

    public void setClippingA(float progress) {
        angle = (progress * 180) / 100;
    //    mPathA.reset();
        oval = new RectF(mPivotXA, mPivotYA, mPivotXA + mBitmapA.getWidth(), mPivotYA + mBitmapA.getHeight());
        mPathA.moveTo(oval.centerX(), oval.centerY());
        mPathA.addArc(oval, 180, angle);
        mPathA.lineTo(oval.centerX(), oval.centerY());
    }

    public void setClippingB(float progress) {
      //  mPathB.reset();
        oval = new RectF(mPivotXA, mPivotYA, mPivotXA + mBitmapB.getWidth(), mPivotYA + mBitmapB.getHeight());
        mPathB.moveTo(oval.centerX(), oval.centerY());
        mPathB.addArc(oval, 180, angle);
        mPathB.lineTo(oval.centerX(), oval.centerY());
    }

    public void setClippingC(float progress) {
      //  mPathC.reset();
        oval = new RectF(mPivotXA, mPivotYA, mPivotXA + mBitmapC.getWidth(), mPivotYA + mBitmapC.getHeight());
        mPathC.moveTo(oval.centerX(), oval.centerY());
        mPathC.addArc(oval, 180, angle);
        mPathC.lineTo(oval.centerX(), oval.centerY());

    }

    public void setClippingD(float progress) {
        float angle = (progress * 180) / 100;
        mPathD.reset();
        RectF oval = new RectF(mPivotXA, mPivotYA, mPivotXA + mBitmapD.getWidth(), mPivotYA + mBitmapD.getHeight());
        mPathD.moveTo(oval.centerX(), oval.centerY());
        mPathD.addArc(oval, 180, angle);
        mPathD.lineTo(oval.centerX(), oval.centerY());
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmapA, mPivotXA - 60, mPivotYA - 60, null);
        canvas.drawBitmap(mBitmapB, mPivotXA - 40, mPivotYA - 40, null);
        canvas.clipPath(mPathC);
        canvas.drawBitmap(mBitmapC, mPivotXA, mPivotYA, null);
        canvas.clipPath(mPathD);
        canvas.drawBitmap(mBitmapD, mPivotXA, mPivotYA, null);
    }

    private float getWidthScreen() {
        metrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels / 16;
    }

    private float getHeightScreen() {
        return metrics.heightPixels / 8;
    }

    public int getHeightItemCenter(){
        return (int) (((getWidth_Screen * 10)/2) - 130 + getHeight_Screen);
    }

    public int getHeightItemLeftRight(){
        return (int) ((getWidth_Screen * 10)/2 + getHeight_Screen);
    }

    public int get_PadingLeftA(){
        return (int) (mPivotXA + (getWidth_Screen * 10)/8.2);
    }

    public int get_PadingLeftB(){
        return (int) (mPivotXA + (getWidth_Screen * 8) + getWidth_Screen* 0.2);
    }
}
