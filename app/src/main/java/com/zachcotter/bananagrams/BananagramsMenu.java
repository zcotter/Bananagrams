package com.zachcotter.bananagrams;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class BananagramsMenu extends Activity implements OnClickListener {

  private static final int GAME_OVER_REQUEST_CODE = 1;
  private static final int GAME_ID_REQUEST_CODE = 2;

  private TextView gameOverView;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.bananagrams_menu);

    gameOverView = (TextView) findViewById(R.id.game_over_view);

    findViewById(R.id.new_bananagrams_button).setOnClickListener(this);
    findViewById(R.id.bananagrams_menu_quit).setOnClickListener(this);
    findViewById(R.id.bananagrams_menu_settings_button).setOnClickListener(this);
    findViewById(R.id.bananagrams_acknowledgements_button).setOnClickListener(this);
    findViewById(R.id.multiplayer_button).setOnClickListener(this);

    if (android.os.Build.VERSION.SDK_INT > 10){
      ActionBar actionBar = getActionBar();
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  @Override
  public void onClick(View view) {
    switch(view.getId()) {
      case R.id.new_bananagrams_button:
        startActivityForResult(new Intent(this,
                                          SinglePlayerBananagrams.class),
                               GAME_OVER_REQUEST_CODE);
        return;
      case R.id.bananagrams_menu_settings_button:
        startActivityForResult(new Intent(this,
                                          Settings.class),
                               1);
        return;
      case R.id.multiplayer_button:
        Intent intent = new Intent(this,
                                   ChoosePlayer.class);
        intent.putExtra(GCMIntentService.PROOF_OF_CONCEPT,
                        false);
        startActivityForResult(intent,
                               GAME_ID_REQUEST_CODE);

        return;
    }
  }

  @Override
  protected void onActivityResult(int requestCode,
                                  int resultCode,
                                  Intent data) {
    if(resultCode == RESULT_OK) {
      switch(requestCode) {
        case GAME_OVER_REQUEST_CODE:
          String score = data.getStringExtra(Bananagrams.SCORE_KEY);
          gameOverView.setText("GAME OVER\nYour score: " + score);
          gameOverView.invalidate();
          HighScore.postHighScore(Integer.parseInt(score), this);
          return;
        case GAME_ID_REQUEST_CODE:
          Intent intent = new Intent(this, MultiPlayerBananagrams.class);
          intent.putExtra(Network.GAME_ID_KEY, data.getIntExtra(Network.GAME_ID_KEY, -1));
          startActivityForResult(intent,
                                 GAME_OVER_REQUEST_CODE);
          return;
      }
    }
  }




}
