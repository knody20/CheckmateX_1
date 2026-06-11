package com.checkmatex.main;                //package declaration

//importing statements 
import com.checkmatex.ui.GameFrame;
import javax.swing.SwingUtilities;          
             

public class Main {
    public static void main(String[] args) {
    
        SwingUtilities.invokeLater(() -> {
            new GameFrame();
        });
    }
}
