package com.ohmatthew.indecisive;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class Roll extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roll);
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		
		Intent i = getIntent();
		String optString = i.getExtras().getString("options");
		String[] options = optString.substring(1, optString.length() - 1).split(", ");
		((TextView)findViewById(R.id.testSlot)).setText(options[(int)(Math.random() * options.length)]);
		ViewGroup layout = (ViewGroup)findViewById(R.id.SlotLayout);
		//layout.addView(new SlotView(this));
	}
	
}
