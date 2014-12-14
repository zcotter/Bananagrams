package com.zachcotter.bananagrams;

import java.util.HashSet;

public class Viewport {
  private int upperLeftX;
  private int upperLeftY;
  private int width;
  private int height;

  public Viewport() {
    upperLeftX = 0;
    upperLeftY = 0;
    width = 10;
    height = 10;
  }

  public boolean move(int direction) {
    this.setUpperLeftX(this.getUpperLeftX() + Direction.X_DIFFS[direction]);
    this.setUpperLeftY(this.getUpperLeftY() + Direction.Y_DIFFS[direction]);
    return true;
  }

  public boolean isVisible(PlacedLetter letter) {
    return this.isVisible(letter.getXPosition(),
                          letter.getYPosition());
  }

  public boolean isVisible(int x,
                           int y) {
    return x >= this.getUpperLeftX() &&
      x < this.getUpperLeftX() + this.getHeight() &&
      y >= this.getUpperLeftY() &&
      y < this.getUpperLeftY() + this.getHeight();
  }

  public HashSet<PlacedLetter> getVisibleLetters(HashSet<PlacedLetter>
                                                     all) {
    HashSet<PlacedLetter> visible = new HashSet<PlacedLetter>();
    for(PlacedLetter letter : all) {
      if(this.isVisible(letter)) {
        visible.add(letter);
      }
    }
    return visible;
  }

  public int getXInViewport(PlacedLetter letter) {
    return getXInViewport(letter.getXPosition());
  }

  public int getXInViewport(int x) {
    return x - this.getUpperLeftX();
  }

  public int getYInViewport(PlacedLetter letter) {
    return getYInViewport(letter.getYPosition());
  }

  public int getYInViewport(int y) {
    return y - this.getUpperLeftY();
  }

  public int getUpperLeftX() {
    return upperLeftX;
  }

  public void setUpperLeftX(int upperLeftX) {
    this.upperLeftX = upperLeftX;
  }

  public int getUpperLeftY() {
    return upperLeftY;
  }

  public void setUpperLeftY(int upperLeftY) {
    this.upperLeftY = upperLeftY;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  @Override
  public String toString() {
    return "Viewport{" +
      "upperLeftX=" + upperLeftX +
      ", upperLeftY=" + upperLeftY +
      ", width=" + width +
      ", height=" + height +
      '}';
  }

}
