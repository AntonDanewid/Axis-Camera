package server;

import java.io.InputStream;

/**
 * Created by Anton, Petter, Dragan & Sven on 2016-11-14.
 */
public class InputThread extends Thread {

    private CameraControl monitor;

    public InputThread(CameraControl monitor) {
        this.monitor = monitor;
    }

    public void run () {
        while(true) {
            try {
                InputStream is = monitor.getInputStream();
                byte[] message = new byte[1];
                while (true) {
                    is.read(message, 0, 1);
                    monitor.setMode(message);
                    System.out.println(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}



