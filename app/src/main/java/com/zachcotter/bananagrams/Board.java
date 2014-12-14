package com.zachcotter.bananagrams;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;

public class Board extends RelativeLayout {

  private HashSet<PlacedLetter> placedLetters;
  private int dimension;
  private DictionarySearcher searcher;
  private Viewport viewport;
  private Tutor tutor;

  public Board(Context context,
               AttributeSet attrs) {
    super(context,
          attrs);
    this.setWillNotDraw(false);
    placedLetters = new HashSet<PlacedLetter>();
    dimension = 10;
    this.searcher = new DictionarySearcher(context);
    viewport = new Viewport();
  }

  public void setTutor(Tutor tutor) {
    this.tutor = tutor;
  }

  public HashSet<PlacedLetter> getPlacedLetters() {
    return placedLetters;
  }

  public void setPlacedLetters(HashSet<PlacedLetter> placedLetters) {
    this.placedLetters = placedLetters;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    Paint p = new Paint();
    p.setColor(Color.BLUE);
    int cell_width = getWidth() / dimension;
    int cell_height = getHeight() / dimension;

    for(int i = 0; i < dimension; i++) {
      //draw vertical grid lines
      int startX = i * cell_width;
      int startY = 0;
      int endX = startX;
      int endY = getHeight();
      canvas.drawLine(startX,
                      startY,
                      endX,
                      endY,
                      p);

      //draw horizontal gridlines
      startX = 0;
      startY = i * cell_height;
      endX = getWidth();
      endY = startY;
      canvas.drawLine(startX,
                      startY,
                      endX,
                      endY,
                      p);
    }

    // have each letter draw itself
    for(PlacedLetter placedLetter : viewport.getVisibleLetters(placedLetters)) {
      placedLetter.draw(canvas,
                        cell_width,
                        cell_height,
                        getContext(),
                        viewport);
    }
  }

  public int getScore() {
    int score = 0;

    if(placedLetters.size() == 2) {
      tutor.show(getContext(),
                 Tutor.SCROLLABILITY);
    }

    //TODO exclude unconnected
    for(PlacedLetter letter : placedLetters) {
      if(letter.isValid()) {
        score += letter.getPoints();
      }
    }
    return score;
  }

  public void scroll(String direction,
                     int distance) {
    int boardDistance = distance / (getWidth() / dimension);
    for(int i = 0; i < boardDistance; i++) {
      viewport.move(Direction.DIRECTIONS.indexOf(direction));
    }
    this.invalidate();
  }

  public boolean placeLetter(Letter letter,
                             int x,
                             int y) {
    x += viewport.getUpperLeftX();
    y += viewport.getUpperLeftY();
    if(canPlaceLetter(x,
                      y)) {
      PlacedLetter newLetter = new PlacedLetter(letter,
                                                x,
                                                y);
      placedLetters.add(newLetter);
      validateLetters(newLetter);
      this.invalidate();
      return true;
    }
    else { return false; }
  }

