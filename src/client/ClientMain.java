package client;

/**
 * Created by Anton, Petter, Dragan & Sven on 2016-11-15.
 */
public class ClientMain {

    public static void main(String[] args) {
        Model model = new Model();
        //ReaderThread rt = new ReaderThread(model);
        SignalingThread st = new SignalingThread(model);
        st.start();
        GUI gui = new GUI(model);
        ImageProcessingThread IPT = new ImageProcessingThread(model, gui);
      //  rt.start();
        IPT.start();
    }
}
