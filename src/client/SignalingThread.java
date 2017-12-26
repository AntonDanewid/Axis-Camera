package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Anton, Petter, Dragan & Sven on 2016-11-14.
 */
public class SignalingThread extends Thread {


    private Model monitor;

    /**
     * A thread that sends control messages to all connected servers.
     * @param monitor The shared resource where the messages are.
     */
    public SignalingThread(Model monitor) {
        this.monitor = monitor;
    }

    public void run() {
        while (true) {
            byte[] message = monitor.waitForBroadcast();
            LinkedList<OutputStream> sockets = monitor.getSocketList();
            for (OutputStream os : sockets) {
                try {
                    os.write(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
