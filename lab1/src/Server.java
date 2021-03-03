import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Server {
    private static final HashMap<String, String> table = new HashMap<>();
    private static DatagramSocket socket;
    private static DatagramPacket packet;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Invalid number of arguments!");
            System.err.println("Usage: java Server <port no.>");
            return;
        }
        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException err) {
            System.err.println("Invalid argument!");
            System.err.println("Usage: java Server <port no.>");
            return;
        }

        Server.socket = new DatagramSocket(port);

        while (true) {
            String message = Server.listen();
            String response = Server.respond(message);

            Server.packet.setData(response.getBytes(StandardCharsets.UTF_8));
            Server.socket.send(Server.packet);
        }
    }

    private static String listen() throws IOException {
        byte[] buffer = new byte[512];
        Server.packet = new DatagramPacket(buffer, buffer.length);
        Server.socket.receive(Server.packet);

        String data_str = new String(packet.getData(), Charset.defaultCharset());
        System.out.println("Server: " + data_str);
        return data_str;
    }

    private static String respond(String message) {
        String[] args = message.trim().split(" ");
        String response;

        if (args[0].equals("LOOKUP") && args.length > 1) {
            String address = Server.lookup(args[1]);
            response = (address == null)? "NOT FOUND" : address;
        } else if (args[0].equals("REGISTER") && args.length > 2) {
            response = String.valueOf(Server.register(args[1], args[2]));
        } else {
            response = "ERROR";
        }

        return response;
    }

    private static int register(String name, String address) {
        if (table.containsKey(name)) return -1;
        table.put(name, address);
        return table.size();
    }

    private static String lookup(String name) {
        return table.get(name);
    }
}
