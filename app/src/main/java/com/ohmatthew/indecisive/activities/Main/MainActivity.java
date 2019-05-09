package com.ohmatthew.indecisive.activities.Main;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.ohmatthew.indecisive.models.ChoiceSlice;
import com.ohmatthew.indecisive.R;
import com.ohmatthew.indecisive.activities.Spin.SpinActivity;

public class MainActivity extends Activity implements ActivityInterface {
    Menu _menu;
    ArrayList<ChoiceSlice> choicesList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ChoicesRecyclerViewAdapter choicesAdapter;
    //String[] baseColors = new String[]{"#EF9A9A", "#F48FB1", "#CE93D8",
    //      "#B39DDB", "#9FA8DA", "#90CAF9", "#81D4FA", "#80DEEA", "#80CBC4", "#A5D6A7", "#C5E1A5", "#E6EE9C", "#FFE082", "#FFCC80", "#FFAB91"};
    String[] baseColors = new String[]{"#fcc360", "#61ff8e", "#6088fb", "#7061ff", "#e060fb", "#ff6161"};
    int currBaseColorIdx = -1;
    DisplayMetrics displayMetrics;

    public class ItemColor {
        String value;
        int pos;
        int color;
        Boolean firstAppearance;
    }

    public static final String PREFS_NAME = "IndecisivePrefsFile";
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getActionBar();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        displayMetrics = this.getResources().getDisplayMetrics();

        actionBar.setCustomView(R.layout.action_bar_main);
        actionBar.setDisplayShowCustomEnabled(true);

        //Load saved choices from shared pref
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String choicesString = settings.getString("choices", null);
        String[] optionNames = null;

        if (choicesString != null) {
            Log.v("MainActivity", "choicesString from pref isn't empty. "+choicesString);
            JSONArray savedChoicesJson = null;
            try {
                savedChoicesJson = new JSONArray(choicesString);
                optionNames = new String[savedChoicesJson.length()];
                for (int i = 0; i < savedChoicesJson.length(); i++) {
                    optionNames[i] = savedChoicesJson.getString(i);
                }
            } catch (Exception e) {
                Log.v("MainActivity", "Couldn't parse shared prefs", e);
            }
        }

        if (optionNames == null) {
            Log.v("MainActivity", "optionNames null");
            optionNames = new String[]{"Chipotle", "Kaju", "BCD", "Shabu", "Yoshinoya"};
        }

        Log.v("MainActivity", "optionNames size "+optionNames.length);

        //Transform choices into ChoiceSlice objects with generated colors
        choicesList = new ArrayList<ChoiceSlice>();
        for (int i = 0; i < optionNames.length; ++i) {
            choicesList.add(new ChoiceSlice(optionNames[i], generateColor()));
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.divider_recycler_view_transparent));
        recyclerView.addItemDecoration(dividerItemDecoration);

        choicesAdapter = new ChoicesRecyclerViewAdapter(choicesList, this);
        recyclerView.setAdapter(choicesAdapter);

        View button_go = findViewById(R.id.button_go);
        button_go.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SpinActivity.class);
                intent.putParcelableArrayListExtra("choices", choicesList);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }

        });

        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Black.ttf");

    }

    protected int generateColor(){
        //int randomBaseColor = Color.parseColor(baseColors[(int) (Math.random() * baseColors.length) % baseColors.length)]);
        int baseColor;
        if (currBaseColorIdx == -1) {
            currBaseColorIdx = (int) (Math.random() * (baseColors.length)) % (baseColors.length - 1);
        } else {
            currBaseColorIdx = (currBaseColorIdx+1) % (baseColors.length - 1);
        }

        baseColor = Color.parseColor(baseColors[currBaseColorIdx]);

        /*double shade = 0.50 * Math.random();
        int red = (int) (Color.red((Integer) randomBaseColor) * (1 - shade));
        int green = (int) ((Color.green((Integer) randomBaseColor)) * (1 - shade));
        int blue = (int) ((Color.blue((Integer) randomBaseColor)) * (1 - shade));

        return Color.rgb(red, green, blue);*/
        float[] hsv = new float[3];
        Color.colorToHSV(baseColor, hsv);
        Log.v("MainActivity", "Original Hue:"+hsv[0]);
        float hueShift = (float) (60f * Math.random()) - 30;
        hsv[0] = hsv[0] + hueShift;
        Log.v("MainActivity", "Altered Hue:"+hsv[0]);

        return Color.HSVToColor(hsv);

    }

    @Override
    protected void onStop() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        JSONArray choicesJson = new JSONArray();
        for (int i = 0; i < choicesList.size(); i++){
            choicesJson.put(choicesList.get(i).getName());
        }
        editor.putString("choices", choicesJson.toString());
        editor.commit();
        super.onStop();
    }

    public void addItemToChoices(final String s) {
        final Runnable r = new Runnable() {
            public void run() {
                boolean found = false;
                for (int i = 0; i < choicesList.size(); i++){
                    if (s.equals(choicesList.get(i).getName())) {
                        found = true;
                        return;
                    }
                }

                choicesList.add(new ChoiceSlice(s, generateColor()));
                choicesAdapter.notifyItemInserted(choicesList.size()-1);
            }

        };
        runOnUiThread(r);

    }

    public void removeChoice(int pos) {
        choicesList.remove(pos);
        choicesAdapter.notifyItemRemoved(pos);
        choicesAdapter.notifyItemRangeChanged(pos, choicesList.size() - pos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        _menu = menu;
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        MenuItem addItem = menu.findItem(R.id.action_add);
        //addItem.getIcon().setColorFilter(Color.parseColor("#AAAAAA"), Mode.SRC_ATOP);
        ((EditText) addItem.getActionView().findViewById(R.id.text_add)).setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addItemToChoices(v.getText().toString());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.v("id", "" + id);
        switch (id) {
            case android.R.id.home:
                imm.hideSoftInputFromWindow(findViewById(R.id.text_add).getWindowToken(), 0);
                return true;
            case R.id.action_add:
                item.getActionView().findViewById(R.id.text_add).requestFocus();
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                return true;
            default:
                Log.v("id", "" + id);
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //menu.findItem(R.id.action_add).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    public String[] splitString(String s) {
        return s.substring(1, s.length() - 1).split(", ");
    }
}
