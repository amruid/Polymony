package com.fichtepaulsen.polymony.Gamelogic.Fields;

import com.fichtepaulsen.polymony.Gamelogic.Game;

public class TaxField extends SpecialField {
    final private int tax;

    public TaxField(int tax, String name, int index) {
        this.tax = tax;
        this.name = name;
    }

    @Override
    public void action(Game game) {
        game.getActivePlayer().setBalance(game.getActivePlayer().getBalance() - tax);
    }
}
