import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FakeServer implements AutoCloseable {
  private int port;
  private ServerSocket serverSocket;

  public FakeServer(int port) {
    this.port = port;
  }

  @Override
  public void close() throws Exception {
    this.serverSocket.close();
  }

  public void listen() throws IOException {
    try (ServerSocket serverSocket = new ServerSocket(this.port)) {

      while (true) {
        Socket socket = serverSocket.accept();

        Thread thread = new RequestProcessor(socket);
        thread.start();
      }
    }
  }

  public static void main(String[] args) throws IOException {
    try (FakeServer server = new FakeServer(getPortNumber(args.length == 1 ? args[0] : null))) {
      server.listen();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static int getPortNumber(String args) {
    try {
      if (args != null) {
        return Integer.parseInt(args);
      }
    } catch (NumberFormatException e) {
      // no-op
    }

    return 9876;
  }

}