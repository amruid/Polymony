package polymony.DrawerEvents;

import javafx.stage.Stage;
import polymony.Gamelogic.GameInterface;
import polymony.Main.Drawer;

public class OnNewGame implements Drawer {
    GameInterface spielLogik;
    Stage s;
       
    public OnNewGame(Stage st,GameInterface sp) {
        this.s = st;
        this.spielLogik = sp;
    }
    
    public void handle () {
    }
}
