package com.fichtepaulsen.polymony.Gamelogic.Cards;

import com.fichtepaulsen.polymony.Gamelogic.Player.Player;

import com.fichtepaulsen.polymony.Gamelogic.Game;

public class JumpCard extends Card {
  private final int value; // value on how much the player moves from a field to another field
  private Player[] players;

  public JumpCard(int value, String title, boolean community) {
    super(title, community);
    this.value = value;
  }

  @Override
  public void action(Game game) {
    players = game.getAllPlayers();
    players[game.getCurrentPlayer().getIndex()]
        .setPosition(players[game.getCurrentPlayer().getIndex()].getPosition() + value);
  }
}
