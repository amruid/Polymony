package com.fichtepaulsen.polymony.Gamelogic.Cards;

import com.fichtepaulsen.polymony.Gamelogic.Player.Player;

import com.fichtepaulsen.polymony.Gamelogic.Game;

public class JumpToCard extends Card {

    private final int position; // position on which the player jumps
    private final boolean getMoney;

    public JumpToCard(int position, String title, boolean getMoney, boolean community) {
        super(title, community);
        this.position = position;
        this.getMoney = getMoney;
    }

    @Override
    public void action(Game game) {
        Player currentPlayer = game.getCurrentPlayer();
        if (game.pastStart(currentPlayer.getPosition(), position) && getMoney) {
            currentPlayer.changeBalance(4000);
        }
        currentPlayer.setPosition(position);

    }
}
