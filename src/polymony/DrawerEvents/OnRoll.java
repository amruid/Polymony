package polymony.DrawerEvents;
import javafx.stage.Stage;
import polymony.Gamelogic.GameInterface;
import polymony.Main.Drawer;
public class OnRoll implements Drawer{
    GameInterface spielLogik;
    private Stage s;
    public OnRoll(Stage st,GameInterface sp){
        this.s = st;
        this.spielLogik = sp;
    }
    public void handle(){
        
    }
    
}
