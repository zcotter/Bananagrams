package com.zachcotter.bananagrams;

public class Tip {
  private String text;
  private final String key;

  public Tip(String text) {
    this.text = text;
    this.key = "" + text.hashCode();
  }

  public String getText() {
    return text;
  }

  public String getKey() {
    return key;
  }
}