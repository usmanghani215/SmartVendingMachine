

import javax.swing.SwingUtilities;

/**
 * Entry point for the Smart Vending Machine Simulator.
 */
public class Main {
    public static void main(String[] args) {
        // Initialize the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            VendingMachineGUI gui = new VendingMachineGUI();
            gui.setVisible(true);
        });
    }
}
