package com.fichtepaulsen.polymony.Gamelogic.Fields;

import com.fichtepaulsen.polymony.Gamelogic.Game;
import com.fichtepaulsen.polymony.Gamelogic.Player.Player;

public abstract class OwnableField extends Field {
    public int price;
    private Player owner;
    public String name;
    protected int rent;

    private int houseAmount;
    private boolean isMortgage;
    private int mortgageAmount;

    // method to buy an ownableField
    public abstract void buyField(Player currentPlayer, Field[] fields);

    // method to get the rent a player has to pay on a field
    public abstract int getPayPrice(int sum);

    public boolean getIsMortgage() {
        return isMortgage;
    }

    public void setIsMortgage(boolean isMortgage) {
        this.isMortgage = isMortgage;
    }

    public void addMortgage() {
        owner.setBalance(owner.getBalance() + mortgageAmount);
        isMortgage = true;
    }

    public void freeMortgage() {
        if (isMortgage == true) {
            owner.setBalance(owner.getBalance() - (mortgageAmount + mortgageAmount * 1 / 10));
            isMortgage = false;
        }
    }

    public int getMortgageAmount() {
        return mortgageAmount;
    }

    public int getHouseAmount() {
        return houseAmount;
    }

    public void setHouseAmount(int houseAmount) {
        this.houseAmount = houseAmount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    @Override
    public void action(Game game) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from
                                                                       // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
