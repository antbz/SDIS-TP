import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Client {
    public static void main(String[] args) throws IOException {
        if (args.length < 4 || args.length > 5) {
            System.err.println("Invalid number of arguments!");
            System.err.println("Usage: java Server <port no.>");
            return;
        }

        String message = Client.makeMessage(args);
        DatagramSocket socket = new DatagramSocket();
//        socket.setSoTimeout(2000);

        InetAddress address = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
        byte[] buf = message.getBytes(StandardCharsets.UTF_8);

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);

        buf = new byte[512];
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        String response = new String(packet.getData(), Charset.defaultCharset());
        System.out.println("Client: " + message + " : " + response);
        socket.close();
    }

    private static String makeMessage(String[] args) {
        String message = args[2] + " " + args[3];
        if (args.length == 5) message += " " + args[4];
        return message;
    }
}
