package com.zachcotter.bananagrams;

import android.content.Context;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class HighScore {

  public static void postHighScore(final int score,
                                   final Context context) {
    if(Network.networkAvailable(context)) {
      postHighScore(score,
                    Network.getPlayerId(context),
                    context);
    }

  }

  private static void postHighScore(int score,
                                    int id,
                                    Context context) {
    if(Network.networkAvailable(context)) {
      try {
        int rank = new Network.PostHighScoreTask().execute(score,
                                                           id).get();
        Toast.makeText(context,
                       "Your score is the " + rank + " best score amongst all players",
                       Toast.LENGTH_LONG).show();
      }
      catch(InterruptedException e) {
        e.printStackTrace();
      }
      catch(ExecutionException e) {
        e.printStackTrace();
      }
    }
  }


}
