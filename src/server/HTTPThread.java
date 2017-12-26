package server;

import se.lth.cs.eda040.realcamera.AxisM3006V;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Petter, Dragan, Anton & Sven on 01/12/16.
 */
public class HTTPThread extends Thread {

        private CameraControl monitor;
        private ServerSocket outputSocket;
        private AxisM3006V myCamera;


    public HTTPThread(CameraControl monitor, AxisM3006V myCamera) {
            this.monitor = monitor;
            this.myCamera = myCamera;
        }

        public void run() {
            byte[] jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(80);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("HTTP server operating at port " + 80 + ".");

            while (true) {
                try {
                    // The 'accept' method waits for a client to connect, then
                    // returns a socket connected to that client.
                    Socket clientSocket = serverSocket.accept();

                    // The socket is bi-directional. It has an input stream to read
                    // from and an output stream to write to. The InputStream can
                    // be read from using read(...) and the OutputStream can be
                    // written to using write(...). However, we use our own
                    // getLine/putLine methods below.
                    InputStream is = clientSocket.getInputStream();
                    OutputStream os = clientSocket.getOutputStream();

                    // Read the request
                    String request = getLine(is);

                    // The request is followed by some additional header lines,
                    // followed by a blank line. Those header lines are ignored.
                    String header;
                    boolean cont;
                    do {
                        header = getLine(is);
                        cont = !(header.equals(""));
                    } while (cont);

                    System.out.println("HTTP request '" + request
                            + "' received.");

                    // Interpret the request. Complain about everything but GET.
                    // Ignore the file name.
                    if (request.substring(0,4).equals("GET ")) {
                        // Got a GET request. Respond with a JPEG image from the
                        // camera. Tell the client not to cache the image
                        putLine(os, "HTTP/1.0 200 OK");
                        putLine(os, "Content-Type: image/jpeg");
                        putLine(os, "Pragma: no-cache");
                        putLine(os, "Cache-Control: no-cache");
                        putLine(os, "");                   // Means 'end of header'

                        if (!myCamera.connect()) {
                            System.out.println("Failed to connect to camera!");
                            System.exit(1);
                        }
                        int len = myCamera.getJPEG(jpeg, 0);

                        os.write(jpeg, 0, len);
                        myCamera.close();
                    }
                    else {
                        // Got some other request. Respond with an error message.
                        putLine(os, "HTTP/1.0 501 Method not implemented");
                        putLine(os, "Content-Type: text/plain");
                        putLine(os, "");
                        putLine(os, "No can do. Request '" + request
                                + "' not understood.");

                        System.out.println("Unsupported HTTP request!");
                    }
                    os.flush();                      // Flush any remaining content
                    clientSocket.close();	          // Disconnect from the client
                }
                catch (IOException e) {
                    System.out.println("Caught exception " + e);
                }
            }
        }

    private static String getLine(InputStream s)
            throws IOException {
        boolean done = false;
        String result = "";

        while(!done) {
            int ch = s.read();        // Read
            if (ch <= 0 || ch == 10) {
                // Something < 0 means end of data (closed socket)
                // ASCII 10 (line feed) means end of line
                done = true;
            }
            else if (ch >= ' ') {
                result += (char)ch;
            }
        }

        return result;
    }

    private static void putLine(OutputStream s, String str)
            throws IOException {
        s.write(str.getBytes());
        s.write(CRLF);
    }

    private static final byte[] CRLF      = { 13, 10 };

}



