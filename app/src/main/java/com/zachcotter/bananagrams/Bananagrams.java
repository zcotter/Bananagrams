package com.zachcotter.bananagrams;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;

public abstract class Bananagrams extends Activity implements OnTouchListener {


  public static final String SCORE_KEY = "Score";
  public static final int REQUEST_EXIT = -1;
  protected static final long TIME_ALLOWED_MS = 300000;
  protected static final String TIME_KEY = "Time";
  protected static final String PLACED_LETTER_KEY = "PlacedLetters";
  protected static final String LETTER_LIST_KEY = "LetterList";
  protected static final String TILE_BAG_KEY = "TileBag";
  protected static final String PEEL_KEY = "peelyness";

  protected ArrayList<Letter> tileBag;
  protected LetterList list;
  protected Board board;
  protected Letter lastTouchedInLetterList;
  protected PlacedLetter lastTouchedOnBoard;
  protected GestureDetector detector;
  protected Vibrator vibrator;
  protected boolean gameOver;

  protected TextView scoreView;

  protected Tutor tutor;

  protected int gameID;

  public abstract boolean isMultiPlayer();

  public abstract boolean isPOC();

  public int getGameID() {
    return gameID;
  }

  public int getPlayerID() {
    return Network.getPlayerId(this);
  }


  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    gameOver = false;

    detector = new GestureDetector(this,
                                   new BananagramsGestureListener());
    vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

    board = (Board) this.findViewById(R.id.board);
    board.setOnTouchListener(this);

    list = (LetterList) this.findViewById(R.id.letters_view);
    list.setOnTouchListener(this);

    this.scoreView = (TextView) findViewById(R.id.score);

    if(savedInstanceState != null) {
      reinitialize(savedInstanceState);
    }
    else {
      initialize();
    }

