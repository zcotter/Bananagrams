package com.zachcotter.bananagrams;

public class BoardToBoardMove extends AbstractMove {

  private PlacedLetter origin;
  private int x;
  private int y;

  public BoardToBoardMove(Bananagrams bananagrams,
                          PlacedLetter origin,
                          int x,
                          int y) {
    super(bananagrams);
    initialize(origin, x, y);
  }

  public BoardToBoardMove(Bananagrams bananagrams,
                          PlacedLetter placedLetter,
                          Integer x1,
                          Integer y1,
                          String letters) {
    super(bananagrams, letters);
    initialize(placedLetter,
               x1,
               y1);
  }

  private void initialize(PlacedLetter placedLetter,
                          Integer x1,
                          Integer y1) {
    this.origin = placedLetter;
    this.x = x1;
    this.y = y1;
  }


  @Override
  public boolean makeMove() {
    //deal with assignment 7 nonsense
    if(bananagrams == null) {
      return true;
    }
    if(!bananagrams.board.locationFilled(x,
                                         y)) {
      if(bananagrams.board.placeLetter(origin,
                                       x,
                                       y)) {
        bananagrams.board.remove(origin);
        bananagrams.vibrator.vibrate(50);
        bananagrams.refreshAfterMove();
        execute(origin.getXPosition(),
                origin.getYPosition(),
                x,
                y,
                origin);
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "Move " + origin.getLetter() + " from (" + origin.getXPosition() + ", " +
      origin.getYPosition() + ") to (" + x + ", " + y + ")";
  }
}
