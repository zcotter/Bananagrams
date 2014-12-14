package com.zachcotter.bananagrams;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

  //should incoming moves be routed to the proof of concept activity or the real one
  //"breaking changes" like this wouldn't be a problem if assignments were in different apps
  public static final String PROOF_OF_CONCEPT = "poc";

  public GCMIntentService() {
    super(Network.GCM_SENDER_ID);
  }

  @Override
  protected void onMessage(Context context,
                           Intent data) {

    String messageType = data.getStringExtra("type");
    if(messageType.equals("GameInvitation")) {
      int gameId = Integer.parseInt(data.getStringExtra("game_id"));
      String requester = data.getStringExtra("requester");

      boolean poc = Boolean.parseBoolean(data.getStringExtra(PROOF_OF_CONCEPT));
      NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context
                                                                                              .NOTIFICATION_SERVICE);
      Intent intent;
      if(poc) {
        intent = new Intent(this,
                            SinglePlayerBananagrams.class);
      }
      else {
        intent = new Intent(this,
                            MultiPlayerBananagrams.class);
      }
      intent.putExtra(Network.GAME_ID_KEY,
                      gameId);
      intent.putExtra(PROOF_OF_CONCEPT,
                      poc);
      intent.putExtra(MultiPlayerBananagrams.OBSERVER_MODE_KEY, false);
      PendingIntent pendingIntent = PendingIntent.getActivity(this,
                                                              1,
                                                              intent,
                                                              PendingIntent.FLAG_UPDATE_CURRENT
                                                                | PendingIntent.FLAG_ONE_SHOT);
      Builder builder = new Builder(this);
      builder.setContentTitle("Bananagrams Invitation");
      String message = requester + " wants to play bananagrams";
      builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
      builder.setAutoCancel(true);
      builder.setContentText(message);
      builder.setSmallIcon(R.drawable.b); //unbelievable that this won't work w/out icon and that fact was not
      // documented
      builder.setContentIntent(pendingIntent);
      notificationManager.notify(1,
                                 builder.build());
    }
    if(messageType.equals("GameMove")) {
      boolean poc = Boolean.parseBoolean(data.getStringExtra(PROOF_OF_CONCEPT));
      int gameId = Integer.parseInt(data.getStringExtra("bananagrams_game_id"));
      if(poc) {

        //Intent i = new Intent(this,
        //                      POCMoveList.class);
        //i.putExtra(Network.GAME_ID_KEY,
        //           gameId);
        //startActivity(i);
        // TODO check if started and if so do this, if not start
        Intent sendCheckMoves = new Intent(POCMoveList.RECEIVE_MOVES);
        sendCheckMoves.putExtra(Network.GAME_ID_KEY, gameId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(sendCheckMoves);
      }
      else {
        //go to multiplayer bananagrams
        //if started
        Intent sendCheckMoves = new Intent(MultiPlayerBananagrams.RECEIVE_MOVES);
        sendCheckMoves.putExtra(Network.GAME_ID_KEY, gameId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(sendCheckMoves);
      }
    }
  }

  @Override
  protected void onError(Context context,
                         String s) {
  }

  @Override
  protected void onRegistered(Context context,
                              String s) {
    Network.getPlayerId(this);
  }

  @Override
  protected void onUnregistered(Context context,
                                String s) {

  }
}
