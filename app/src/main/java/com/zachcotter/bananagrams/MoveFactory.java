package com.zachcotter.bananagrams;

import android.content.Context;

public class MoveFactory {
  public static Move buildMove(Integer x0,
                               Integer y0,
                               Integer x1,
                               Integer y1,
                               char letter,
                               Bananagrams bananagrams,
                               Context context,
                               String letters) {
    Letter tile = new Letter(letter,
                             Letter.determinePoints(letter,
                                                    context));
    if(x0 == null && y0 == null) {
      return new LetterListToBoardMove(bananagrams,
                                       tile,
                                       x1,
                                       y1,
                                       letters);
    }
    else if(x1 == null && y1 == null) {
      PlacedLetter placedLetter = new PlacedLetter(tile,
                                                   x0,
                                                   y0);
      return new BoardToLetterListMove(bananagrams,
                                       placedLetter,
                                       letters);
    }
    else if(x0 != null && y0 != null && x1 != null && y1 != null) {
      PlacedLetter placedLetter = new PlacedLetter(tile,
                                                   x0,
                                                   y0);
      return new BoardToBoardMove(bananagrams,
                                  placedLetter,
                                  x1,
                                  y1,
                                  letters);
    }
    throw new RuntimeException("Invalid Move");
  }
}
