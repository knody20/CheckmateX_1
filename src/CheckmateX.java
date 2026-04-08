import javax.swing.SwingUtilities;

// Main class - just launches the game window
// Project: CheckMate X
// Subject: Design and Analysis of Algorithms (DAA)
// Team Members: [Your Names Here]

public class CheckmateX {
    public static void main(String[] args) {
        // run on EDT thread as required for swing
        SwingUtilities.invokeLater(() -> {
            new GameWindow();
        });
    }
}
