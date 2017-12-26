package server;

import java.io.InputStream;

/**
 * Created by Petter, Anton, Dragan & Sven on 2016-11-14.
 */
public class CameraControl {

    private byte[] byteToSend;
    private boolean movieMode = true;
    private static final int SEND_SPEED = 5000;
    private long lastSent;
    private InputStream is;

    /**
     * Creates a shared resource for a server based camera. Stores images and server related attributes.
     */
    public CameraControl() {
        lastSent = 0;
    }

    /**
     * Stores an image in the shared resource CameraControl. The shared resource enters movie mode if motion was detected
     * @param jpg An image in JPG format, contained in a byte array.
     * @param timeStamp A timestamp for when the image was taken, contained in a byte array.
     * @param motionDet A boolean that tells if a camera detected motion.
     */
    public synchronized void putImage(byte[] jpg, byte[] timeStamp, boolean motionDet) {
       if (motionDet && !movieMode) {
            byte[] movieMessage = new byte[1];
            byteToSend = new byte[1];
            movieMessage[0] = 0b10; //The protocol for movieMode message;
            byteToSend = movieMessage; // set byteToSend as equal to the message
            movieMode = true;
        }
        else if(System.currentTimeMillis() - lastSent > SEND_SPEED || movieMode) { //Fills the message with the bytes at their correct position
            byte[] message = new byte[jpg.length + timeStamp.length + 1];
            byteToSend = new byte[jpg.length + timeStamp.length + 1]; //give byteToSend correct size
            byte frameMessage = 0b1;
            message[0] = frameMessage;
            int counter = 1;
            for(int i = 0; i < timeStamp.length; i++) {
                message[counter] = timeStamp[i];
                counter++;
            }
            for(int i = 0; i < jpg.length; i++) {
                message[counter] = jpg[i];
                counter++;
            }
            byteToSend =  message;
        }
        notifyAll();
    }


    /**
     * Getter method for a data package to be sent over a network connection.
     * @return The next data package to be sent.
     */
    public synchronized byte[] grabByteFrame() {
        try {
            while (byteToSend == null) {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lastSent = System.currentTimeMillis();
        notifyAll();
        return byteToSend;
    }

    /**
     * Clears the data package. For proper use, call this method after sending the message to avoid duplicates.
     */

    public synchronized void clearByteArray(){
        byteToSend = null;
    }

    /**
     * Sets the mode depending on the byte. Possible modes are movie mode or idle mode.
     * @param mode The mode that will be set. Decimal 2 = movie mode, decimal 4 = idle mode.
     */
    public synchronized void setMode(byte[] mode) { //The method that the InputThread calls.
        Byte temp = new Byte(mode[0]);
        int tempInt = temp.intValue();
        if(tempInt == 2) { //As defined in the protocol
            movieMode = true;
            System.out.println("moviemode is on");
        } else if(tempInt == 4) {
            movieMode = false;
            System.out.println("moviemode is off");
        }
        notifyAll();
    }

    /**
     * Store the servers input stream in the shared resource
     * @param is The input stream that the input shread is going to use
     */
    public synchronized void putInputStream(InputStream is) {
        this.is = is;
        notifyAll();
    }

    /**
     * Getter for the servers input stream. Should be used to be able to read messages sent to the server.
     * @return The input stream for the server.
     */
    public synchronized InputStream getInputStream(){
        while (is == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        notifyAll();
        return is;
    }

    /**
     * Sets the input stream to null. Should be used to insure proper usage after connection timeout
     */
    public synchronized void resetInputStream() {
        is = null;
        notifyAll();
    }
}
