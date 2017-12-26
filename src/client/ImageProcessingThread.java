package client;

/**
 * Created by Anton, Petter, Dragan & Sven on 2016-11-15.
 */
public class ImageProcessingThread extends Thread {


    private Model model;
    private GUI gui;
    private ViewingInstance v;

    /**
     * Pulls a image from a shared resource and puts it in a GUI.
     * @param model The shared resource where the image is pulled.
     * @param gui The GUI that the image is put.
     */
    public ImageProcessingThread(Model model, GUI gui) {
        this.model = model;
        this.gui = gui;
    }

    public void run() {
        while(true) {
            v = model.getFrame();
            gui.putFrame(v.getImage(), v.getPort());
        }
    }
}
