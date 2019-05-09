package com.ohmatthew.indecisive.activities.Spin;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class SpinAnimation extends Animation{
    private float mFromDegrees;
    private float mToDegrees;

    private int mPivotXType = ABSOLUTE;
    private int mPivotYType = ABSOLUTE;
    private float mPivotXValue = 0.0f;
    private float mPivotYValue = 0.0f;

    private float mPivotX;
    private float mPivotY;
    
    float time = 0;

    /**
     * Constructor used when a RotateAnimation is loaded from a resource.
     * 
     * @param context Application context to use
     * @param attrs Attribute set from which to read values
     */
    public SpinAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructor to use when building a RotateAnimation from code.
     * Default pivotX/pivotY point is (0,0).
     * 
     * @param fromDegrees Rotation offset to apply at the start of the
     *        animation.
     * 
     * @param toDegrees Rotation offset to apply at the end of the animation.
     */
    public SpinAnimation(float fromDegrees, float toDegrees, float pivotX, float pivotY) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mPivotX = pivotX;
        mPivotY = pivotY;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
    	float degrees = mFromDegrees + ((mToDegrees - mFromDegrees) * interpolatedTime);
    	//Log.v("anim", interpolatedTime+":"+(interpolatedTime-1));

    	if(time < 1){
    		degrees = ((-1*(mToDegrees - mFromDegrees)) *  (float)Math.pow(interpolatedTime-1,2)) + (mToDegrees - mFromDegrees);
    	} else {
    		degrees = mToDegrees - mFromDegrees;
    	}

    	time = interpolatedTime;
        float scale = getScaleFactor();
        
        if (mPivotX == 0.0f && mPivotY == 0.0f) {
            t.getMatrix().setRotate(degrees);
        } else {
            t.getMatrix().setRotate(degrees, mPivotX * scale, mPivotY * scale);
        }
    }
}
