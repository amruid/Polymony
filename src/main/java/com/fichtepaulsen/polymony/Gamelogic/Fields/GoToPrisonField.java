package com.fichtepaulsen.polymony.Gamelogic.Fields;

import com.fichtepaulsen.polymony.Gamelogic.Game;

public class GoToPrisonField extends SpecialField {
    public GoToPrisonField() {
    }

    @Override
    public void action(Game game) {

        game.getActivePlayer().setInPrison();
    }
}
