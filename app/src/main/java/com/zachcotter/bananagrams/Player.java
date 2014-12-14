package com.zachcotter.bananagrams;

public class Player {

  private String name;

  /*
  CREATE:
  curl -X POST -H "Content-Type: application/json" -d '{"bananagrams_player": {"name": "test2"}}' http://0.0.0.0:3000/bananagrams_players.json
   */
  public Player(String name) {
    this.name = name;
  }


}
