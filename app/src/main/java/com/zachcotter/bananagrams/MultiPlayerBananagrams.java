package com.zachcotter.bananagrams;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

public class MultiPlayerBananagrams extends Bananagrams {

  private boolean observerMode;
  private TextView itAintYoTurnView;
  public static final String OBSERVER_MODE_KEY = "ObserverMode";
  private Integer gameID;

  public static final String RECEIVE_MOVES = "receivemoves";
  private NewMoveBroadcastReceiver broadcastReceiver;
  private boolean active;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    active = true;
    setContentView(R.layout.multi_player_bananagrams);
    super.onCreate(savedInstanceState);
    itAintYoTurnView = (TextView) findViewById(R.id.not_yo_turn);

  }

  @Override
  protected void onResume() {
    active = true;
    super.onResume();
    LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(RECEIVE_MOVES);
    broadcastReceiver = new NewMoveBroadcastReceiver();
    manager.registerReceiver(broadcastReceiver,
                             intentFilter);
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    if(hasFocus){
      active = true;
    }
    else{
      active = false;
    }
  }

  @Override
  protected void onStop() {
    active = false;
    try {
      unregisterReceiver(broadcastReceiver);
    }
    catch(IllegalArgumentException e) {
      e.printStackTrace();
    }
    super.onStop();
  }

  @Override
  protected void onPause() {
    active = false;
    super.onPause();
  }

  private void reactToMoves(int gameID) {
    FetchMovesTask fetcher = new FetchMovesTask();
    fetcher.initialize(this,
                       this);

    try {
      Map<Integer, Move> moves = fetcher.execute(gameID).get();
      for(Entry<Integer, Move> move : moves.entrySet()) {
        DestroyMoveTask destroyer = new DestroyMoveTask();
        destroyer.setContext(this);
        if(destroyer.execute(move.getKey()).get()) {
          AbstractMove m = (AbstractMove) move.getValue();
          m.shouldPost = false;
          m.makeMove();
          switchToNormalMode();
        }
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

  @Override
  protected void initialize() {
    super.initialize();
    if(getIntent().getBooleanExtra(OBSERVER_MODE_KEY,
                                   true)) {
      switchToObserverMode();
    }
    else {
      switchToNormalMode();
    }

  }

  @Override
  protected void reinitialize(Bundle savedInstanceState) {
    super.reinitialize(savedInstanceState);
    this.observerMode = savedInstanceState.getBoolean(OBSERVER_MODE_KEY);
    if(observerMode) {
      switchToObserverMode();
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(OBSERVER_MODE_KEY,
                        observerMode);
  }

  @Override
  protected void onMove(Move move,
                        boolean localPlayersTurn) {
    if(localPlayersTurn) {
      switchToObserverMode();
    }
    else {
      switchToNormalMode();
    }
  }

  public void switchToObserverMode() {
    observerMode = true;
    list.setVisibility(View.GONE);
    findViewById(R.id.not_yo_turn).setVisibility(View.VISIBLE);
  }

  private void switchToNormalMode() {
    observerMode = false;
    list.setVisibility(View.VISIBLE);
    findViewById(R.id.not_yo_turn).setVisibility(View.GONE);
  }

  @Override
  public boolean isMultiPlayer() {
    return true;
  }

  @Override
  public boolean isPOC() {
    throw new RuntimeException("Multiplayer bananagrams cannot be in POC mode");
  }

  @Override
  public boolean onTouch(View view,
                         MotionEvent motionEvent) {
    if(!observerMode) {
      return super.onTouch(view,
                           motionEvent);
    }
    return false;
  }

  @Override
  protected void onNewIntent(Intent intent) {
    int gameID = intent.getIntExtra(Network.GAME_ID_KEY, -1);
    if(gameID != -1){
      reactToMoves(gameID);
    }
  }

  private class NewMoveBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context,
                          Intent intent) {
      if(intent.getAction().equals(RECEIVE_MOVES)) {
        int gameID = intent.getIntExtra(Network.GAME_ID_KEY,
                                        0);
        if(active){
          reactToMoves(gameID);
        }
        else{
          NotificationManager notificationManager = (NotificationManager) getSystemService(Context
                                                                                             .NOTIFICATION_SERVICE);
          Intent nextIntent = new Intent(getApplicationContext(), MultiPlayerBananagrams.class);

          nextIntent.putExtra(Network.GAME_ID_KEY, gameID);
          nextIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
          PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                                                  1,
                                                                  nextIntent,
                                                                  PendingIntent.FLAG_UPDATE_CURRENT
                                                                    | PendingIntent.FLAG_ONE_SHOT);
          Builder builder = new Builder(getApplicationContext());
          builder.setContentTitle("Bananagrams Move");
          String message = "Your opponent has made a move";
          builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
          builder.setAutoCancel(true);
          builder.setContentText(message);
          builder.setSmallIcon(R.drawable.b); //unbelievable that this won't work w/out icon and that fact was not
          // documented
          builder.setContentIntent(pendingIntent);
          notificationManager.notify(1,
                                     builder.build());
        }
      }
    }
  }
}
