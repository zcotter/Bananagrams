package com.zachcotter.bananagrams;

public interface Move {
  //have to use Integer instead of int, because java sucks and primatives can't be null
  public void execute(Integer startX,
                      Integer startY,
                      Integer endX,
                      Integer endY,
                      Letter letter);

  public boolean makeMove();

  public String toString();
}

