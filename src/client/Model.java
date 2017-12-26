package client;

import java.io.OutputStream;
import java.util.*;

/**
 * Created by Petter, Anton, Dragan & Sven on 2016-11-14.
 */
public class Model {


    private final static long WINDOW_SIZE = 100;
    private PriorityQueue<ViewingInstance> queue;
    private long lastReceived;
    private HashMap<Integer, ReaderThread> readers;
    private LinkedList<byte[]> broadcastList;
    private long timeDifference = 0;
    private long lastImageTimeStamp = 0;
    private LinkedList<OutputStream> socketList;
    private int synchronization;
    private ViewingInstance v;

    /**
     * Creates a client side shared resource that the threads will interact with. Holds images and control messages. This is a thread safe class.
     */
    public Model() {
        queue = new PriorityQueue<>();
        lastReceived = 0;
        readers = new HashMap<Integer, ReaderThread>();
        broadcastList = new LinkedList<>();
        socketList = new LinkedList<>();
    }

    /**
     * Puts output from a server  in the shared resource.
     * @param in The data package that is recieved from a server.
     * @param name The port that the data package was retreived from.
     */
    public synchronized void ServerOutput(byte[] in, int name) {
        long imageCaptureTime = 0;
        if(in[0] == 0b10) {
            movieModeOn();
        } else if(in[0] == 0b1) {
            for(int i = 1; i < 8; i++) {
                imageCaptureTime = (imageCaptureTime << 8) + (in[i] & 0xff);
            }
            queue.offer(new ViewingInstance(name, imageCaptureTime, Arrays.copyOfRange(in, 9, in.length)));

        }
        notifyAll();
    }


    /**
     *  Returns the next image in the sequence that that it was taken. Blocking until there is an image to return.
     * @return The next picture.
     */
    public synchronized ViewingInstance getFrame() {

        if (synchronization == -1) {
            v = SynchronousMode();
        }
        else if (synchronization == 0) {
            v = AutoMode();
        } else {
            v = AsynchnronousMode();
        }
        notifyAll();
        return v;
    }

    public synchronized ViewingInstance SynchronousMode() {
        try {
            while(queue.isEmpty()) {
                wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ViewingInstance v = queue.poll();
        timeDifference = v.getTimeStamp() - lastImageTimeStamp;
        long timeToWait = timeDifference;
        timeDifference = timeDifference + System.currentTimeMillis();
        try {
            while(System.currentTimeMillis() < timeDifference) {
                wait(timeToWait);
            } } catch (Exception e) {
            System.out.println("ERROR: EXCEPTION CAST IN getFrame IN MODEL");
        }
        lastImageTimeStamp = v.getTimeStamp();
        notifyAll();
        return v;
    }

    public synchronized ViewingInstance AsynchnronousMode() {
        while(queue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        notifyAll();
        return queue.poll();
    }

    private ViewingInstance AutoMode() {
        try {
            while(queue.isEmpty()) {
                wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ViewingInstance v = queue.poll();
        timeDifference = v.getTimeStamp() - lastImageTimeStamp;
        long timeToWait = timeDifference;
        timeDifference = timeDifference + System.currentTimeMillis();
        try {
            while(System.currentTimeMillis() < timeDifference && timeDifference < WINDOW_SIZE ) {
                wait(timeToWait);
            } } catch (Exception e) {
            System.out.println("ERROR: EXCEPTION CAST IN getFrame IN MODEL");
        }
        lastImageTimeStamp = v.getTimeStamp();
        notifyAll();
        return v;
    }


    /**
     * Returns a control message that should be broadcasted to all connected servers. Blocking if there is no message.
     * @return The control message that should be sent to the servers.
     */
    public synchronized byte[] waitForBroadcast() {
        try{
            while(broadcastList.isEmpty()) {
                wait();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return broadcastList.poll();
    }

    /**
     * Opens a new connection to a server. If failed, the thread and connection expires.
     * @param port The port that the connection established on.
     */
    public synchronized void openConnection(int port) {
        ReaderThread rt = new ReaderThread(this, port);
        readers.put(port, rt);
        rt.start();
        notifyAll();
    }

    /**
     * Disconnects a from a server.
     * @param port The port of the connection that should be disconnected.
     */
    public synchronized void disconnect(int port) {
        System.out.println("TERMINATE");
        readers.remove(port).interrupt();
    }

    /**
     * Turns movie mode on. Stores a broadcast message for a SignalingThread to pick up.
     * Turns movie mode on. Stores a broadcast message for a SignalingThread to pick up.
     */
    public synchronized void movieModeOn() {
        byte[] movieMode = new byte[1];
        movieMode[0] = 0b10;
        broadcastList.addFirst(movieMode);
        notifyAll();
    }

    /**
     * Turns movie mode off. Stores a broadcast message for a SignalingThread to pick up.
     */

    public synchronized void movieModeOff(){
        byte[] movieMode = new byte[1];
        movieMode[0] = 0b100;
        broadcastList.addFirst(movieMode);
        notifyAll();
    }

    /**
     * Put an OutputStream from a socket. Needs to be used if broadcasting is required.
     * @param os The OutputStream from a socket.
     */
    public synchronized void putOutputStream(OutputStream os) {
        socketList.add(os);
        notifyAll();
    }

    /**
     * Returns a list of all known connected OutputStreams
     * @return All connected OutputStreams
     */
    public synchronized LinkedList<OutputStream> getSocketList(){
        notifyAll();
        return socketList;
    }

    public synchronized void setSynchronization(int i)  {
        synchronization = i;
        notifyAll();
    }

}