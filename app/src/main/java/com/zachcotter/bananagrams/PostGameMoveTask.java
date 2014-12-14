package com.zachcotter.bananagrams;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;

public class PostGameMoveTask extends AsyncTask<String, Void, Integer> {

  @Override
  protected Integer doInBackground(String... strings) {
    String json = strings[0];
    try {
      return (Integer) Network.postJson(json,
                                        Network.CREATE_MOVE_ROUTE,
                                        "id");
    }
    catch(JSONException e) {
      e.printStackTrace();
    }
    catch(IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
