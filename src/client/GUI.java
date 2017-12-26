package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Anton, Petter, Dragan & Sven on 2016-11-15.
 */
public class GUI extends JFrame  {

    private ImageIcon icon;
    private JPanel panel = new JPanel();
    private JButton discButton = new JButton("Disconnect");
    private JButton connectButton = new JButton("Connect");
    private HashMap<Integer, PictureWindow> windowMap;
    private Model model;
    private JTextField text;
    private JButton movieButton = new JButton("Movie mode on");
    private JButton syncButton = new JButton("SyncMode: OFF");

    public GUI(Model model)   {
            super();
            windowMap = new HashMap<Integer, PictureWindow>();
            JLabel label = new JLabel("Enter the port number you wish to connect to or disconnect from.");
            text = new JTextField();
            add(label, BorderLayout.CENTER);
            add(text, BorderLayout.SOUTH);
            syncButton.addActionListener(new SyncButton(this));
            connectButton.addActionListener(new ConnectButton(this));
            discButton.addActionListener(new DisconnectButton(this));
            movieButton.addActionListener(new MovieButton(this));
            panel.add(discButton);
            panel.add(connectButton);
            panel.add(movieButton);
            panel.add(syncButton);
            add(panel);
        this.setSize(300, 300);
        this.setVisible(true);
        this.model = model;
        }


    public void putFrame (byte[] frame, int port) {
        windowMap.get(port).refreshImage(frame);
    }

    public HashMap<Integer, PictureWindow> getMap(){
        return windowMap;
    }

    public void connect() {
        try {
        windowMap.put(Integer.parseInt(text.getText()), new PictureWindow());
            model.openConnection(Integer.parseInt(text.getText()));
        }
        catch(Exception e) {
            text.setText(text.getText() + " is not a valid port number.");
        }
    }

    public void disconnect() {
        model.disconnect(Integer.parseInt(text.getText()));
        windowMap.remove(Integer.parseInt(text.getText())).dispose();
    }

    public void movieMode() {
        if(movieButton.getText().equals("Movie mode: ON")) {
            movieButton.setText("Movie mode: OFF");
            model.movieModeOff();
        } else {
            movieButton.setText("Movie mode: ON");
            model.movieModeOn();
        }
    }


    public void syncButtonMode() {
        if(syncButton.getText().equals("SyncMode: OFF")) {
            syncButton.setText("SyncMode: ON");
            model.setSynchronization(1);
        } else if(syncButton.getText().equals("SyncMode: ON"))
        {
            syncButton.setText("SyncMode AUTO");
            model.setSynchronization(0);
        } else {
            syncButton.setText("SyncMode: OFF");
            model.setSynchronization(-1);
        }
    }
}
