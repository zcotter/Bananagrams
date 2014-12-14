package com.zachcotter.bananagrams;

class LetterListToBoardMove extends AbstractMove {
  private Letter letter;
  private int x;
  private int y;

  public LetterListToBoardMove(Bananagrams bananagrams,
                               Letter letter,
                               int x,
                               int y) {
    super(bananagrams);
    initialize(letter, x, y);
  }

  public LetterListToBoardMove(Bananagrams bananagrams,
                               Letter letter,
                               int x,
                               int y,
                               String letters){
    super(bananagrams, letters);
    initialize(letter, x, y);
  }

  private void initialize(Letter letter, int x, int y){
    this.letter = letter;
    this.x = x;
    this.y = y;
  }

  public boolean makeMove() {

    //deal with assignment 7 nonsense
    if(bananagrams == null) {
      return true;
    }

    if(bananagrams.board.placeLetter(letter,
                                     x,
                                     y)) {
      bananagrams.list.remove(letter);
      bananagrams.tutor.show(bananagrams,
                             Tutor.AFTER_PLACE_FIRST_LETTER);
      bananagrams.vibrator.vibrate(50);
      bananagrams.refreshAfterMove();
      execute(null,
              null,
              x,
              y,
              letter);
      return true;
    }
    return false;
  }

  public String toString() {
    return "Move " + letter.getLetter() + " from letter list to (" + x + ", " + y + ")";
  }
}
