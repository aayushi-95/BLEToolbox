package com.freescale.bletoolbox.view;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class FslRotateAnimation extends Animation {
    private static final float SPEED_ROTATION = 90f / 400f;
    private static final long MIN_DURATION = 200l;
    private static final long MAX_DURATION = 600l;
    private float mFromDegrees;
    private float mToDegrees;
    private float mCurrentDegrees;

    private int mPivotXType = ABSOLUTE;
    private int mPivotYType = ABSOLUTE;
    private float mPivotXValue = 0.0f;
    private float mPivotYValue = 0.0f;

    private float mPivotX;
    private float mPivotY;

    /**
     * Constructor to use when building a RotateAnimation from code
     *
     * @param fromDegrees Rotation offset to apply at the start of the
     *                    animation.
     * @param toDegrees   Rotation offset to apply at the end of the animation.
     * @param pivotXType  Specifies how pivotXValue should be interpreted. One of
     *                    Animation.ABSOLUTE, Animation.RELATIVE_TO_SELF, or
     *                    Animation.RELATIVE_TO_PARENT.
     * @param pivotXValue The X coordinate of the point about which the object
     *                    is being rotated, specified as an absolute number where 0 is the
     *                    left edge. This value can either be an absolute number if
     *                    pivotXType is ABSOLUTE, or a percentage (where 1.0 is 100%)
     *                    otherwise.
     * @param pivotYType  Specifies how pivotYValue should be interpreted. One of
     *                    Animation.ABSOLUTE, Animation.RELATIVE_TO_SELF, or
     *                    Animation.RELATIVE_TO_PARENT.
     * @param pivotYValue The Y coordinate of the point about which the object
     *                    is being rotated, specified as an absolute number where 0 is the
     *                    top edge. This value can either be an absolute number if
     *                    pivotYType is ABSOLUTE, or a percentage (where 1.0 is 100%)
     *                    otherwise.
     */
    public FslRotateAnimation(float fromDegrees, float toDegrees, int pivotXType, float pivotXValue,
                              int pivotYType, float pivotYValue) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;

        mPivotXValue = pivotXValue;
        mPivotXType = pivotXType;
        mPivotYValue = pivotYValue;
        mPivotYType = pivotYType;
        initializePivotPoint();
    }

    /**
     * Called at the end of constructor methods to initialize, if possible, values for
     * the pivot point. This is only possible for ABSOLUTE pivot values.
     */
    private void initializePivotPoint() {
        if (mPivotXType == ABSOLUTE) {
            mPivotX = mPivotXValue;
        }
        if (mPivotYType == ABSOLUTE) {
            mPivotY = mPivotYValue;
        }
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float degrees = mFromDegrees + ((mToDegrees - mFromDegrees) * interpolatedTime);
        float scale = getScaleFactor();

        if (mPivotX == 0.0f && mPivotY == 0.0f) {
            t.getMatrix().setRotate(degrees);
        } else {
            t.getMatrix().setRotate(degrees, mPivotX * scale, mPivotY * scale);
        }
        mCurrentDegrees = degrees;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mPivotX = resolveSize(mPivotXType, mPivotXValue, width, parentWidth);
        mPivotY = resolveSize(mPivotYType, mPivotYValue, height, parentHeight);
    }

    public void setFromDegrees(float mFromDegrees) {
        this.mFromDegrees = mFromDegrees;
    }

    public void setToDegrees(float mToDegrees) {
        this.mToDegrees = mToDegrees;
    }

    public void updateToDegrees(float toDegrees) {
        this.cancel();
        this.reset();
        float fromDegreeNeedles, toDegreeNeedles;
        float currentDegrees = getConvertedCurrentDegree();
        toDegreeNeedles = toDegrees;
        //
        if (360 > toDegreeNeedles && toDegreeNeedles - currentDegrees > 180) {
            fromDegreeNeedles = 360 + currentDegrees;
        } else if (360 > toDegreeNeedles && currentDegrees - toDegreeNeedles > 180) {
            fromDegreeNeedles = currentDegrees - 360;
        } else {
            fromDegreeNeedles = currentDegrees;
        }
        this.mFromDegrees = fromDegreeNeedles;
        this.mToDegrees = toDegreeNeedles;
        float changedDegrees = Math.abs(this.mToDegrees - this.mFromDegrees);
        long duration = (long) (changedDegrees / SPEED_ROTATION);

        if (MAX_DURATION < duration) {
            duration = MAX_DURATION;
        } else if (MIN_DURATION > duration) {
            duration = MIN_DURATION;
        }
        this.setDuration(duration);
    }

    public float getToDegrees() {
        return mToDegrees;
    }

    public float getCurrentDegree() {
        return mCurrentDegrees;
    }

    public float getConvertedCurrentDegree() {
        float curentDegrees = convertDegree(getCurrentDegree());
        return curentDegrees;
    }

    public static final float convertDegree(float degrees) {
        float convertedDegrees = degrees;
        if (convertedDegrees < 0) {
            convertedDegrees = 360 + degrees;
        } else if (convertedDegrees > 360) {
            convertedDegrees = degrees - 360;
        }
        if (convertedDegrees < 0 || convertedDegrees > 360) {
            convertDegree(convertedDegrees);
        }
        return convertedDegrees;
    }
}
