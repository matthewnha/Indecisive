package com.ohmatthew.indecisive;

import java.util.ArrayList;
import java.util.Hashtable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

public class PieChartView extends View{
	int slices = 2;
	ArrayList<Hashtable> colorsList;
	Boolean expandWin = false;
	float interpolatedTime = 0;
	int winnerIndex;
	float start = 0;
	RectF bounds;
	
	public PieChartView(Context context, int _slices) {
		super(context);
		slices = _slices;
		// TODO Auto-generated constructor stub
	}
	
	public PieChartView(Context context, int _slices, ArrayList<Hashtable> colors) {
		super(context);
		slices = _slices;
		colorsList = colors;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		bounds = new RectF(0,0,getMeasuredWidth(),getMeasuredHeight());
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		if(start >= 360f || start < 0f){
			start = start % 360f;
		}
		if(colorsList != null){
		   Paint mPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		   mPiePaint.setStyle(Paint.Style.FILL);
		   mPiePaint.setColor(Color.parseColor("#D3D3D3"));
			// Draw the shadow
			canvas.drawOval(bounds, mPiePaint);
			float intervals = slices;
			// Draw the pie slices
			String[] values = new String[] { "FE6B72", "FD5760", "FB4C55",
		            "D33E46", "903136", "FFCD6B", "FFC658", "FFC34D", "D7A33F", 
		            "937232", "739CF1", "5981D7", "4367B3", "35528F", "283A62", 
		            "75F768", "5FE951", "4FD641", "40B034", "307829", "FFDA6B", 
		            "FFD558", "FFD24D", "D6B13F", "937A32", "E4FE6B", "DFFB57", 
		            "DAF74B", "B6CF3D", "7D8E30", "5065F0", "D54ADF", "AF35AF", 
		            "8B298B"};

			if(expandWin) start = 180f*interpolatedTime;
				
		   for (int i = 0; i < intervals; i++) {
			   Hashtable colorHT = colorsList.get(i); 
			   int color = (Integer) colorHT.get("color");
			   //Log.v("colorht: ",""+color);
			   mPiePaint.setColor(color);
		       canvas.drawArc(bounds,
		               start+(360/intervals)*i,
		               (360/intervals),
		               true, mPiePaint);
		   }
		   
			if(expandWin) {
				Hashtable colorHT = colorsList.get(winnerIndex); 
				   int color = (Integer) colorHT.get("color");
				   //Log.v("colorht: ",""+color);
				   mPiePaint.setColor(color);
				   float toGrow = 360 - (360/intervals);
				   float deltaAngle = toGrow * interpolatedTime;
				   float startArc = start+((360/intervals)*winnerIndex);
				   Log.v("startArc", ""+startArc);
			       canvas.drawArc(bounds,
			              startArc,
			               (360/intervals) + (deltaAngle),
			               true, mPiePaint);
			}
		}

		super.onDraw(canvas);
	}
}

