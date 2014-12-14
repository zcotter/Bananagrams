package com.zachcotter.bananagrams;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.Random;

public class Communication extends Activity implements OnClickListener {

  @TargetApi(VERSION_CODES.HONEYCOMB)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.communication);

    findViewById(R.id.post_random_score_button).setOnClickListener(this);
    findViewById(R.id.view_high_scores_button).setOnClickListener(this);
    findViewById(R.id.view_players_button).setOnClickListener(this);

    //Show the back arrow in the Action Bar
    if (android.os.Build.VERSION.SDK_INT > 10){
      ActionBar actionBar = getActionBar();
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  public void onClick(View view) {

    switch(view.getId()) {
      case R.id.post_random_score_button:
        HighScore.postHighScore(new Random().nextInt(1000),
                                this);
        return;
      case R.id.view_high_scores_button:
        Intent i = new Intent(this,
                              HighScoreTable.class);
        startActivity(i);
        finish();
        return;
      case R.id.view_players_button:
        Intent j = new Intent(this,
                              ChoosePlayer.class);
        j.putExtra(GCMIntentService.PROOF_OF_CONCEPT, true);
        startActivity(j);
        finish();
        return;
    }
  }
}
