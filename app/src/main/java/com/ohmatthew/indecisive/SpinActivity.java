package com.ohmatthew.indecisive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;

import android.animation.AnimatorSet;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SpinActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin);
        ActionBar actionBar = getActionBar();
        actionBar.hide();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent i = getIntent();

        final ArrayList<String> choices = i.getExtras().getStringArrayList("choices");

        ArrayList<String> colors = i.getExtras().getStringArrayList("colors");

        ArrayList<Hashtable> colorsList = new ArrayList<Hashtable>();
        ListIterator li = colors.listIterator();
        while (li.hasNext()) {
            Hashtable ic = new Hashtable();
            ic.putAll((HashMap) (Object) li.next());
            colorsList.add(ic);
        }

        Log.v("colors: ", "" + colorsList.toString());

        //((TextView)findViewById(R.id.testSlot)).setText(options[(int)(Math.random() * choices.length)]);


        RelativeLayout layout = (RelativeLayout) findViewById(R.id.PieChart);
        layout.setHorizontalGravity(Gravity.AXIS_X_SHIFT);


        final SpinningWheelView wheelView = new SpinningWheelView(this, choices.size(), colorsList);
        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (200 * scale);
        wheelView.setLayoutParams(new LayoutParams(pixels, pixels));

        //Choose what angle that the wheel will end on
        float randomDegree = 0;
        randomDegree = (float) (Math.random() * 360d);

        //Make sure it doesn't end up in between slices
        float sliceDegreeSize = (float) (choices.size());
        if (randomDegree % (360f / sliceDegreeSize) == 0) {
            randomDegree += 5.0f;
        }
        final float finalAngle = randomDegree;


        final int winnerIndex = (int) ((360 - randomDegree) / (360 / (choices.size())));
        final Hashtable winner = colorsList.get(winnerIndex);
        final int winnerColor = (Integer) colorsList.get(winnerIndex).get("color");

        Log.v("Winner degree: ", "" + randomDegree);
        Log.v("Winner index: ", "" + winnerIndex);
        Log.v("Winner name: ", "" + winner.get("value"));

        //Preparing to spin animation
        //Animation anim1 = new RotateAnimation(35.0f, 0.0f, pixels / 2, pixels / 2);
        final Animation anim1 = new Animation() {
            private float mFromDegrees = 35.0f;
            private float mToDegrees = 0;

            private int mPivotXType = ABSOLUTE;
            private int mPivotYType = ABSOLUTE;
            private float mPivotXValue = 0.0f;
            private float mPivotYValue = 0.0f;

            private float mPivotX;
            private float mPivotY;

            float time = 0;

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                float degrees = mFromDegrees + ((mToDegrees - mFromDegrees) * interpolatedTime);
                //Log.v("anim", interpolatedTime+":"+(interpolatedTime-1));

                if(time < 1){
                    degrees = -1* (mToDegrees - mFromDegrees) *  (1 - interpolatedTime);
                } else {
                    degrees = mToDegrees - mFromDegrees;
                }

                time = interpolatedTime;
                float scale = getScaleFactor();

                wheelView.setAnimRotation(degrees);
                wheelView.invalidate();
            }
        };
        anim1.setDuration(1300);
        anim1.setRepeatCount(0);
        anim1.setFillAfter(true);
        wheelView.setAnimation(anim1);

        //Spin animation
        final Animation anim2 = new Animation() {
            private float mFromDegrees = 0f;
            private float mToDegrees = 3600f + finalAngle;

            private int mPivotXType = ABSOLUTE;
            private int mPivotYType = ABSOLUTE;
            private float mPivotXValue = 0.0f;
            private float mPivotYValue = 0.0f;

            private float mPivotX;
            private float mPivotY;

            float time = 0;

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

                wheelView.setAnimRotation(degrees);
                wheelView.invalidate();
            }
        };
        anim2.setInterpolator(new LinearInterpolator());
        anim2.setRepeatCount(0);
        anim2.setDuration((long) (7000f + ((18f / 35f) * randomDegree)));
        //anim2.setFillEnabled(true);
        //anim2.setFillAfter(true);


        final Animation anim3 = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                wheelView.setInterpolatedTime(interpolatedTime);
                wheelView.invalidate();
            }
        };

        anim3.setInterpolator(new LinearInterpolator());
        anim3.setRepeatCount(0);
        anim3.setDuration(450);
        anim3.setFillEnabled(true);
        anim3.setFillAfter(true);
        anim3.setStartOffset(1000);

        anim1.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                float animationRotation = wheelView.getAnimRotation();
                wheelView.setBaseRotation(animationRotation);
                wheelView.setAnimRotation(0f);
                wheelView.startAnimation(anim2);
            }

            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }
        });
        anim2.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                float animationRotation = wheelView.getAnimRotation();
                wheelView.setBaseRotation(animationRotation);
                wheelView.setAnimRotation(0f);
                wheelView.doWinChosenAnimation(true);
                wheelView.setWinnerIndex(winnerIndex);
                wheelView.startAnimation(anim3);
                //wheelView.setWheelRotation(finalAngle);
            }

            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }
        });
        anim3.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(getBaseContext(), ChosenActivity.class);
                intent.putExtra("color", String.format("#%06X", 0xFFFFFF & winnerColor));
                //TODO pass name of winner to intent
                intent.putExtra("winner", choices.get(winnerIndex));
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }

            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }
        });

        layout.addView(wheelView);
        wheelView.startAnimation(anim1);

    }
}
