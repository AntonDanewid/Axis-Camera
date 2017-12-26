package server;

import java.io.*;
import java.net.*;
import java.io.IOException;

/**
 * Created by Petter, Anton, Dragan & Sven on 2016-11-14.
 */

public class OutputThread extends Thread {

    private ServerSocket outputSocket;
    private CameraControl monitor;
    private int port;

    /**
     * A thead the outputs the images from the shared resource CameraControl on a network connection.
     * @param monitor The shared resource that holds the images
     */
    public OutputThread(CameraControl monitor, int port)  {
        this.port = port;
        this.monitor = monitor;

    }

    public void run() {
        while(true) {
        try {
            outputSocket = new ServerSocket(port);
            Socket s = outputSocket.accept();
            s.setTcpNoDelay(true);
            OutputStream os = s.getOutputStream();
            monitor.putInputStream(s.getInputStream());
            while(true) {
                os.write(monitor.grabByteFrame());
                monitor.clearByteArray();
            }
        } catch (IOException io) {
            monitor.resetInputStream();
            io.printStackTrace();
            try {
                outputSocket.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        }
    }
}
