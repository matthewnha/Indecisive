package com.ohmatthew.indecisive.activities.Spin;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.TextView;

import com.ohmatthew.indecisive.activities.Result.ResultActivity;
import com.ohmatthew.indecisive.R;
import com.ohmatthew.indecisive.models.ChoiceSlice;

public class SpinActivity extends Activity {

    private int choicesCnt;
    private ArrayList<ChoiceSlice> receivedChoices;
    private static DisplayMetrics displayMetrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin);
        ActionBar actionBar = getActionBar();
        actionBar.hide();

        Log.v("SpinActivity", "onCreate()");
    }

    @Override
    protected void onResume() {
        Log.v("SpinActivity", "onResume()");
        super.onResume();
        Intent intent = getIntent();

        // Display metrics
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        receivedChoices = intent.getExtras().getParcelableArrayList("choices");
        choicesCnt = receivedChoices.size();

        TextView choiceText = findViewById(R.id.choice_text);
        final SpinningWheelView wheelView = initWheelView();

        doAnimation(wheelView, choiceText);
    }

    private float generateRandomAngle() {
        // Choose what angle that the wheel will end on
        float randomDegree = 0;
        randomDegree = (float) (Math.random() * 360d);

        // Make sure it doesn't end up in between slices
        float sliceDegreeSize = (float) (choicesCnt);
        if (randomDegree % (360f / sliceDegreeSize) == 0) {
            randomDegree += 5.0f;
        }

        return randomDegree;
    }

    private float[] calculateAngleBreakpoints(int slices) {
        float angleBreakpoints[] = new float[slices];
        for (int i = 0; i < slices; i++) {
            angleBreakpoints[i] = 360f - (360f / slices) * i;
            Log.v("SpinActivity", "Angle breakpoint ["+i+"] = "+angleBreakpoints[i]);
        }

        return angleBreakpoints;
    }

    private ChoiceSlice getChoiceAtAngle(float degree, float[] breakpoints) {
        int currIndexTest = choicesCnt - 1;
        boolean found = false;

        while (currIndexTest > 0 && !found) {
            if (degree < breakpoints[currIndexTest]) {
                Log.v("SpinActivity", degree+"<"+breakpoints[currIndexTest]);
                found = true;
            } else {
                Log.v("SpinActivity", degree+">"+breakpoints[currIndexTest]);
                currIndexTest--;
            }
        }

        return receivedChoices.get(currIndexTest);
    }

    private SpinningWheelView initWheelView() {
        final SpinningWheelView wheelView = findViewById(R.id.wheel_view);
        ConstraintLayout.LayoutParams wheelViewLayoutParams = (ConstraintLayout.LayoutParams) wheelView.getLayoutParams();
        int wheelViewSize = (int) (displayMetrics.widthPixels * wheelViewLayoutParams.matchConstraintPercentWidth);

        // Move wheel view
        Guideline wheelViewTopGuideline = findViewById(R.id.top_wheel_guideline);
        wheelViewTopGuideline.setGuidelineEnd(wheelViewSize/2);
        Log.v("SpinActivity", "width: " + displayMetrics.widthPixels);

        // Feed choices into wheel
        wheelView.initSlices(choicesCnt, receivedChoices);

        return wheelView;
    }

    private void doAnimation(final SpinningWheelView wheelView, final TextView choiceText) {

        final float finalAngle = generateRandomAngle();

        final float angleBreakpoints[] = calculateAngleBreakpoints(choicesCnt);
        final ChoiceSlice winner = getChoiceAtAngle(finalAngle, angleBreakpoints);

        final int winnerColor = (Integer) winner.getColor();

        //Preparing to spin animation
        final Animation anim1 = new Animation() {
            private float mFromDegrees = 35.0f;
            private float mToDegrees = 0;
            float time = 0;

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                float degrees = mFromDegrees + ((mToDegrees - mFromDegrees) * interpolatedTime);

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
            private float mToDegrees = 3600f + finalAngle;
            float time = 0;

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                float degrees = (mToDegrees * interpolatedTime);

                if(time < 1){
                    degrees = ( (-1 * mToDegrees) *  (float)Math.pow(interpolatedTime-1,2)) + mToDegrees;
                } else {
                    degrees = mToDegrees;
                }

                time = interpolatedTime;

                wheelView.setAnimRotation(degrees);
                wheelView.invalidate();

                Log.v("SpinActivity", "curr "+degrees % 360);
                String currentName = getChoiceAtAngle(degrees % 360, angleBreakpoints).getName();
                choiceText.setText(currentName);
            }
        };

        anim2.setInterpolator(new LinearInterpolator());
        anim2.setRepeatCount(0);
        anim2.setDuration((long) (7000f + ((18f / 35f) * finalAngle)));
        anim2.setFillEnabled(true);
        anim2.setFillAfter(true);

        anim1.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                float animationRotation = wheelView.getAnimRotation();
                float baseRotation = wheelView.getBaseRotation();
                wheelView.setBaseRotation(baseRotation+animationRotation);
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

                Intent intent = new Intent(getBaseContext(), ResultActivity.class);
                intent.putExtra("color", String.format("#%06X", 0xFFFFFF & winnerColor));
                //TODO pass name of winner to intent
                intent.putExtra("winner", winner);

                final Intent fIntent = intent;

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //startActivity(fIntent);
                        finish();
                    }
                }, 950);

            }

            public void onAnimationRepeat(Animation animation) {
                // Do nothing
            }
        });

        wheelView.startAnimation(anim1);
    }
}
