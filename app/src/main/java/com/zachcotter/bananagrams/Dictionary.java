package com.zachcotter.bananagrams;

import android.app.ActionBar;
import android.app.Activity;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class Dictionary extends Activity implements OnClickListener {

  private TextView resultView;
  private EditText search;
  private ArrayList<String> foundWords;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dictionary);

    foundWords = new ArrayList<String>();
    resultView = (TextView) findViewById(R.id.search_results);
    findViewById(R.id.clear_dictionary_button).setOnClickListener(this);
    findViewById(R.id.dictionary_acknowledgements_button).setOnClickListener(this);
    findViewById(R.id.dictionary_return_to_menu_button).setOnClickListener(this);

    search = (EditText) findViewById(R.id.dictionary_search);
    search.addTextChangedListener(new SearchTextChangedListener());

    if (android.os.Build.VERSION.SDK_INT > 10){
      ActionBar actionBar = getActionBar();
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  @Override
  public void onClick(View view) {
    switch(view.getId()) {
      case R.id.clear_dictionary_button:
        search.setText("");
        resultView.setText(R.string.search_results_header);
        foundWords.clear();
        break;
      case R.id.dictionary_return_to_menu_button:
        onOptionsItemSelected(null);
        break;
    }
  }

  private class SearchTextChangedListener implements TextWatcher {

    private DictionarySearcher searcher;

    public SearchTextChangedListener() {
      super();
      searcher = new DictionarySearcher(getApplicationContext());
    }

    @Override
    public void beforeTextChanged(CharSequence text,
                                  int start,
                                  int count,
                                  int after) {
      //Do nothing
    }

    @Override
    public void onTextChanged(CharSequence text,
                              int start,
                              int before,
                              int count) {
      //Do nothing
    }

    @Override
    public void afterTextChanged(Editable string) {
      try {
        if(searcher.search(string.toString()) && !foundWords.contains(string
                                                                        .toString())) {

          new ToneGenerator(AudioManager.STREAM_NOTIFICATION,
                            100).startTone(
            ToneGenerator.TONE_PROP_BEEP);
          foundWords.add(string.toString());
          resultView.setText(resultView.getText() + "\n" + string.toString());
        }
      }
      catch(IOException e) {
        e.printStackTrace();
      }
    }
  }
}
