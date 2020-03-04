/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.freescale.bletoolbox.R;

public class Thermometer extends View {
    private Paint paintOne;
    private Paint paintTwo;
    private Paint paintFive;
    private Paint paintFour;
    private Paint paintThree;
    private Paint paintArc;
    private int radiusOne;
    private int radiusTwo;
    private int radiusFour;
    private int radiusFive;
    private int radiusThree;
    private Paint paintLineTwo;
    private Paint paintLineOne;
    private Paint paintLineThree;
    private float cellwidth;
    private float startCircleX;
    private static final int NUMBER_CELLS_WIDTH = 5;
    private int choose = 0;
    private float XEnd;
    private float totalHeight;
    private float totalWidth;
    private float xLineStop;
    private float xLineStart;

    public Thermometer(Context context) {
        this(context, null);
    }

    public Thermometer(Context context, float x) {
        this(context, null);
    }

    public Thermometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Thermometer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paintOne = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintOne.setColor(getResources().getColor(R.color.temprature_gray));
        paintOne.setStyle(Paint.Style.FILL);

        paintTwo = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTwo.setColor(Color.WHITE);
        paintTwo.setStyle(Paint.Style.FILL);

        paintThree = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintThree.setColor(getResources().getColor(R.color.temprature_gray));
        paintThree.setStyle(Paint.Style.FILL);

        paintFour = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintFour.setColor(Color.WHITE);
        paintFour.setStyle(Paint.Style.FILL);

        paintFive = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintFive.setColor(getResources().getColor(R.color.temprature_yellow));
        paintFive.setStyle(Paint.Style.FILL);

        paintArc = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintArc.setColor(getResources().getColor(R.color.temprature_gray));
        paintArc.setStyle(Paint.Style.STROKE);

        paintLineOne = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLineOne.setColor(getResources().getColor(R.color.temprature_gray));
        paintLineOne.setStyle(Paint.Style.FILL);

        paintLineTwo = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLineTwo.setColor(Color.WHITE);
        paintLineTwo.setStyle(Paint.Style.FILL);

        paintLineThree = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLineThree.setColor(getResources().getColor(R.color.temprature_yellow));
        paintLineThree.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getWidth() > getHeight()) {
            totalWidth = getHeight();
            totalHeight = getWidth();
        } else {
            totalHeight = getHeight();
            totalWidth = getWidth();
        }

        cellwidth = totalWidth / NUMBER_CELLS_WIDTH;
        startCircleX = (int) ((totalWidth / 5) + cellwidth / 1.7);
        radiusOne = (int) (cellwidth * 1.6);
        radiusTwo = (int) (radiusOne * 0.95);
        radiusThree = (int) (0.5 * cellwidth);
        radiusFour = (int) (0.85 * radiusThree);
        radiusFive = (int) (0.85 * radiusFour);

        paintLineOne.setStrokeWidth(radiusThree);
        paintLineTwo.setStrokeWidth((float) (radiusThree / 1.5));
        paintLineThree.setStrokeWidth((float) (radiusThree / 2.9));
        paintArc.setStrokeWidth((float) (radiusThree / 5.9));
        xLineStart = (float) ((cellwidth * 2) * 0.962);
        xLineStop = (float) ((cellwidth * 3) + cellwidth * 0.7);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (choose == 0) {
            drawCircleOne(canvas);
            drawCircleTwo(canvas);
            drawCircleThree(canvas);
            drawCircleFour(canvas);
            drawCircleFive(canvas);
            drawLineOne(canvas);
            drawLineTwo(canvas);
            drawCornerArc(canvas);
        } else if (choose == 1) {
            drawCircleOne(canvas);
            drawCircleTwo(canvas);
            drawCircleThree(canvas);
            drawCircleFour(canvas);
            drawCircleFive(canvas);
            drawLineOne(canvas);
            drawLineTwo(canvas);
            drawLineCus(canvas, xLineStart - 2, XEnd, paintLineThree);
            drawCornerArc(canvas);
        }
    }

    public void changeDrawLine(float _temperatureCPU) {
        choose = 1;
        float valueX;
        float temperatureCPU;

        if (_temperatureCPU >= 0) {

            temperatureCPU = (float) (_temperatureCPU + 40);
        } else {
            temperatureCPU = _temperatureCPU * (-1);
        }
        valueX = (xLineStop - xLineStart) / 120;
        XEnd = (valueX * temperatureCPU) + xLineStart;
    }

    private void drawCircleFive(Canvas canvas) {
        drawCircleCustom(canvas, radiusFive, paintFive);
    }

    private void drawCircleFour(Canvas canvas) {
        drawCircleCustom(canvas, radiusFour, paintFour);
    }

    private void drawCircleThree(Canvas canvas) {
        drawCircleCustom(canvas, radiusThree, paintThree);
    }

    private void drawCircleCustom(Canvas canvas, float radius, Paint paint) {
        canvas.drawCircle(startCircleX, totalHeight / 3, radius, paint);
    }

    private void drawCircleOne(Canvas canvas) {
        drawCircleOneTwo(canvas, radiusOne, paintOne);
    }

    private void drawCircleTwo(Canvas canvas) {
        drawCircleOneTwo(canvas, radiusTwo, paintTwo);
    }

    private void drawCircleOneTwo(Canvas canvas, float radius, Paint paint) {
        canvas.drawCircle(totalWidth / 2, totalHeight / 3, radius, paint);
    }

    public float getTopHeight(){
        return totalHeight / 3 + radiusOne;
    }

    private void drawLineTwo(Canvas canvas) {
        float startX = (float) ((cellwidth * 2) * 0.979);
        float xtop = (float) ((cellwidth * 3) + cellwidth * 0.71);
        drawLineCus(canvas, startX, xtop, paintLineTwo);
    }

    private void drawLineOne(Canvas canvas) {
        float startX = cellwidth * 2;
        drawLineCus(canvas, startX, xLineStop, paintLineOne);
    }

    private void drawLineCus(Canvas canvas, float startX, float stopX, Paint paint) {
        canvas.drawLine(startX, totalHeight / 3, stopX, totalHeight / 3, paint);
    }

    private void drawCornerArc(Canvas canvas) {
        float let = (float) ((cellwidth * 3) + cellwidth * 0.49);
        float top = (float) ((totalHeight / 3) - (radiusThree / 2) * 0.84);
        float right = (float) ((cellwidth * 3) + cellwidth * 0.89);
        float bottom = (float) ((totalHeight / 3) + (radiusThree / 2) * 0.84);
        RectF rectF = new RectF(let, top, right, bottom);
        canvas.drawArc(rectF, 270, 180, false, paintArc);
    }
}