    tutor = new Tutor(this);
    board.setTutor(tutor);
    tutor.show(this,
               Tutor.SELECT_FIRST_LETTER);
  }

  protected void initialize() {
    tileBag = new ArrayList<Letter>();
    gameID = this.getIntent().getIntExtra(Network.GAME_ID_KEY,
                                          -1);
    this.buildTileBag();
    drawLetters(20);
  }

  protected void reinitialize(Bundle savedInstanceState) {
    Gson gson = new Gson();

    Type letterListType = new TypeToken<ArrayList<Letter>>() {}.getType();
    Object tileBagG = gson.fromJson(savedInstanceState.getString(TILE_BAG_KEY),
                                    letterListType);
    tileBag = (ArrayList<Letter>) tileBagG;

    Type placedLettersType = new TypeToken<HashSet<PlacedLetter>>() {}.getType();
    Object placedLetters = gson.fromJson(savedInstanceState.getString
                                           (PLACED_LETTER_KEY),
                                         placedLettersType);
    board.setPlacedLetters((HashSet<PlacedLetter>) placedLetters);

    Object letterListLetters = gson.fromJson(savedInstanceState.getString(LETTER_LIST_KEY),
                                             letterListType);
    list.setLetters((ArrayList<Letter>) letterListLetters);

    list.setPeelMode(savedInstanceState.getBoolean(PEEL_KEY));
    gameID = savedInstanceState.getInt(Network.GAME_ID_KEY);

    this.setScore(board.getScore());
  }

  @Override
  protected void onPause() {
    if(!gameOver && !isMultiPlayer()) { pause(); }
    super.onPause();
  }


  private void setScore(int score) {
    scoreView.setText("Score:" + score);
  }

  protected void endGame() {
    gameOver = true;
    Intent result = new Intent(this,
                               BananagramsMenu.class);
    result.putExtra(SCORE_KEY,
                    "" + board.getScore());
    result.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    setResult(Activity.RESULT_OK,
              result);
    startActivity(result);
    finish();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Gson gson = new Gson();
    outState.putString(PLACED_LETTER_KEY,
                       gson.toJson(board.getPlacedLetters()));
    outState.putString(LETTER_LIST_KEY,
                       gson.toJson(list.getLetters()));
    outState.putString(TILE_BAG_KEY,
                       gson.toJson(tileBag));
    outState.putBoolean(PEEL_KEY,
                        list.isInPeelMode());
    outState.putInt(Network.GAME_ID_KEY,
                    getGameID());

  }

  private void drawLetters(int amount) {
    for(int i = 0; i < amount; i++) {
      list.add(tileBag.remove(new Random().nextInt(tileBag.size())));
    }
  }

  private void buildTileBag() {
    //fill the tile bag
    String[] frequencies = this.getResources().getString(R.string
                                                           .tile_frequencies).split(" ");
    String[] scorePerLetter = this.getResources().getString(R.string
                                                              .tile_scores).split(" ");
    for(int i = 0; i < frequencies.length; i++) {
      String letter = frequencies[i];
      char character = letter.split(":")[0].charAt(0);
      int frequency = Integer.parseInt(letter.split(":")[1]);
      int score = Integer.parseInt(scorePerLetter[i].split(":")[1]);
      for(int j = 0; j < frequency; j++) {
        tileBag.add(new Letter(character,
                               score));
      }
    }
  }

  protected void pause() {
    Intent result = new Intent(this,
                               BananagramsPauseMenu.class);
    setResult(Activity.RESULT_OK,
              result);
    result.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    startActivityForResult(result,
                           REQUEST_EXIT);
  }

  @Override
  protected void onActivityResult(int requestCode,
                                  int resultCode,
                                  Intent data) {
    if(requestCode == REQUEST_EXIT && resultCode == RESULT_OK) {
      finish();
    }
  }

  public void clearLastTouched() {
    lastTouchedInLetterList = null;
    list.setLastTouched(null);
    lastTouchedOnBoard = null;
  }

  public void refreshAfterMove() {
    clearLastTouched();
    board.invalidate();
    list.invalidate();
  }

  @Override
  public boolean onTouch(View view,
                         MotionEvent motionEvent) {
    this.detector.onTouchEvent(motionEvent);
    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

      int x = Math.round(motionEvent.getX());
      int y = Math.round(motionEvent.getY());

      Entry<Integer, Integer> boardCoord = board.translateTouchPositionToGridPosition(x, y);
      int xGrid =  boardCoord.getKey();
      int yGrid = boardCoord.getValue();
      switch(view.getId()) {
        case R.id.board:
          //If second space selected on board is empty
          if(lastTouchedOnBoard != null) {
            executeMove(new BoardToBoardMove(this,
                                             lastTouchedOnBoard,
                                             xGrid,
                                             yGrid));
          }
          else if(lastTouchedInLetterList != null) {
            executeMove(new LetterListToBoardMove(this,
                                                  lastTouchedInLetterList,
                                                  xGrid,
                                                  yGrid));
          }
          else {
            PlacedLetter letter = board.getLetterAt(x,
                                                    y);
            if(letter != null) {
              lastTouchedOnBoard = letter;
              vibrator.vibrate(50);
            }
          }
          return true;
        case R.id.letters_view:
          lastTouchedInLetterList = list.getLetterAt(x,
                                                     y);
          list.setLastTouched(lastTouchedInLetterList);
          list.invalidate();
          if(lastTouchedInLetterList != null) {
            vibrator.vibrate(50);
            tutor.show(this,
                       Tutor.PLACE_FIRST_LETTER);
          }
          //they touched an empty space and want to move a letter off the board
          if(lastTouchedInLetterList == null && lastTouchedOnBoard != null) {
            executeMove(new BoardToLetterListMove(this,
                                                  lastTouchedOnBoard));
          }
          return true;
      }
    }
    return false;
  }

  private void executeMove(Move move) {
    executeMove(move,
                true);
  }

  protected final void executeMove(Move move,
                                   boolean localPlayersTurn) {
    move.makeMove();
    onMove(move,
           localPlayersTurn);
  }

  protected void onMove(Move move,
                        boolean localPlayersTurn) {
    // just a callback
  }

  public void checkPeelyness() {
    for(PlacedLetter letter : board.getPlacedLetters()) {
      if(!letter.isValid()) {
        return;
      }
    }
    //keep going if all the letters are valid
    drawLetters(3);
    list.switchToPeelMode(list.getLetters());
  }

  private class BananagramsGestureListener extends SimpleOnGestureListener {
    @Override
    public boolean onFling(MotionEvent touch,
                           MotionEvent release,
                           float v,
                           float v2) {
      int xDist = Math.round(touch.getX() - release.getX());
      int yDist = Math.round(touch.getY() - release.getY());
      String swipeDirection = "";
      int swipeDistance = 0;
      if(Math.abs(xDist) > Math.abs(yDist)) {
        if(xDist > 0) {
          swipeDirection = Direction.EAST;
        }
        else {
          swipeDirection = Direction.WEST;
        }
        swipeDistance = Math.abs(xDist);
      }
      else if(Math.abs(xDist) < Math.abs(yDist)) {
        if(yDist > 0) {
          swipeDirection = Direction.NORTH;
        }
        else {
          swipeDirection = Direction.SOUTH;
        }
        swipeDistance = Math.abs(yDist);
      }
      board.scroll(swipeDirection,
                   swipeDistance);
      lastTouchedInLetterList = null;
      lastTouchedOnBoard = null;
      list.invalidate();
      return false;
    }
  }
}
