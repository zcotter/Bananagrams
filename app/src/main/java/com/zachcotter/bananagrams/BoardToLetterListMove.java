package com.zachcotter.bananagrams;

public class BoardToLetterListMove extends AbstractMove {

  private PlacedLetter letter;

  public BoardToLetterListMove(Bananagrams bananagrams,
                               PlacedLetter letter) {
    super(bananagrams);
    this.letter = letter;
  }

  public BoardToLetterListMove(Bananagrams bananagrams,
                               PlacedLetter placedLetter,
                               String letters) {
    super(bananagrams,
          letters);
    this.letter = placedLetter;
  }

  @Override
  public boolean makeMove() {
    //deal with assignment 7 nonsense
    if(bananagrams == null){
      return true;
    }
    bananagrams.board.remove(letter);
    bananagrams.list.add(letter);
    bananagrams.vibrator.vibrate(50);
    bananagrams.refreshAfterMove();
    execute(letter.getXPosition(),
            letter.getYPosition(),
            null,
            null,
            letter);
    return true;
  }

  public String toString(){
    return "Move " + letter.getLetter() + " to letter list";
  }
}
