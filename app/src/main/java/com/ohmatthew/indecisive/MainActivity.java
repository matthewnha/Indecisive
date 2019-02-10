package com.ohmatthew.indecisive;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;

import org.json.JSONArray;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Spannable;
import android.text.SpannableString;
import com.ohmatthew.indecisive.TypefaceSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends Activity {
	Menu _menu;
	ArrayList<String> list;
	ColoredArrayAdapter adapter;
	ArrayList ItemColors = new ArrayList<Hashtable>();
	String[] values = new String[] { "#EF9A9A", "#F48FB1", "#CE93D8",
            "#B39DDB", "#9FA8DA", "#90CAF9", "#81D4FA", "#80DEEA","#80CBC4","#A5D6A7", "#C5E1A5", "#E6EE9C","#FFE082","#FFCC80","#FFAB91"};
	  public class ItemColor{
		  String value;
		  int pos;
		  int color;
		  Boolean firstAppearance;
	  }
	  
	  public static final String PREFS_NAME = "IndecisivePrefsFile";
	  private DrawerLayout mDrawerLayout;
	  private ListView mDrawerList;
	  private ActionBarDrawerToggle mDrawerToggle;
	  private CharSequence mDrawerTitle;
	  private CharSequence mTitle;
	  InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ActionBar actionBar = getActionBar();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		actionBar.setCustomView(R.layout.actionbar);
        actionBar.setDisplayShowCustomEnabled(true);
        //actionBar.setDisplayShowHomeEnabled(false);
        //actionBar.setDisplayShowTitleEnabled(false);
        
        //actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_dehaze);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        
        
        final ListView listview = (ListView) findViewById(R.id.listview);
        
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String savedOptions = settings.getString("options", null);
        String[] values = null;
        if(!(savedOptions == null)){
        	//savedOptions = savedOptions.substring(1, savedOptions.length() - 1);
        	//values = savedOptions.split(", ");
        	values = splitString(savedOptions);
        	Log.v("Saved:", ""+savedOptions);
        	//values = new String[] { "Chipotle", "Kaju", "BCD", "Shabu", "Yoshinoya" };
        } else values = new String[] { "Chipotle", "Kaju", "BCD", "Shabu", "Yoshinoya" };

        list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
          list.add(values[i]);
        }
        adapter = new ColoredArrayAdapter(this,
            R.layout.list_item, list);
        listview.setAdapter(adapter);
        listview.setItemsCanFocus(true);
        adapter.setNotifyOnChange(true);
        
        View button_go = findViewById(R.id.button_go);
        button_go.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), Spin.class);
				JSONArray ItemColorsJson = new JSONArray(ItemColors);
				Log.v("J: ", ItemColorsJson.toString());
				intent.putExtra("options", getList());
				intent.putStringArrayListExtra("colors", ItemColors);
				startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                /*Intent intent = new Intent(getBaseContext(), ChosenScreen.class);
                intent.putExtra("color", "#F48FB1");
                intent.putExtra("winner", "swag");
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);*/
			}
        	
        });
        
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Black.ttf");
        //((TextView)findViewById(R.id.actionBarTitle)).setTypeface(type);
        
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        //mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mPlanetTitles));
        // Set the list's click listener
        //mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_dehaze, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        
	}
	
	public String getList(){
		return list.toString();
	}
	
	@Override
	protected void onStop(){
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("options", list.toString());
		editor.commit();
		super.onStop();
	}
	
	public String[] splitString(String s){
		//String s = l.toString();
		return s.substring(1, s.length() - 1).split(", ");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		_menu = menu;
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem addItem = menu.findItem(R.id.action_add);
		//addItem.getIcon().setColorFilter(Color.parseColor("#AAAAAA"), Mode.SRC_ATOP);
		((EditText)addItem.getActionView().findViewById(R.id.text_add)).setOnEditorActionListener(new OnEditorActionListener() {
		        	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        		addItem(v.getText().toString());
		        		v.setText("");
		                boolean handled = true;
		                /*if (actionId == EditorInfo.IME_ACTION_SEND) {
		                	addItem(v.getText().toString());
		                    handled = true;
		                }*/
		                return handled;
		            }
		        });
		return true;
	}
	
	public void addItem(final String s){
		final Runnable r = new Runnable(){
			public void run() {
				if(!list.contains(s) && (s != null) && (!s.isEmpty())){
					list.add(0, s);
				}
				adapter.notifyDataSetChanged();
			}
		
		};
		runOnUiThread(r);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		Log.v("id",""+id);
		if(mDrawerToggle.onOptionsItemSelected(item)){
			return true;
		}
		switch(id){
		case android.R.id.home:
			imm.hideSoftInputFromWindow(findViewById(R.id.text_add).getWindowToken(), 0);
	        return true;
		case R.id.action_add:
			item.getActionView().findViewById(R.id.text_add).requestFocus();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            Log.v("LIST: ",""+list.toString());
			return true;
		default:
			Log.v("id",""+id);
			return super.onOptionsItemSelected(item);
		}
		
	}
	
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_add).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
	
	public class ColoredArrayAdapter extends ArrayAdapter{
		Context context;
		
		public ColoredArrayAdapter(Context _context, int resource, List objects) {
			super(_context, resource, objects);
			context = _context;
			// TODO Auto-generated constructor stub
		}
		
		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
			  ItemRowView rowView = new ItemRowView(context, parent, position, (String)getItem(position));
			  return rowView;
		  }
		  
		  
		  @Override
		  public void notifyDataSetChanged(){
			  super.notifyDataSetChanged();
			  SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("options", list.toString());
				editor.commit();
		  }
	}
	
	public class ItemRowView extends RelativeLayout{
		int state = 0;
		String name;
		
		public ItemRowView(final Context context, ViewGroup parent, int position, String s) {
			super(context);
			name = s;	    
			inflate(context, R.layout.list_item, this);
		    Hashtable ic = getItemColor(s);
		    if(ic != null){
			    ((TextView) this.findViewById(R.id.item_name)).setText((String)ic.get("value"));
			    this.setBackgroundColor((Integer) ic.get("color"));
		    }
		    if((Boolean) ic.get("firstAppearance")) {
		    	Log.v("FIRSTAPPEARANCE",""+getBaseContext().toString());
		        Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_from_left);
		        setAnimation(animation);
	        }
		    this.findViewById(R.id.action_remove).setOnClickListener(new OnClickListener(){
				public void onClick(View _v) {
					Log.v("click", name);
					final ImageView v = (ImageView) _v;
					if(state == 0){
						v.setImageResource(R.drawable.ic_highlight_remove);
						state = 1;
					} else if(state == 1){
						v.setImageResource(R.drawable.ic_remove_circle_outline);
						state = 0;
						list.remove(name);
						ListIterator li = ItemColors.listIterator();
					    Boolean found = false;
					    Hashtable ic;
					    while(li.hasNext() && !found){
					    	ic = (Hashtable) li.next();
					    	if(ic.get("value").equals(name)){
					    		list.remove(ic);
					    		found = true;
					    	}
					    }
						adapter.notifyDataSetChanged();
					}
				}
		    	
		    });
		}
		  
		  Hashtable getItemColor(String s){
			  ListIterator li = ItemColors.listIterator();
			    Boolean found = false;
			    Hashtable ic;
			    while(li.hasNext() && !found){
			    	ic = (Hashtable) li.next();
			    	if(ic.get("value").equals(name)){
			    		ic.put("firstAppearance", false);
			    		return ic;
			    	}
			    }
		    	ic = new Hashtable();
		    	ic.put("value", name);
		    	//ic.put("pos", pos);
		    	ic.put("color", Color.parseColor(values[(int)(Math.random() * values.length)]));
			    double shade = 0.50 * Math.random();
			    Log.v("Red: ","Original: "+Color.red((Integer) ic.get("color"))+"Difference: "+(255 - Color.red((Integer) ic.get("color"))+", New: "+(Color.red((Integer) ic.get("color")) - (int)((255 - Color.red((Integer) ic.get("color"))) * shade))));
			    int red = (int)(Color.red((Integer) ic.get("color")) * (1- shade));
			    int green = (int)((Color.green((Integer) ic.get("color"))) * (1- shade));
			    int blue = (int)((Color.blue((Integer) ic.get("color"))) * (1- shade));
			    ic.put("color", Color.rgb(red,green,blue));
			    ic.put("firstAppearance", true);
			    ItemColors.add(ic);
			    return ic;
		  }
	}
}
