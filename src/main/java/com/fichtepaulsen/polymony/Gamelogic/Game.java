package com.fichtepaulsen.polymony.Gamelogic;

import com.fichtepaulsen.polymony.Gamelogic.Cards.Card;
import com.fichtepaulsen.polymony.Gamelogic.Cards.MoneyCard;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.fichtepaulsen.polymony.Gamelogic.Fields.Field;
import com.fichtepaulsen.polymony.Gamelogic.Dice.Dice;
import com.fichtepaulsen.polymony.Gamelogic.Dice.NormalDice;
import com.fichtepaulsen.polymony.Gamelogic.Fields.ActionField;
import com.fichtepaulsen.polymony.Gamelogic.Fields.OwnableField;
import com.fichtepaulsen.polymony.Gamelogic.Fields.PrisonField;
import com.fichtepaulsen.polymony.Gamelogic.Fields.StartField;
import com.fichtepaulsen.polymony.Gamelogic.Fields.StreetField;
import com.fichtepaulsen.polymony.Gamelogic.Fields.TaxField;
import com.fichtepaulsen.polymony.Gamelogic.Fields.TrafficField;
import com.fichtepaulsen.polymony.Gamelogic.Fields.UtilityField;
import com.fichtepaulsen.polymony.Gamelogic.Player.HumanPlayer;
import com.fichtepaulsen.polymony.Gamelogic.Player.Player;
import com.fichtepaulsen.polymony.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;

public class Game implements GameInterface{
    private Player[] players;
    private Field[] fields;
    private Dice[] dices;
    private Card[] cards;
    private boolean keepActivePlayer;
    
    private int activePlayerIndex;
    
    public Game(){
    
    }
    
