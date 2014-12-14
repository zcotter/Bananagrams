package com.zachcotter.bananagrams;

import java.util.ArrayList;
import java.util.Arrays;

public class Direction {

  public static final String NORTH = "north";
  public static final String EAST = "east";
  public static final String SOUTH = "south";
  public static final String WEST = "west";

  public static final ArrayList<String> DIRECTIONS = new ArrayList(
    Arrays.asList(
      NORTH,
      EAST,
      SOUTH,
      WEST));
  public static final int[] X_DIFFS = {0, 1, 0, -1};
  public static final int[] Y_DIFFS = {1, 0, -1, 0};
}
