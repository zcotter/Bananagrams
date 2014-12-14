package com.zachcotter.bananagrams;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class LetterList extends RelativeLayout implements OnClickListener {

  private static final int LETTERS_PER_ROW = 10;

  private ArrayList<Letter> letters;
  private Letter lastTouched;
  private boolean peelMode;

  public LetterList(Context context,
                    AttributeSet attrs) {
    super(context,
          attrs);
    this.setWillNotDraw(false);
    peelMode = false;
    this.letters = new ArrayList<Letter>();
    lastTouched = null;
  }

  public boolean isInPeelMode(){
    return peelMode;
  }

  public void setPeelMode(boolean inPeelMode){
    if(inPeelMode){
      //only for pause/resume
      switchToPeelMode(new ArrayList<Letter>());
    }
    else{
      endPeelMode();
    }
  }

  public void switchToPeelMode(ArrayList<Letter> nextLetters){
    letters.addAll(nextLetters);
    peelMode = true;
    findViewById(R.id.peel_button).setVisibility(View.VISIBLE);
    findViewById(R.id.peel_button).setOnClickListener(this);
    this.invalidate();
  }

  public void endPeelMode(){
    findViewById(R.id.peel_button).setVisibility(View.GONE);
    peelMode = false;
    this.invalidate();
  }

  protected void onDraw(Canvas canvas) {
    if(peelMode){
      super.onDraw(canvas);
      return;
    }
    int cellWidth = this.getWidth() / LETTERS_PER_ROW;
    int cellHeight = this.getHeight() / 2;
    // have each letter draw itself
    for(int i = 0; i < letters.size(); i++) {
      Letter letter = letters.get(i);
      int x = -1;
      int y = -1;
      if(i > 9) {
        x = (i - LETTERS_PER_ROW) * cellWidth;
        y = cellHeight;
      }
      else {
        x = i * cellWidth;
        y = 0;
      }
      letter.draw(canvas,
                  cellWidth,
                  cellHeight,
                  x,
                  y,
                  this.getContext());
      if(lastTouched != null && letter.equals(lastTouched)) {
        Paint p = new Paint();
        p.setColor(Color.GREEN);
        p.setStrokeWidth(3);
        p.setStyle(Paint.Style.STROKE);
        canvas.drawRect(x,
                        y,
                        x + cellWidth,
                        y + cellHeight,
                        p);
      }
    }
  }

  public Letter getLetterAt(int x,
                            int y) {
    int column = x / (getWidth() / LETTERS_PER_ROW);
    int row = y / (getHeight() / 2);
    int letter = column + (row * LETTERS_PER_ROW);
    try {
      return letters.get(letter);
    }
    catch(IndexOutOfBoundsException e) {
      return null;
    }
  }

  public void setLastTouched(Letter lastTouched) {
    this.lastTouched = lastTouched;
  }

  public ArrayList<Letter> getLetters() {
    return letters;
  }

  public boolean remove(Letter letter) {
    return letters.remove(letter);
  }

  public boolean add(Letter letter) {
    return letters.add(letter);
  }

  public void setLetters(ArrayList<Letter> letters) {
    this.letters = letters;
  }

  @Override
  public void onClick(View view) {
    switch(view.getId()){
      case R.id.peel_button:
        endPeelMode();
        break;
    }
  }

  public String toString(){
    String toString = "";
    for(Letter l : letters){
      toString += l.getLetter();
    }
    return toString;
  }
}
