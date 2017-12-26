package server;

import se.lth.cs.eda040.realcamera.AxisM3006V;

/**
 * Created by Petter, Anton, Dragan & Sven on 2016-11-14.
 */
public class Main {

    public static void main(String[] args) {
        AxisM3006V cam = new AxisM3006V();
        CameraControl monitor = new CameraControl();
        FrameGrabberThread fgt = new FrameGrabberThread(monitor, cam);
        OutputThread out = new OutputThread(monitor, Integer.parseInt(args[0]));
        InputThread in = new InputThread(monitor);
        HTTPThread httpThread = new HTTPThread(monitor, cam);
        fgt.start();
        out.start();
        in.start();
        httpThread.start();
    }
}
