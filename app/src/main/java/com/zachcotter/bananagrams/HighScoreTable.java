package com.zachcotter.bananagrams;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

public class HighScoreTable extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.high_score_table);

    final SharedPreferences prefs = getSharedPreferences(Network.BANAGRAMS_SERVER_PREFS,
                                                         Activity.MODE_PRIVATE);
    final int playerID = prefs.getInt(Network.PLAYER_ID_KEY,
                                      -1);

    WebView webView = (WebView) findViewById(R.id.high_score_table);
    String url = "http://www.zachcotter.com:82/bananagrams_scores";
    if(playerID != -1){
      url += "?current_player_id=" + playerID;
    }
    webView.loadUrl(url);
    //Show the back arrow in the Action Bar
    if (android.os.Build.VERSION.SDK_INT > 10){
      ActionBar actionBar = getActionBar();
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    Intent i = new Intent(this,
                          BananagramsMenu.class);
    startActivity(i);
    finish();
    return true;
  }
}