    @Override
    /*
    requires: integer number of players. 
    does: initializes players,fields and dice to start the game.
    */
    public void startGame(int playerCount){
        // create 40 fields in a fieldArray.    
        try {
            fields = readJson(40);
        } catch (Exception e) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, e.getMessage());        
        }
    
        // create playerArray with given playerCount.
        this.players = new Player[playerCount];
        // fill playerArray with human players.
        for(int i = 0; i < playerCount; i++){
            this.players[i] = new HumanPlayer(0, 30000, i);
        }
        activePlayerIndex = 0;
        
        // create diceArray with 2 dices.
        this.dices = new Dice[2];
        //fills array with 2 normal dices.
        for (int i = 0; i < dices.length; i++){
            this.dices[i] = new NormalDice();
        }
        
        //create cards and shuffle them
        /*    cards = new Card[Settings.getInstance().GameFields]; 
        try {
            cards = shuffle(readCardsJson(Settings.getInstance().GameFields));
        } catch (IOException e) {
           Logger.getLogger(Game.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        */
    }
    
    @Override
    /* 
    requires: -
    does: makes the next player active
    */
    public void nextTurn(){
        if (!keepActivePlayer){
            activePlayerIndex=(activePlayerIndex+1)%players.length;
        }

    }

    /* 
    requires: -
    returns: results of dices being rolled
    */
    @Override
    public int[] rollDices(){
        int lastPosition = getCurrentPlayer().getPosition();
        Player currentPlayer = getCurrentPlayer();
        //Returns an array of roll results
        int [] results = new int[dices.length];
        for (int i = 0;i<dices.length;i++){
            results[i] = dices[i].roll();
        }
        
        //Calculates the sum of roll results
        int gesamtZahl = 0;
        for (int value : results){
            gesamtZahl+=value;
        }
        //System.out.println("GesamtZahl = "+gesamtZahl);
       
        Player activePlayer = players[activePlayerIndex];
        //Tests for doublets
        boolean doublets =  isDoublets(results); 
        //Calculates next position after rolling the dices
        int newPos = (activePlayer.getPosition()  + gesamtZahl) % 40;           
        //Case where the player is in prison:
          if(activePlayer.getIsInPrison()==true){   
              if (doublets == false){

                  activePlayer.incrementPrisonAttemptCounter();
                  //When the player doesn't roll doublets for 3 rounds
                  if(activePlayer.getPrisonAttemptCounter()==3){                 
                  //he comes out of prison and moves
                  activePlayer.setIsInPrison(false);                            
                  activePlayer.setPosition(newPos);
                  activePlayer.setPrisonAttemptCounter(0);
                }  

              }
              //If the player rolls doublets during one of his 3 attempts
              else{                                                             
                //he comes out of prison and moves by the amount he rolled
                activePlayer.setIsInPrison(false);                                
                //(no additional move after these doublets)
                activePlayer.setPosition(newPos);                               
              }    
          }
          //Normal case:
          else{                                                     
            activePlayer.setPosition(newPos);                                   
            //Normal roll(player moves, activePlayerIndex increments,
            if (doublets == false){                                             
                keepActivePlayer = false;
                //doubletsCounter resets)
              activePlayer.setDoubletsCounter(0);
            }
            //Doublet roll(doubletCounter increments, activePlayerIndex stays untouched)
            else{                                                               
              activePlayer.incrementDoubletsCounter();
                keepActivePlayer = true;
                //When doubletCounter reaches 3, the player will be automatically moved to 
                if (activePlayer.getDoubletsCounter()==3){                       
                  //the prison field and activePlayerIndex increments
                  activePlayer.setIsInPrison(true);                             
                  activePlayer.setDoubletsCounter(0);
                  activePlayer.setPrisonAttemptCounter(0);
                  activePlayer.setPosition(10);
                }
            }
        }
        
        if(pastStart(lastPosition, newPos) && activePlayer.getDoubletsCounter()!=3){
            currentPlayer.setBalance(currentPlayer.getBalance() + 4000);
        }
        
        return results;
    }

    public static boolean isDoublets(int[] array){
        for(int i = 1; i < array.length; i++){
            if((array[0] != array[i])) 
            {
                return false;
            }
        }
        return true;
    }
    
    @Override
    /*
    requires: 
    returns: boolean if the current player is able to buy himself out of prison
    */
    public boolean isAbleToBuyOutOfPrison(){                                    
        Player activePlayer = players[activePlayerIndex];                       
        if(activePlayer.getIsInPrison()==true && activePlayer.getBalance()>=1000){
            return true;
        }
        else{
            return false;
        }
    }
    
    @Override
    /*
    requires: 
    does:  current player buys himself out of prison
    */
    public void prisonPayment(){                                                
        Player activePlayer = players[activePlayerIndex];
        activePlayer.setIsInPrison(false);
        activePlayer.setBalance(activePlayer.getBalance()-1000);    
    } 
    
    public Field[] readJson(int length) throws IOException, JSONException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {

        //Array der später zurückgegeben wird-
        Field[] temp = new Field[length];

        //Öffne die fields.json Datei und schreibe den Inhalt in jsonString
        InputStream in = this.getClass().getResourceAsStream("/setup.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String jsonString = "";

        String line = null;
        while ( (line = reader.readLine()) != null) {
            jsonString +=line;
        }

        //lade den String als JSONObject
        JSONObject obj = new JSONObject(jsonString);

        //öffne den fields array aus dem JSONObject
        JSONArray jsonArray = obj.getJSONArray("fields");

        //iteriere durch alle Einträge
        for (int i = 0; i < jsonArray.length(); i++){

            //lade das JSONObject am Index i
            JSONObject field = jsonArray.getJSONObject(i);
            
            //Hole den Typen bzw. den Klassenbezeichner des Feldes
            String fieldClassName = (String) field.get("type");
            
            //Je nachdem welche Klasse es ist wird der Konstruktor mit den jeweils gewünschten Werten aufgerufen und das Objekt in temp an Stelle des Indizes i geschrieben
            switch (fieldClassName)
            {
                case "StartField":
                    temp[i] = new StartField();
                    break;
                case "StreetField":
                    temp[i] = new StreetField((String)field.get("name"),(int)field.get("price"), getColor((int)field.get("color")));
                    break;
                case "ActionField":
                    temp[i] = new ActionField();
                    break;
                case "TaxField":
                    temp[i] = new TaxField((int)field.get("tax"),(String) field.get("name"),i);
                    break;
                case "TrafficField":
                    temp[i] = new TrafficField();
                    break;
                case "Prison":
                    temp[i] = new PrisonField();
                    break;
                case "Utility":
                    temp[i] = new UtilityField((String)field.get("name"),(int)field.get("price"));
                    break;
                default:
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "JSON Import does not work!");
                    break;
            }
        }
        return temp;
    }
    
    /*
        requires:
        returns: player object from the active player.
                 to get the player index: getIndex().
                 to get the player position: getPosition().
    */
    @Override
    public Player getCurrentPlayer() {
        return players[activePlayerIndex];
    }

    /*
        requires: index from a player 
        returns:  player object from players at the given index
    */ 
    @Override
    public Player getNthPlayer(int index) {
        return players[index];
    }

    /*
        requires: 
        returns: player array with all players
    */
    @Override
    public Player[] getAllPlayers() {
       return players; 
    }

    /*
        requires: 
        returns: dice array with all dices
    */
    @Override
    public Dice[] getAllDice() {
        return dices;
    }

    /*
        requires: 
        returns: field array with all fields
    */
    @Override
    public Field[] getAllFields() {
        return fields;
    }

    /*
    requires: index from a field 
    returns:  field object from fields at the given index
    */ 
    @Override
    public Field getNthField(int n) {
        return fields[n];
    }
    
    /*
    requires: Color index from json file
    returns:  Color object for the corresponding index
    */
    public static Color getColor(int n){
        // return a
        switch (n){
            case 1:
                return Color.BROWN;
            case 2:
                return Color.LIGHTBLUE;
            case 3:
                return Color.PINK;
            case 4:
                return Color.ORANGE;
            case 5:
                return Color.RED;
            case 6:
                return Color.YELLOW;
            case 7:
                return Color.GREEN;
            case 8:
                return Color.BLUE;
            default:
                //should never happen, maybe throw an exception
                return Color.BLACK;

        }
    }

    public Card[] readCardsJson(int length) throws IOException{
                //Array der später zurückgegeben wird-
        Card[] temp = new Card[length];

        //Öffne die fields.json Datei und schreibe den Inhalt in jsonString
        InputStream in = this.getClass().getResourceAsStream("/setup.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String jsonString = "";

        String line = null;
        while ( (line = reader.readLine()) != null) {
            jsonString +=line;
        }

        //lade den String als JSONObject
        JSONObject obj = new JSONObject(jsonString);

        //öffne den fields array aus dem JSONObject
        JSONArray jsonArray = obj.getJSONArray("cards");

        //iteriere durch alle Einträge
        for (int i = 0;i<jsonArray.length();i++){

            //lade das JSONObject am Index i
            JSONObject card = jsonArray.getJSONObject(i);
            //Hole den Typen bzw. den Klassenbezeichner des Feldes
            String cardClassName = (String) card.get("type");
            
            switch (cardClassName){
                case "MoneyCard": 
                    temp[i] = new MoneyCard((String) card.getString("text"),(int) card.get("value"));
                    break;
                default: 
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Card JSON import not working!");
            }
        }
        return temp;
    }
    
    private Card[] shuffle(Card[] array) {
        Random rnd = ThreadLocalRandom.current();
        
        for (int i = array.length - 1; i > 0; i--)
        {
          int index = rnd.nextInt(i + 1);
          // Simple swap
          Card a = array[index];
          array[index] = array[i];
          array[i] = a;
        }
        return array;
    }
    
    @Override
    /*
    requires: 
    does:  current player buys the ownableField he stands on
    */
    public void buyField(){
        Player activePlayer = getCurrentPlayer();
        OwnableField currentField = (OwnableField) fields[activePlayer.getPosition()];
        //player becomes owner of the ownableField
        currentField.setOwner(activePlayer);
        //Player loses as much money as the price of the ownableField 
        activePlayer.setBalance(activePlayer.getBalance() - currentField.getPrice());
        
    }
    
    @Override
    /*
    requires: 
    returns: boolean if the current player is able to buy the street he stands on
    */
    public boolean isAbleToBuyField(){                                    
        Player activePlayer = getCurrentPlayer();
        OwnableField currentField = (OwnableField) fields[activePlayer.getPosition()];                       
        if(activePlayer.getBalance() >= currentField.getPrice()){
            return true;
        }
        else{
            return false;
        }
    }
    
    @Override
    /*
    requires: integer for the last position of a player
              integer for the current position of a player
    returns:  boolean if a player past start in the last turn
    */
    public boolean pastStart(int lastPosition, int newPosition){
        if((newPosition - lastPosition) < 0){
            return true;
        }
        else{ 
            return false;
        }
    }
}
