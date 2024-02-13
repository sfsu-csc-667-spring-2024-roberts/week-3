import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class RequestProcessor extends Thread {
  private static int threadCounter;

  private Socket socket;
  private int requestId;

  public RequestProcessor(Socket socket) {
    this.socket = socket;
    this.requestId = threadCounter++;
  }

  public void process() throws IOException {
    System.out.println(String.format("Processing request [thread %d]", this.requestId));
    String result = processRequest(this.socket.getInputStream());

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println(String.format("Sending response [thread %d]", this.requestId));
    sendResponse(this.socket.getOutputStream(), result);

    this.socket.close();
  }

  private String processRequest(InputStream stream) throws IOException {
    StringBuffer buffer = new StringBuffer();

    BufferedReader reader = new BufferedReader(
        new InputStreamReader(stream));
    String line;

    while ((line = reader.readLine()).trim().length() != 0) {
      buffer.append(String.format("%s\r\n", line));
    }

    return buffer.toString();
  }

  private void sendResponse(OutputStream output, String result) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(output))) {

      writer.write("HTTP/1.1 200 Ok\r\n");
      writer.write("Content-Type: text/text\r\n");
      writer.write(String.format("Content-Length: %d\r\n", result.length()));
      writer.write("\r\n");
      writer.write(result);

      writer.flush();
    }
  }

  @Override
  public void run() {
    try {
      this.process();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
