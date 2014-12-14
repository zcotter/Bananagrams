package com.zachcotter.bananagrams;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class Letter {

  protected char letter;
  protected int points;

  public Letter(char letter,
                int points) {
    this.letter = letter;
    this.points = points;
  }

  public static int determinePoints(char letter,
                                    Context context) {
    String[] scorePerLetter = context.getResources().getString(R.string
                                                                 .tile_scores).split(" ");
    ArrayList<Character> letters = new ArrayList<Character>();
    ArrayList<Integer> scores = new ArrayList<Integer>();
    for(String pair : scorePerLetter){
      String[] split = pair.split(":");
      letters.add(split[0].charAt(0));
      scores.add(Integer.parseInt(split[1]));
    }
    return scores.get(letters.indexOf(letter));
  }

  @Override
  public boolean equals(Object o) {
    try {
      Letter other = (Letter) o;
      return other.getLetter() == this.getLetter();
    }
    catch(ClassCastException e) {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return (int) letter;
  }

  public void draw(Canvas canvas,
                   int width,
                   int height,
                   int x,
                   int y,
                   Context c) {
    int id = c.getResources().getIdentifier("" + this.getLetter(),
                                            "drawable",
                                            c.getPackageName());
    Drawable tile = c.getResources().getDrawable(id);
    tile.setBounds(x,
                   y,
                   x + width,
                   y + height);
    tile.draw(canvas);
  }

  public int getPoints() {
    return points;
  }

  public void setPoints(int points) {
    this.points = points;
  }

  public char getLetter() {
    return letter;
  }

  public void setLetter(char letter) {
    this.letter = letter;
  }

  public String toString() {
    return "" + this.getLetter();
  }
}
