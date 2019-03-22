package com.ohmatthew.indecisive;

import java.util.ArrayList;
import java.util.Hashtable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

public class SpinningWheelView extends View {
    private int slices = 2;
    private ArrayList<Hashtable> colorsList;

    private float interpolatedTime = 0;
    private Boolean doWinChosenAnimation = false;
    private Boolean doSpinAnimation = false;

    private int winnerIndex;

    private float baseRotation = 0;
    private float animRotation = 0;
    private RectF viewBounds;

    private Paint wheelStrokePaint;
    private Paint wheelPaint;
    private RectF wheelBounds;
    private int wheelSize;
    private int wheelRadius;
    private float wheelStrokeRatio = 1f/20f;

    private int arrowHeight;
    private int arrowWidth;
    private Path arrowPath;
    private Paint arrowPaint;

    public SpinningWheelView(Context context, int _slices) {
        super(context);
        slices = _slices;
        // TODO Auto-generated constructor stub
    }

    public SpinningWheelView(Context context, int _slices, ArrayList<Hashtable> colors) {
        super(context);
        slices = _slices;
        colorsList = colors;

        wheelStrokePaint = new Paint();
        wheelStrokePaint.setStyle(Paint.Style.STROKE);
        wheelStrokePaint.setColor(Color.parseColor("#000000"));
        wheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wheelPaint.setStyle(Paint.Style.FILL);
        wheelPaint.setColor(Color.parseColor("#D3D3D3"));
        arrowPaint = new Paint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewBounds = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
        Log.d("ViewBounds", viewBounds.toShortString());

        float arrowWScaleRelToWheel = 1f/4f;
        float arrowHScaleRelToWheel = 1f/8f;

        wheelSize = (int) (getMeasuredHeight() / (1f + arrowWScaleRelToWheel/2f));
        wheelRadius = wheelSize/2;
        wheelStrokePaint.setStrokeWidth(wheelSize * wheelStrokeRatio);

        float wheelPadding = wheelSize*arrowWScaleRelToWheel/2;

        wheelBounds = new RectF(0, wheelPadding/2, wheelSize, wheelSize + wheelPadding/2);
        Log.d("wheelBounds", wheelBounds.toShortString());

        arrowWidth = (int) (wheelSize * arrowWScaleRelToWheel);
        arrowHeight = (int) (wheelSize * arrowHScaleRelToWheel);

        arrowPath = new Path();
        arrowPath.setFillType(Path.FillType.EVEN_ODD);
        arrowPath.moveTo(wheelSize * (1 - arrowWScaleRelToWheel/2), wheelSize/2);
        arrowPath.lineTo(viewBounds.right, wheelRadius - arrowHeight/2);
        arrowPath.lineTo(viewBounds.right, wheelRadius + arrowHeight/2);
        arrowPath.lineTo(wheelSize * (1 - arrowWScaleRelToWheel/2), wheelSize/2);
        arrowPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (animRotation >= 360f || animRotation < 0f) {
            animRotation = animRotation % 360f;
        }

        if (colorsList != null) {
            // Draw the shadow
            canvas.drawOval(wheelBounds, wheelPaint);
            float intervals = slices;
            // Draw the pie slices

            if (doWinChosenAnimation) animRotation = 180f * interpolatedTime;

            for (int i = 0; i < intervals; i++) {
                Hashtable colorHT = colorsList.get(i);
                int color = (Integer) colorHT.get("color");
                wheelPaint.setColor(color);
                canvas.drawArc(wheelBounds,
                        baseRotation + animRotation + (360 / intervals) * i,
                        (360 / intervals),
                        true, wheelPaint);
            }

            if (doWinChosenAnimation) {
                Hashtable colorHT = colorsList.get(winnerIndex);
                int color = (Integer) colorHT.get("color");
                //Log.v("colorht: ",""+color);
                wheelPaint.setColor(color);
                float toGrow = 360 - (360 / intervals);
                float deltaAngle = toGrow * interpolatedTime;
                float startArc = baseRotation + animRotation + ((360 / intervals) * winnerIndex);
                canvas.drawArc(wheelBounds,
                        startArc,
                        (360 / intervals) + (deltaAngle),
                        true, wheelPaint);
            }
        }


        canvas.drawCircle(wheelBounds.left+wheelSize/2f, wheelBounds.top+wheelSize/2f, wheelRadius - (wheelSize * wheelStrokeRatio/2), wheelStrokePaint);

        arrowPaint.setStyle(Paint.Style.FILL);
        arrowPaint.setColor(Color.WHITE);
        canvas.drawPath(arrowPath, arrowPaint);

        super.onDraw(canvas);
    }

    public void setInterpolatedTime(float time) {
        interpolatedTime = time;
    }

    public float getInterpolatedTime() {
        return interpolatedTime;
    }

    public void setWinnerIndex(int index) {
        winnerIndex = index;
    }

    public void setBaseRotation(float degrees) {
        baseRotation = degrees;
    }

    public float getBasemRotation() {
        return baseRotation;
    }

    public void setAnimRotation(float degrees) {
        animRotation = degrees;
    }

    public float getAnimRotation() {
        return animRotation;
    }

    public void doSpinAnimation(boolean perform) {
        doSpinAnimation = perform;
    }

    public void doWinChosenAnimation(boolean perform) {
        doWinChosenAnimation = perform;
    }


}

