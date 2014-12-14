package com.zachcotter.bananagrams;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class PlacedLetter extends Letter {

  private int xPosition;
  private int yPosition;
  private boolean valid;

  public PlacedLetter(Letter letter,
                      int x,
                      int y) {
    super(letter.getLetter(),
          letter.getPoints());
    this.xPosition = x;
    this.yPosition = y;
    this.valid = false;
  }

  public String toString() {
    return this.getLetter() + ": (" +
      this.getXPosition() + ", " +
      this.getYPosition() + ")";
  }

  public boolean equals(Object o) {
    try {
      PlacedLetter other = (PlacedLetter) o;
      return super.equals(other) &&
        other.getXPosition() == this.getXPosition() &&
        other.getYPosition() == this.getYPosition();
    }
    catch(ClassCastException e) {
      return false;
    }
  }

  public int hashCode() {
    return super.hashCode() +
      (2 * this.getXPosition()) +
      (3 * this.getYPosition());
  }

  public void draw(Canvas canvas,
                   int width,
                   int height,
                   Context c,
                   Viewport viewport) {
    int upperLeftX = viewport.getXInViewport(this) * width;
    int upperLeftY = viewport.getYInViewport(this) * height;
    int id = c.getResources().getIdentifier("" + this.getLetter(),
                                            "drawable",
                                            c.getPackageName());
    Drawable tile = c.getResources().getDrawable(id);
    tile.setBounds(upperLeftX,
                   upperLeftY,
                   upperLeftX + width,
                   upperLeftY + height);
    tile.draw(canvas);
    if(!this.isValid()) {
      Paint p = new Paint();
      p.setColor(Color.RED);
      p.setStrokeWidth(3);
      p.setStyle(Paint.Style.STROKE);
      canvas.drawRect(upperLeftX,
                      upperLeftY,
                      upperLeftX + width,
                      upperLeftY + height,
                      p);
    }
  }

  public void setValidity(boolean validity) {
    this.valid = validity;
  }

  public boolean isValid() {
    return this.valid;
  }

  public void validate() {
    this.valid = true;
  }

  public void invalidate() {
    this.valid = false;
  }

  public int getXPosition() {
    return xPosition;
  }

  public void setXPosition(int xPosition) {
    this.xPosition = xPosition;
  }

  public int getYPosition() {
    return yPosition;
  }

  public void setYPosition(int yPosition) {
    this.yPosition = yPosition;
  }
}
