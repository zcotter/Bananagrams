package com.zachcotter.bananagrams;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.Toast;

public class Settings extends Activity implements OnClickListener {

  public static final String SETTINGS_KEY = "BananagramsSettings";
  public static final String MUSIC_KEY = "MusicEnabled";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.bananagrams_settings);

    findViewById(R.id.settings_close_button).setOnClickListener(this);
    findViewById(R.id.reset_tutorial_button).setOnClickListener(this);

    CheckBox musicEnabled = (CheckBox) findViewById(R.id.background_music_check);
    musicEnabled.setOnClickListener(this);
    SharedPreferences prefs = getSharedPreferences(SETTINGS_KEY,
                                                   Activity.MODE_PRIVATE);
    musicEnabled.setChecked(prefs.getBoolean(MUSIC_KEY,
                                             true));

    if (android.os.Build.VERSION.SDK_INT > 10){
      ActionBar actionBar = getActionBar();
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  @Override
  public void onClick(View view) {
    switch(view.getId()) {
      case R.id.reset_tutorial_button:
        resetTutorial();
        return;
      case R.id.settings_close_button:
        finish();
        return;
      case R.id.background_music_check:
        boolean checked = ((CheckBox) view).isChecked();
        adjustBackgroundMusic(checked);
        return;
    }
  }

  private void adjustBackgroundMusic(boolean musicOn) {
    SharedPreferences prefs = getSharedPreferences(SETTINGS_KEY,
                                                   Activity.MODE_PRIVATE);
    Editor prefsEditor = prefs.edit();
    prefsEditor.putBoolean(MUSIC_KEY,
                           musicOn);
    prefsEditor.commit();
  }

  private void resetTutorial() {
    SharedPreferences prefs = getSharedPreferences(Tutor.TUTORIAL_PREFERENCES_KEY,
                                                   Activity.MODE_PRIVATE);
    Editor prefsEditor = prefs.edit();
    prefsEditor.clear();
    prefsEditor.commit();
    Toast.makeText(this,
                   "Tutorial Reset",
                   Toast.LENGTH_SHORT).show();
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    finish();
    return true;
  }
}
