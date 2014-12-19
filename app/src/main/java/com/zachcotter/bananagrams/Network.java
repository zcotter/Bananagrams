package com.zachcotter.bananagrams;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Patterns;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

/**
 * This class includes constants for communication with GCM and the app server,
 * as well as static functions that should really be implemented in the Android framework
 * not my code.
 */
public class Network {
  public static final String GCM_SENDER_ID = "129745199203";
  static final int TIMEOUT = 10000;
  static final String APPLICATION_SERVER_URL = "http://bananagrams.zachcotter.com/";
  static final String CREATE_PLAYER_ROUTE = "bananagrams_players.json";
  public static final String BANAGRAMS_SERVER_PREFS = "BananagramsServerPrefs";
  static final String PLAYER_ID_KEY = "PlayerId";
  public static final String CREATE_SCORE_ROUTE = "bananagrams_scores.json";
  public static final String REGISTRATION_ID_KEY = "RegistrationIdKey";
  public static final String RETRIEVE_PLAYERS_ROUTE = "bananagrams_players.json";
  public static final String CREATE_GAME_ROUTE = "bananagrams_games.json";
  public static final String CREATE_MOVE_ROUTE = "bananagrams_moves.json";
  public static final String GAME_ID_KEY = "GAME_ID";
  private static final String SHOW_GAME_ROUTE = "bananagrams_games";
  private static final String DELETE_MOVES_ROUTE = "bananagrams_moves";

  public static Object postJson(String json,
                                String route,
                                String returnField) throws
                                                    JSONException,
                                                    IOException {
    // In Ruby this entire function would be one line, just saying
    //HTTParty.post(SERVER_URL + CREATE_PLAYER_ROUTE, body: {bananagrams_player: {name: 'aName'}}).body.id
    HttpParams params = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(params,
                                              Network.TIMEOUT);
    HttpConnectionParams.setSoTimeout(params,
                                      Network.TIMEOUT);
    HttpClient client = new DefaultHttpClient(params);
    HttpPost post = new HttpPost(Network.APPLICATION_SERVER_URL + route);
    StringEntity se = new StringEntity(new JSONObject(json).toString());
    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                                      "application/json"));
    post.setEntity(se);
    HttpResponse response = client.execute(post);
    String responseString = EntityUtils.toString(response.getEntity());
    Log.w("response", responseString);
    JSONObject record = new JSONObject(responseString);
    return record.get(returnField);

  }

  public static boolean checkPlayServices(final Activity activity) {
    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
    if(resultCode != ConnectionResult.SUCCESS) {
      if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
        GooglePlayServicesUtil.getErrorDialog(resultCode,
                                              activity,
                                              9000).show();
      }
      return false;
    }
    return true;
  }

  public static int getPlayerId(final Context context) {
    final SharedPreferences prefs = context.getSharedPreferences(Network.BANAGRAMS_SERVER_PREFS,
                                                                 Activity.MODE_PRIVATE);
    int playerID = prefs.getInt(Network.PLAYER_ID_KEY,
                                -1);
    if(playerID == -1 && networkAvailable(context, false)) {
      String playerName = getPlayerEmail(context);
      try {
        String id = GCMRegistrar.getRegistrationId(context);

        while(id == null || id.equals("")){
          Thread.sleep(1000);
          id = GCMRegistrar.getRegistrationId(context);
        }

        String[] params = {playerName,
                           id};
        playerID = new PostPlayerTask().execute(params,
                                                null,
                                                null).get();
        Editor prefsEditor = prefs.edit();
        prefsEditor.putInt(Network.PLAYER_ID_KEY,
                           playerID);
        prefsEditor.commit();
      }
      catch(InterruptedException e) {
        e.printStackTrace();
      }
      catch(ExecutionException e) {
        e.printStackTrace();
      }
    }

    return playerID;
  }

  private static String getPlayerEmail(final Context context) {
    Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
    Account[] accounts = AccountManager.get(context).getAccounts();
    for(Account account : accounts) {
      if(emailPattern.matcher(account.name).matches()) {
        String possibleEmail = account.name;
        return possibleEmail;
      }
    }
    //TODO do something nicer here
    throw new RuntimeException("No Email Address");
  }

  public static String showGameRoute(int gameId) {
    return SHOW_GAME_ROUTE + "/" + gameId;
  }

  public static String deleteMoveRoute(int id) {
    return DELETE_MOVES_ROUTE + "/" + id;
  }

  public static void showNetworkErrorDialog(Context context) {
    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = manager.getActiveNetworkInfo();

    if(networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()){
      showOkButtonDialog(context, "This action failed because the network was not available");
    }
    // otherwise it wasn't a network error
  }

  public static boolean networkAvailable(Context context){
    return networkAvailable(context, true);
  }

  public static boolean networkAvailable(Context context, boolean showDialog){
    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = manager.getActiveNetworkInfo();

    if(networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()){
      if(showDialog){
        showOkButtonDialog(context, "The network is not available, so this action will not be performed.");
      }
      return false;
    }
    else{
      return true;
    }
  }

  public static void showOkButtonDialog(Context context, String message){
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setMessage(message)
      .setCancelable(false)
      .setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          //go away
        }
      });
    AlertDialog alert = builder.create();
    alert.show();
  }


  static class PostHighScoreTask extends AsyncTask<Integer, Void, Integer> {


    @Override
    protected Integer doInBackground(Integer... integers) {
      String json = "{\"bananagrams_score\": {\"score\": " + integers[0] + ", \"bananagrams_player\": " + integers[1]
        + "}}";
      try {
        return (Integer) Network.postJson(json,
                                          Network.CREATE_SCORE_ROUTE,
                                          "rank");
      }
      catch(JSONException e) {
        e.printStackTrace();
        throw new RuntimeException(e.getMessage());
      }
      catch(IOException e) {
        e.printStackTrace();
      }
      return null;
    }
  }



}


