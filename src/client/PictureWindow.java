package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Anton, Petter, Dragan & Sven on 2016-11-21.
 */
public class PictureWindow extends JFrame {

    private JPanel panel = new JPanel();
    private ImageIcon icon;

    public PictureWindow() {
        super();
        getContentPane().setLayout(new BorderLayout());
        icon = new ImageIcon();
        JLabel jlabel = new JLabel(icon);
        this.pack();
        this.setSize(640, 480);
        this.setVisible(true);
    }

    public void refreshImage(byte[] frame) {
        Image image = getToolkit().createImage(frame);
        getToolkit().prepareImage(image, -1, -1, null);
        icon.setImage(image);
        icon.paintIcon(this, this.getGraphics(), 0, 0);
    }
}
