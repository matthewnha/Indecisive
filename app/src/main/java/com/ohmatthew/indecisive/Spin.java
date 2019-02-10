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

public class Spin extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spin);
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		
		
	}
	
	@Override protected void onResume(){
		super.onResume();
		Intent i = getIntent();
		String optString = i.getExtras().getString("options");
		String[] options = optString.substring(1, optString.length() - 1).split(", ");
		ArrayList<String> colors = i.getExtras().getStringArrayList("colors");
		ArrayList<Hashtable> colorsList = new ArrayList<Hashtable>();
		ListIterator li = colors.listIterator();
	    while(li.hasNext()){
	    	Hashtable ic = new Hashtable();
	    	ic.putAll((HashMap) (Object )li.next());
	    	colorsList.add(ic);
		}
		
		Log.v("colors: ", ""+colorsList.toString());
		//((TextView)findViewById(R.id.testSlot)).setText(options[(int)(Math.random() * options.length)]);
		
		ViewGroup layout = (ViewGroup)findViewById(R.id.PieChart);
		final PieChartView piechart = new PieChartView(this, options.length, colorsList);
		final float scale = getResources().getDisplayMetrics().density;
		int pixels = (int) (200 * scale + 0.5f);
		piechart.setLayoutParams(new LayoutParams(pixels, pixels));
		piechart.getLayoutParams().height = pixels;
		piechart.getLayoutParams().width = pixels;

		float randomDegree = 0;
		while((randomDegree % (360f/(float)options.length)) == 0){
			randomDegree = (float) (Math.random() * 360d);
		}
		final float fRandomDegree = randomDegree;
		

		final int winnerIndex = (int) ((360-randomDegree)/(360/options.length));
		Hashtable winner = colorsList.get(winnerIndex);
        final int winnerColor = (Integer) colorsList.get(winnerIndex).get("color");
		Log.v("Winner degree: ",""+randomDegree);
		Log.v("Winner index: ",""+winnerIndex);
		Log.v("Winner name: ",""+winner.get("value"));
		
	    Animation anim1 = new RotateAnimation(35.0f, 0.0f, pixels/2, pixels/2);
	    anim1.setDuration(1300);               // duration in ms
	    anim1.setRepeatCount(0);                // -1 = infinite repeated
	    anim1.setFillAfter(true);               // keep rotation after animation
	    piechart.setAnimation(anim1);
		
		final SpinAnimation anim2 = new SpinAnimation(0f, 3600f + randomDegree, pixels/2, pixels/2);
		anim2.setInterpolator(new LinearInterpolator());
		anim2.setRepeatCount(0);
		anim2.setDuration((long) (7000f + ((18f/35f)*randomDegree)));
		anim2.setFillEnabled(true);
		anim2.setFillAfter(true);
		
		final Animation anim3 = new Animation(){
			 @Override
			 protected void applyTransformation(float interpolatedTime, Transformation t) {
				 piechart.interpolatedTime = interpolatedTime;
				 piechart.invalidate();
			 }
		};
		anim3.setInterpolator(new LinearInterpolator());
		anim3.setRepeatCount(0);
		anim3.setDuration(450);
		anim3.setFillEnabled(true);
		anim3.setFillAfter(true);
		anim3.setStartOffset(1000);
		

		anim1.setAnimationListener(new AnimationListener(){
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				piechart.startAnimation(anim2);
			}

			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
		});
		anim2.setAnimationListener(new AnimationListener(){
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				piechart.expandWin = true;
				piechart.winnerIndex = winnerIndex;
				piechart.startAnimation(anim3);
				piechart.setRotation(fRandomDegree);
			}

			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
		});

        anim3.setAnimationListener(new AnimationListener(){
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(getBaseContext(), ChosenScreen.class);
                intent.putExtra("color", String.format("#%06X", 0xFFFFFF & winnerColor));
                //TODO pass name of winner to intent
                intent.putExtra("winner", "Winner here!!");
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }

            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }
        });
		
		layout.addView(piechart);
		piechart.startAnimation(anim1);
		
	}
}
