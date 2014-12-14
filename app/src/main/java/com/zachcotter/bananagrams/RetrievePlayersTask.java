package com.zachcotter.bananagrams;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RetrievePlayersTask extends AsyncTask<Void, Void, Map<Integer, String>> {
  @Override
  protected Map<Integer, String> doInBackground(Void... voids) {
    HttpParams params = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(params,
                                              Network.TIMEOUT);
    HttpConnectionParams.setSoTimeout(params,
                                      Network.TIMEOUT);
    HttpClient client = new DefaultHttpClient(params);
    HttpGet get = new HttpGet(Network.APPLICATION_SERVER_URL + Network.RETRIEVE_PLAYERS_ROUTE);
    HttpResponse response = null;
    try {
      response = client.execute(get);
      String responseString = EntityUtils.toString(response.getEntity());
      JSONArray playersJson = new JSONArray(responseString);
      Map<Integer, String> players = new HashMap<Integer, String>();

      //java sucks, why the fuck is this not iterable
      for(int i = 0; i < playersJson.length(); i++){
        JSONObject playerJson = playersJson.getJSONObject(i);
        players.put(playerJson.getInt("id"), playerJson.getString("name"));
      }
      return players;
    }
    catch(IOException e) {
      e.printStackTrace();
    }
    catch(JSONException e) {
      e.printStackTrace();
    }
    throw new RuntimeException("Could not retrieve players");
  }
}
