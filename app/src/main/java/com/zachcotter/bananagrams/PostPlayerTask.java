package com.zachcotter.bananagrams;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;

public class PostPlayerTask extends AsyncTask<String[], Void, Integer> {

  @Override
  protected Integer doInBackground(String[]... strings) {
    String json = "{\"bananagrams_player\": " +
      "{\"name\": \"" + strings[0][0].toString() + "\", \"registration_id\": \"" + strings[0][1].toString() + "\"}}";
    try {
      return (Integer) Network.postJson(json,
                                        Network.CREATE_PLAYER_ROUTE,
                                        "id");
    }
    catch(JSONException e) {
      // If this happens something is very broken, and app should die
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    }
    catch(IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
