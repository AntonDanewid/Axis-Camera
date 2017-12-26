package client;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

import javax.swing.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;


/**
 * Created by Petter, Anton, Dragan & Sven on 2016-11-14.
 */
public class ReaderThread extends Thread {

    private Socket readerSocket;
    private ImageIcon icon;
    private Model monitor;
    private int port;


    /**
     * Creates a thread that reads incomming connections from a server.
     * @param monitor The shared resource where the received data packages are put.
     * @param port The port that the thread should connect to.
     */
    public ReaderThread(Model monitor, int port) {
        this.monitor = monitor;
        this.port = port;

    }

    public void run() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            readerSocket = new Socket(address, port);
            System.out.println("Socket created");
//            readerSocket.connect();
            InputStream is = readerSocket.getInputStream();
            monitor.putOutputStream(readerSocket.getOutputStream());
            while(true) {
                byte[] temp = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
                is.read(temp, 0, AxisM3006V.IMAGE_BUFFER_SIZE);
                monitor.ServerOutput(temp, port);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR: No connection");
            //Om connection dör, döda denna tråd. Skapa en ny från GUI.
            //Fixa Connect To Server knapp i GUI som skapar nya instanser av denna tråd.
        }


    }



}
