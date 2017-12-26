package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Anton, Petter, Dragan & Sven on 11/28/2016.
 */
public class SyncButton implements ActionListener {

    private GUI gui;

    public SyncButton(GUI gui) {
        this.gui = gui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        gui.syncButtonMode();
    }
}
