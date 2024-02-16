import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class FakeHttpServer {
    private int port;

    public FakeHttpServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {

            while (true) {
                Socket socket = serverSocket.accept();

                String content = handleRequest(socket.getInputStream());
                sendResponse(socket.getOutputStream(), content);

                socket.close();
            }
        }
    }

    private String handleRequest(InputStream is) throws IOException {
        StringBuffer buffer = new StringBuffer();

        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line = "";

        while ((line = br.readLine()).trim().length() != 0) {
            System.out.println(line);
            buffer.append(String.format("%s\n", line));
        }
        System.out.println(">> END");

        return buffer.toString();
    }

    private void sendResponse(OutputStream os, String content) throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(String.format("HTTP/1.1 200 Ok\r\nContent-Length: %d\r\nContent-Type: text/text\r\n\r\n%s",
                content.length(), content));
        bw.flush();
    }

    public static void main(String[] args) throws NumberFormatException, IOException {
        (new FakeHttpServer(Integer.parseInt(args[0]))).start();
    }
}