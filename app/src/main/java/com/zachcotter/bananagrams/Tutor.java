package com.zachcotter.bananagrams;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Gravity;
import android.widget.Toast;



public class Tutor {

  public static final Tip SELECT_FIRST_LETTER = new Tip("Build your first word! Tap a letter below to get started.");
  public static final Tip PLACE_FIRST_LETTER = new Tip("Great! Now tap a space on the board to place that letter.");
  public static final Tip AFTER_PLACE_FIRST_LETTER = new Tip("Keep placing letters until you build a word!");
  public static final Tip FINISH_FIRST_WORD = new Tip("You made your first word! Now try and connect it to some new " +
                                                        "words!");
  public static final Tip SCROLLABILITY = new Tip("If you need more room you can swipe the screen to move the board");
  public static final Tip TIME_RUNNING_OUT = new Tip("Almost out of time! Try and place as many letters as you can.");

  public static final String TUTORIAL_PREFERENCES_KEY = "TUTORIAL_TIPS";

  private SharedPreferences prefs;
  private Editor prefsEditor;

  public Tutor(Context context) {
    prefs = context.getSharedPreferences(TUTORIAL_PREFERENCES_KEY,
                                         Activity.MODE_PRIVATE);
    prefsEditor = prefs.edit();
  }

  public void show(Context context,
                   Tip tip) {
    if(prefs.getBoolean(tip.getKey(),
                        false) == false) {
      Toast toast = Toast.makeText(context,
                                   tip.getText(),
                                   Toast.LENGTH_LONG);
      toast.setGravity(Gravity.CENTER,
                       0,
                       0);
      toast.show();
      prefsEditor.putBoolean(tip.getKey(),
                             true);
      prefsEditor.commit();
    }
  }


}


