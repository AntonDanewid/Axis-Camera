package client;

import javax.swing.text.View;

/**
 * Created by Petter, Anton, Dragan & Sven on 2016-11-14.
 */
public class ViewingInstance implements Comparable {

    private byte[] image;
    private long timeStamp;
    private int port;

    /**
     * A picture along with information
     * @param port
     * @param timeStamp
     * @param image
     */
    public ViewingInstance(int port, long timeStamp, byte[] image) {
        this.image = image;
        this.port = port;
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;

    }

    public int getPort() {
        return port;
    }

    public byte[] getImage() {
        return image;
    }



    @Override
    public int compareTo(Object o) {
        ViewingInstance other = (ViewingInstance) o;
        if(other.getTimeStamp() > timeStamp) {
            return -1;
        } else if (other.getTimeStamp() == timeStamp) {
            return 0;
        } else {
            return 1;
        }
    }
}
