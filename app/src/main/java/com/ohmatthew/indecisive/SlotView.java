package com.ohmatthew.indecisive;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class SlotView extends ImageView{
	Context context;
	Boolean started = false;
	Resources res = context.getResources();
	Drawable slot_view = res.getDrawable(R.drawable.slot_view);
	public SlotView(Context _context) {
		super(_context);
		context = _context;
		this.setImageResource(R.drawable.slot_view);
	      
		
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		slot_view.draw(canvas);
		//super.onDraw(canvas);
		
	}
	
	@Override
	protected void onMeasure(int w, int h){
		int width = ((Activity)context).findViewById(R.id.SlotLayout).getMeasuredWidth();
		int height = ((Activity)context).findViewById(R.id.SlotLayout).getMeasuredHeight();
		setMeasuredDimension(width, height);
	      
	}
	
}