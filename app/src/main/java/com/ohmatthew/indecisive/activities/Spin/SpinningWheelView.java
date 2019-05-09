package com.ohmatthew.indecisive.activities.Spin;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ohmatthew.indecisive.models.ChoiceSlice;

public class SpinningWheelView extends View {
    final float wheelStrokeRatio = 0.025f;
    final float deltaOverlap = 1f; //Overlap slices slightly to cover bad draws.

    private Boolean slicesInitialized = false;

    private int slices = 2;
    private ArrayList<ChoiceSlice> colorsList;

    private float interpolatedTime = 0;
    private Boolean doWinChosenAnimation = false;
    private Boolean doSpinAnimation = false;

    private int winnerIndex;

    private float baseRotation = 270; //First item is at the top, to right of spinner and goes clockwise
    private float animRotation = 0;
    private RectF viewBounds;

    private Paint wheelPaint;
    private RectF wheelBounds;
    private int wheelSize;
    private int wheelRadius;

    private Paint whitenPaint;
    private Paint whitePaint;

    private Paint wheelStrokePaint;
    private float wheelStrokeWidth;

    public SpinningWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        wheelStrokePaint = new Paint();
        wheelStrokePaint.setStyle(Paint.Style.STROKE);
        wheelStrokePaint.setColor(Color.parseColor("#191919"));

        wheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wheelPaint.setStyle(Paint.Style.FILL);
        wheelPaint.setColor(Color.parseColor("#D3D3D3"));

        whitenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitenPaint.setStyle(Paint.Style.FILL);
        whitenPaint.setColor(Color.WHITE);
        whitenPaint.setAlpha(10);

        whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitePaint.setStyle(Paint.Style.FILL);
        whitePaint.setColor(Color.WHITE);
    }

    public void initSlices(int _slices, ArrayList<ChoiceSlice> choices) {
        slices = _slices;
        colorsList = choices;

        slicesInitialized = true;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewBounds = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
        Log.d("ViewBounds", viewBounds.toShortString());

        wheelSize = (int) getMeasuredHeight();
        wheelRadius = wheelSize/2;
        wheelStrokeWidth = wheelSize * wheelStrokeRatio;
        wheelStrokePaint.setStrokeWidth(wheelStrokeWidth);

        float wheelSlicesInset = wheelStrokeWidth/2f;
        wheelBounds = new RectF(wheelSlicesInset, wheelSlicesInset, wheelSize - wheelSlicesInset, wheelSize - wheelSlicesInset);
        Log.d("wheelBounds", wheelBounds.toShortString());


    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!slicesInitialized) {
            return;
        }

        if (animRotation >= 360f || animRotation < 0f) {
            animRotation = animRotation % 360f;
        }

        if (colorsList != null) {
            float intervals = slices;
            // Draw the pie slices

            //if (doWinChosenAnimation) animRotation = 180f * interpolatedTime;

            for (int i = 0; i < intervals; i++) {
                int color = colorsList.get(i).getColor();
                wheelPaint.setColor(color);
                canvas.drawArc(wheelBounds,
                        baseRotation + animRotation + (360 / intervals) * i,
                        (360 / intervals) + deltaOverlap,
                        true, wheelPaint);
            }
        }


        //stroke
        canvas.drawCircle(wheelSize/2f, wheelSize/2f, wheelRadius - (wheelStrokeWidth/2), wheelStrokePaint);

        //whiten layers
        canvas.drawCircle(wheelSize/2f, wheelSize/2f, wheelRadius * 0.75f, whitenPaint);
        canvas.drawCircle(wheelSize/2f, wheelSize/2f, wheelRadius * 0.5f, whitenPaint);

        //White Center
        canvas.drawCircle(wheelSize/2f, wheelSize/2f, wheelRadius * 0.3f, whitePaint);

        super.onDraw(canvas);
    }

    public void setBaseRotation(float degrees) {
        baseRotation = degrees;
    }

    public float getBaseRotation() {
        return baseRotation;
    }

    public void setAnimRotation(float degrees) {
        animRotation = degrees;
    }

    public float getAnimRotation() {
        return animRotation;
    }


}

