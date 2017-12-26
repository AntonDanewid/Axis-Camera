package server;

import se.lth.cs.eda040.realcamera.AxisM3006V;

/**
 * Created by Anton, Petter, Dragan & Sven on 2016-11-14.
 */

public class FrameGrabberThread extends Thread {

    private CameraControl monitor;
    private AxisM3006V camera;

    /**
     * A thread that pulls images from a camera as fast as possible.
     * @param monitor A shared resource, CameraControl that the thread will store images in.
     * @param camera The camera that the thread pulls the images from.
     */
    public FrameGrabberThread(CameraControl monitor, AxisM3006V camera) {
        this.monitor = monitor;
        this.camera = camera;
    }

    public void run() {
        camera.init();
        //camera.setProxy("argus-3.student.lth.se", 4000);
        camera.connect(); //Readies the camera for use
 /*       if(!camera.connect()) {
            System.out.println("ERROR: CAMERA IS NOT CONNECTED");
        }*/
        while(true) {
            byte[] jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
            camera.getJPEG(jpeg, 0);
            byte[] time = new byte[8];
            camera.getTime(time, 0);
            monitor.putImage(jpeg, time,  camera.motionDetected());
        }
     }
}

