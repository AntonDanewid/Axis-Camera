package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Anton, Petter, Dragan & Sven on 2016-11-21.
 */
public class ConnectButton implements ActionListener {

    private GUI gui;

    public ConnectButton(GUI gui) {
        this.gui = gui;
    }


    public void actionPerformed(ActionEvent e) {
        gui.connect();
    }
}
