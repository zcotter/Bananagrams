package com.zachcotter.bananagrams;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
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
import java.util.Map.Entry;

public class LoadMakeAndDestroyMovesTask extends AsyncTask<Integer, Void, Map<Integer, Move>> {

  private Bananagrams bananagrams;
  // client needs to set this var even if it will be null
  private boolean bananagramsInitialized = false;

  private Context context;

  public void setBananagrams(Bananagrams bananagrams) {
    this.bananagrams = bananagrams;
    bananagramsInitialized = true;
  }

  public void setContext(Context context){
    this.context = context;
  }

  public void initialize(Bananagrams bananagrams, Context context){
    setBananagrams(bananagrams);
    setContext(context);
  }

  @Override
  protected Map<Integer, Move> doInBackground(Integer... integers) {

    if(!bananagramsInitialized) {
      throw new RuntimeException("Need to pass bananagrams game to task");
    }

    int gameId = integers[0];
    // grab the moves with that id from the server
    //these should be a set because moves aren't necessarily delivered in order.
    Map<Integer, Move> moves = fetchMoves(gameId);
    HashMap<Integer, Move> completedMoves = new HashMap<Integer, Move>();
    for(Entry<Integer, Move> move : moves.entrySet()){
      if(destroyMove(move.getKey())){
        move.getValue().makeMove();
        completedMoves.put(move.getKey(), move.getValue());
      }//if the http delete fails, then don't make the move, it can be made next time we connect to the server
    }
    return completedMoves;
  }

  private boolean destroyMove(int id){
    HttpParams params = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(params,
                                              Network.TIMEOUT);
    HttpConnectionParams.setSoTimeout(params,
                                      Network.TIMEOUT);
    HttpClient client = new DefaultHttpClient(params);
    HttpDelete delete = new HttpDelete(Network.APPLICATION_SERVER_URL + Network.deleteMoveRoute(id));
    try {
      HttpResponse response = client.execute(delete);
      int statusCode = response.getStatusLine().getStatusCode();
      return statusCode < 400;
    }
    catch(IOException e) {
      e.printStackTrace();
      Network.showNetworkErrorDialog(context);
    }
    return false;
  }

  private Map<Integer, Move> fetchMoves(int gameId){
    HttpParams params = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(params,
                                              Network.TIMEOUT);
    HttpConnectionParams.setSoTimeout(params,
                                      Network.TIMEOUT);
    HttpClient client = new DefaultHttpClient(params);
    HttpGet get = new HttpGet(Network.APPLICATION_SERVER_URL + Network.showGameRoute(gameId));
    HttpResponse response;
    try {
      response = client.execute(get);
      String responseString = EntityUtils.toString(response.getEntity());
      JSONArray movesJson = new JSONArray(responseString);
      //map move id to move
      Map<Integer, Move> moves = new HashMap<Integer, Move>();
      //why the fuck is this not iterable
      for(int i = 0; i < movesJson.length(); i++) {
        JSONObject moveJson = movesJson.getJSONObject(i);
        Integer x0 = jsonToInteger(moveJson,
                                   "x0");
        Integer y0 = jsonToInteger(moveJson,
                                   "y0");
        Integer x1 = jsonToInteger(moveJson,
                                   "x1");
        Integer y1 = jsonToInteger(moveJson,
                                   "y1");
        char letter = moveJson.getString("letter").charAt(0);
        String letters = moveJson.getString("letters");
        Move move = MoveFactory.buildMove(x0,
                                          y0,
                                          x1,
                                          y1,
                                          letter,
                                          bananagrams,
                                          context,
                                          letters);

        Integer id = moveJson.getInt("id");

        moves.put(id,
                  move);
      }

      return moves;
    }
    catch(IOException e) {
      e.printStackTrace();
      Network.showNetworkErrorDialog(context);
    }
    catch(JSONException e) {
      e.printStackTrace();
    }
    throw new RuntimeException("Could not fetch moves from application server");
  }

  private Integer jsonToInteger(JSONObject jsonObject,
                                String fieldName) throws
                                                  JSONException {
    String value = jsonObject.getString(fieldName);
    if(value == null || value.equals("") || value.equals("null")) {
      return null;
    }
    else {
      return Integer.parseInt(value);
    }
  }
}