  public void validateLetters(PlacedLetter newLetter) {
    //get all above letters
    //get all below letters
    //vertical word = above + newLetter + below

    //get all left letters
    //get all right letters
    //horizontal word = above + newLetter + below

    //mark all letters in vertical word as validity of vertical word
    //same with horizontal

    ArrayList<PlacedLetter> above = new ArrayList<PlacedLetter>();
    ArrayList<PlacedLetter> below = new ArrayList<PlacedLetter>();
    ArrayList<PlacedLetter> left = new ArrayList<PlacedLetter>();
    ArrayList<PlacedLetter> right = new ArrayList<PlacedLetter>();

    for(PlacedLetter other : placedLetters) {
      if(other.equals(newLetter)) { continue; }
      //same column
      if(other.getXPosition() == newLetter.getXPosition()) {
        //other is below
        if(other.getYPosition() > newLetter.getYPosition()) {
          above.add(other);
        }
        //other is above
        else if(other.getYPosition() < newLetter.getYPosition()) {
          below.add(other);
        }
      }
      //same row
      else if(other.getYPosition() == newLetter.getYPosition()) {
        //other is to right
        if(other.getXPosition() > newLetter.getXPosition()) {
          right.add(other);
        }
        //other is to left
        else if(other.getXPosition() < newLetter.getXPosition()) {
          left.add(other);
        }
      }

    }

    //remove discontinuous and order
    ArrayList<PlacedLetter> verticalWord = new ArrayList<PlacedLetter>();
    verticalWord.add(newLetter);

    boolean keepGoing = true;
    int nextX = newLetter.getXPosition();
    int nextY = newLetter.getYPosition();
    while(keepGoing) {
      int direction = 0; // north
      nextX += Direction.X_DIFFS[direction];
      nextY += Direction.Y_DIFFS[direction];
      boolean found = false;
      for(PlacedLetter other : above) {
        if(other.getXPosition() == nextX && other.getYPosition() == nextY) {
          verticalWord.add(other);
          found = true;
        }
      }
      keepGoing = found;
    }

    keepGoing = true;
    nextX = newLetter.getXPosition();
    nextY = newLetter.getYPosition();
    while(keepGoing) {
      int direction = 2;
      nextX += Direction.X_DIFFS[direction];
      nextY += Direction.Y_DIFFS[direction];
      boolean found = false;
      for(PlacedLetter other : below) {
        if(other.getXPosition() == nextX && other.getYPosition() == nextY) {
          verticalWord.add(0,
                           other);
          found = true;
        }
      }
      keepGoing = found;
    }

    ArrayList<PlacedLetter> horizontalWord = new ArrayList<PlacedLetter>();
    horizontalWord.add(newLetter);

    keepGoing = true;
    nextX = newLetter.getXPosition();
    nextY = newLetter.getYPosition();
    while(keepGoing) {
      int direction = 3;
      nextX += Direction.X_DIFFS[direction];
      nextY += Direction.Y_DIFFS[direction];
      boolean found = false;
      for(PlacedLetter other : left) {
        if(other.getXPosition() == nextX && other.getYPosition() == nextY) {
          horizontalWord.add(0,
                             other);
          found = true;
        }
      }
      keepGoing = found;
    }

    keepGoing = true;
    nextX = newLetter.getXPosition();
    nextY = newLetter.getYPosition();
    while(keepGoing) {
      int direction = 1;
      nextX += Direction.X_DIFFS[direction];
      nextY += Direction.Y_DIFFS[direction];
      boolean found = false;
      for(PlacedLetter other : right) {
        if(other.getXPosition() == nextX && other.getYPosition() == nextY) {
          horizontalWord.add(other);
          found = true;
        }
      }
      keepGoing = found;
    }

    boolean verticalIsValid = isValidWord(verticalWord);
    boolean horizontalIsValid = isValidWord(horizontalWord);

    if(!verticalIsValid) {
      verticalWord.remove(newLetter);
    }
    else {
      new ToneGenerator(AudioManager.STREAM_NOTIFICATION,
                        100).startTone(
        ToneGenerator.TONE_PROP_BEEP);
    }
    for(PlacedLetter vertical : verticalWord) {
      vertical.setValidity(verticalIsValid);
    }
    if(!horizontalIsValid) {
      horizontalWord.remove(newLetter);
    }
    else {
      new ToneGenerator(AudioManager.STREAM_NOTIFICATION,
                        100).startTone(
        ToneGenerator.TONE_PROP_BEEP);
    }
    for(PlacedLetter horizontal : horizontalWord) {
      horizontal.setValidity(horizontalIsValid);
    }


    boolean newIsValid = verticalIsValid || horizontalIsValid;

    if(newIsValid) {
      tutor.show(getContext(),
                 Tutor.FINISH_FIRST_WORD);
    }

    newLetter.setValidity(newIsValid);
  }

  public boolean placeLetterByTouchLocation(Letter letter,
                                            int x,
                                            int y) {
    int boardX = x / (getWidth() / dimension);
    int boardY = y / (getHeight() / dimension);
    return placeLetter(letter,
                       boardX,
                       boardY);
  }

  public Entry<Integer, Integer> translateTouchPositionToGridPosition(int x,
                                                                      int y) {
    return new SimpleEntry<Integer, Integer>(x / (getWidth() / dimension),
                                             y / (getHeight() / dimension));
  }

  public PlacedLetter getLetterAt(int x,
                                  int y) {
    int letterX = x / (getWidth() / dimension);
    int letterY = y / (getHeight() / dimension);
    letterX += viewport.getUpperLeftX();
    letterY += viewport.getUpperLeftY();
    for(PlacedLetter letter : placedLetters) {
      if(letter.getXPosition() == letterX && letter.getYPosition() == letterY) {
        return letter;
      }
    }
    return null;
  }

  public boolean remove(PlacedLetter letter) {
    boolean removed = placedLetters.remove(letter);
    for(PlacedLetter adjacent : this.getAdjacentLetters(letter.getXPosition(),
                                                        letter.getYPosition())
      ) {
      validateLetters(adjacent);
    }
    return removed;
  }

  private boolean isValidWord(ArrayList<PlacedLetter> letters) {
    char[] characters = new char[letters.size()];
    for(int i = 0; i < letters.size(); i++) {
      characters[i] = letters.get(i).getLetter();
    }
    String word = new String(characters);
    try {
      return searcher.search(word);
    }
    catch(IOException e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
  }


  private boolean canPlaceLetter(int x,
                                 int y) {
    //position not filled
    return !locationFilled(x,
                           y);
  }

  public boolean locationFilled(int x,
                                 int y) {
    for(PlacedLetter letter : placedLetters) {
      if(letter.getXPosition() == x && letter.getYPosition() == y) {
        return true;
      }
    }
    return false;
  }

  private ArrayList<PlacedLetter> getAdjacentLetters(int x,
                                                     int y) {
    ArrayList<PlacedLetter> adjacents = new ArrayList<PlacedLetter>();

    for(PlacedLetter letter : placedLetters) {
      int letterX = letter.getXPosition();
      int letterY = letter.getYPosition();

      for(int i = 0; i < Direction.DIRECTIONS.size(); i++) {
        if(letterX == x + Direction.X_DIFFS[i] && letterY == y + Direction.Y_DIFFS[i]) {
          adjacents.add(letter);
        }
      }
    }

    return adjacents;
  }

  public String toString() {
    String toString = "Letters:\n";
    for(PlacedLetter letter : placedLetters) {
      toString += letter + "\n";
    }
    toString += viewport;
    return toString;
  }
}
