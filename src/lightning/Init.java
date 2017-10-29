package lightning;

import javax.swing.*;

/**
 * Let's go!
 */
public class Init {

    public static void main(String[] args) {
        JFrame f = new Gui();
        f.setTitle("Lightning generator");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(1000, 600);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        f.setResizable(false);
    }
}
