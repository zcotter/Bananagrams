package com.zachcotter.bananagrams;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;

public class DestroyMoveTask extends AsyncTask<Integer, Void, Boolean> {

  private Context context;

  public void setContext(Context context){
    this.context = context;
  }

  @Override
  protected Boolean doInBackground(Integer... integers) {
    return destroyMove(integers[0]);
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
}
