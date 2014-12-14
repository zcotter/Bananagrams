package com.zachcotter.bananagrams;

import java.util.ArrayList;

abstract class AbstractMove implements Move {
  protected Bananagrams bananagrams;
  protected String letters;
  public boolean shouldPost;

  public AbstractMove(Bananagrams bananagrams) {
    this.bananagrams = bananagrams;
    this.shouldPost = true;
  }

  public AbstractMove(Bananagrams bananagrams,
                      String letters) {
    this.bananagrams = bananagrams;
    this.letters = letters;
  }

  //called after a move is successfully made...
  public void execute(Integer startX,
                      Integer startY,
                      Integer endX,
                      Integer endY,
                      Letter letter) {
    bananagrams.scoreView.setText("Score: " + bananagrams.board.getScore());
    bananagrams.scoreView.invalidate();
    if(letters != null && !letters.equals("")) {
      bananagrams.list.setLetters(stringToList(letters));
    }
    if(bananagrams.list.getLetters().isEmpty() && !bananagrams.list.isInPeelMode()) {
      bananagrams.checkPeelyness();
    }
    if((bananagrams.isMultiPlayer() || bananagrams.isPOC()) && Network.networkAvailable(bananagrams
                                                                                          .getApplicationContext())
      && shouldPost) {
      String json = "{\"bananagrams_move\":" +
        "{\"bananagrams_game\":" + bananagrams.getGameID() + //TODO need to set gameID for single player poc
        ", \"x0\":\"" + rubyizeInteger(startX) +
        "\", \"y0\":\"" + rubyizeInteger(startY) +
        "\", \"x1\":\"" + rubyizeInteger(endX) +
        "\", \"y1\":\"" + rubyizeInteger(endY) +
        "\", \"letter\":\"" + letter.getLetter() +
        "\", \"bananagrams_player\":" + bananagrams.getPlayerID() + "" +
        ", \"letters\": \"" + bananagrams.list.toString() + "\"}}";
      new PostGameMoveTask().execute(json);
    }
  }

  private ArrayList<Letter> stringToList(String string) {
    ArrayList<Letter> letters = new ArrayList<Letter>();
    if(letters != null) {
      for(int i = 0; i < string.length(); i++) {
        letters.add(new Letter(string.charAt(i),
                               Letter.determinePoints(string.charAt(i),
                                                      bananagrams)));
      }
    }
    return letters;
  }

  private String rubyizeInteger(Integer number) {
    if(number == null) {
      return "null";
    }
    else { return "" + number.toString(); }
  }

  public abstract String toString();
}
