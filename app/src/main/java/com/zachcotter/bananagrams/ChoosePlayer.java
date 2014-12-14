package com.zachcotter.bananagrams;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ChoosePlayer extends ListActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if(android.os.Build.VERSION.SDK_INT > 10) {
      ActionBar actionBar = getActionBar();
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
    viewPlayers();
  }

  private void viewPlayers() {
    try {
      if(Network.networkAvailable(this)) {
        Map<Integer, String> players = new RetrievePlayersTask().execute().get();
        setListAdapter(new PlayerListAdapter(players));
      }
    }
    catch(InterruptedException e) {
      e.printStackTrace();
    }
    catch(ExecutionException e) {
      e.printStackTrace();
    }
  }

  class PlayerListAdapter extends BaseAdapter {

    private ArrayList<Integer> playerIDs;
    private ArrayList<String> playerNames;

    public PlayerListAdapter(Map<Integer, String> players) {
      this.playerIDs = new ArrayList<Integer>(players.keySet());
      this.playerNames = new ArrayList<String>(players.values());
    }

    @Override
    public int getCount() {
      return playerIDs.size();
    }

    @Override
    public String getItem(int i) {
      return playerNames.get(i);
    }

    @Override
    public long getItemId(int i) {
      return playerIDs.get(i);
    }

    @Override
    public View getView(final int i,
                        View convertView,
                        ViewGroup parent) {
      View result;
      if(convertView == null) {
        result = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_player,
                                                                  parent,
                                                                  false);
      }
      else {
        result = convertView;
      }

      Button view = (Button) result.findViewById(R.id.player_list_item);
      view.setText(getItem(i));
      view.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
          int playerID = Network.getPlayerId(getApplicationContext());
          int opponentID = playerIDs.get(i);
          try {
            int gameId = new PostGameInvitationTask().execute(playerID,
                                                              opponentID).get();
            if(getIntent().getBooleanExtra(GCMIntentService.PROOF_OF_CONCEPT,
                                           false)) {
              startActivity(new Intent(getApplicationContext(),
                                       POCMoveList.class));
            }
            else {
              Intent data = new Intent();
              data.putExtra(Network.GAME_ID_KEY, gameId);
              if (getParent() == null) {
                setResult(Activity.RESULT_OK, data);
              } else {
                getParent().setResult(Activity.RESULT_OK, data);
              }
              finish();
            }
          }
          catch(InterruptedException e) {
            e.printStackTrace();
          }
          catch(ExecutionException e) {
            e.printStackTrace();
          }

        }
      });
      return result;
    }
  }

  class PostGameInvitationTask extends AsyncTask<Integer, Void, Integer> {

    @Override
    protected Integer doInBackground(Integer... integers) {
      int playerID = integers[0];
      int opponentID = integers[1];
      boolean poc = getIntent().getBooleanExtra(GCMIntentService.PROOF_OF_CONCEPT,
                                                false);
      String json = "{\"bananagrams_game\": {\"first_player\": " + playerID
        + ", \"second_player\": " + opponentID + ", \"poc\": " + poc + "}}";

      try {
        return (Integer) Network.postJson(json,
                                          Network.CREATE_GAME_ROUTE,
                                          "id");
      }
      catch(JSONException e) {
        e.printStackTrace();
      }
      catch(IOException e) {
        e.printStackTrace();
        Network.showNetworkErrorDialog(getApplicationContext());
      }
      throw new RuntimeException("Could not post game");
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Intent i = new Intent(this,
                          Communication.class);
    startActivity(i);
    finish();
    return true;
  }


}
