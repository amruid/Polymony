package com.fichtepaulsen.polymony.Gamelogic.Fields;

import com.fichtepaulsen.polymony.Gamelogic.Game;

public class ActionField extends SpecialField {
    boolean community;
    boolean freeParking;

    public ActionField(boolean community, boolean freeParking) {
        this.community = community;
        this.freeParking = freeParking;
    }

    @Override
    public void action(Game game) {
        if (!this.freeParking) {
            if (this.community) {
                game.getCommunityCards()[0].action(game);
            } else {
                game.getChanceCards()[0].action(game);
            }
        }
    }

}
