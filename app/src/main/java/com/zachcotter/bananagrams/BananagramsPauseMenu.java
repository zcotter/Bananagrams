package com.zachcotter.bananagrams;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class BananagramsPauseMenu extends Activity implements OnClickListener {

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.bananagrams_pause);

    findViewById(R.id.resume_bananagrams_button).setOnClickListener(this);
    findViewById(R.id.bananagrams_pause_quit).setOnClickListener(this);
  }


  public void onClick(View view) {
    switch(view.getId()) {
      case R.id.resume_bananagrams_button:
        Intent j = new Intent(this,
                              SinglePlayerBananagrams.class);
        j.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(j);
        finish();
        break;
      case R.id.bananagrams_pause_quit:
        startActivity(new Intent(this,
                                 BananagramsMenu.class));
        setResult(RESULT_OK, null);
        finish();
        break;
    }
  }
}
