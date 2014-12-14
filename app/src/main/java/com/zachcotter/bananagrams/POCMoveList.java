package com.zachcotter.bananagrams;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class POCMoveList extends Activity {

  public static final String RECEIVE_MOVES = "receivemoves";
  private NewMoveBroadcastReceiver broadcastReceiver;
  private TextView text;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.poc_bananagrams);
    text = (TextView) findViewById(R.id.poc_text_view);
    if(android.os.Build.VERSION.SDK_INT > 10) {
      ActionBar actionBar = getActionBar();
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(RECEIVE_MOVES);
    broadcastReceiver = new NewMoveBroadcastReceiver();
    manager.registerReceiver(broadcastReceiver,
                             intentFilter);
  }


  @Override
  protected void onPause() {
    super.onPause();
    try {
      unregisterReceiver(broadcastReceiver);
    }
    catch(IllegalArgumentException e) {
      e.printStackTrace();
    }
  }

  private void reactToMoves(int gameID) {
    LoadMakeAndDestroyMovesTask task = new LoadMakeAndDestroyMovesTask();
    task.initialize(null,
                    this);
    try {
      Map<Integer, Move> moves = task.execute(gameID).get();
      for(Move move : moves.values()) {
        text.setText(text.getText() + "\n" + move.toString());
        text.invalidate();
      }
      return;
    }
    catch(InterruptedException e) {
      e.printStackTrace();
    }
    catch(ExecutionException e) {
      e.printStackTrace();
    }
    throw new RuntimeException();
  }

  private class NewMoveBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context,
                          Intent intent) {
      if(intent.getAction().equals(RECEIVE_MOVES)) {
        int gameID = intent.getIntExtra(Network.GAME_ID_KEY,
                                        0);
        reactToMoves(gameID);
      }
    }
  }
}
