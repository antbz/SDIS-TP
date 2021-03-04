import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;

public class Client {
    public static void main(String[] args) throws IOException {
        if (args.length < 4 || args.length > 5) {
            System.err.println("Invalid number of arguments!");
            System.err.println("Usage: java Client <mcast_addr> <mcast_port> <oper> <opnd> *");
            return;
        }

        String[] serviceInfo = getService(args[0], Integer.parseInt(args[1]));

        sendCommand(args, serviceInfo[0], Integer.parseInt(serviceInfo[1]));
    }

    private static String[] getService(String address, int port) throws IOException {
        InetAddress mcastAddr = InetAddress.getByName(address);
        InetSocketAddress mcastGroup = new InetSocketAddress(mcastAddr, port);

        MulticastSocket mcastSocket = new MulticastSocket(port);
        NetworkInterface netInt = NetworkInterface.getByName("bge0");
        mcastSocket.joinGroup(mcastGroup, netInt);

        byte[] buf = new byte[512];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        mcastSocket.receive(packet);

        mcastSocket.leaveGroup(mcastGroup, netInt);
        mcastSocket.close();

        return new String(packet.getData()).trim().split(" ");
    }

    private static void sendCommand(String[] args, String servAddr, int servPort) throws IOException {
        String message = Client.makeMessage(args);

        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(2000);

        InetAddress address = InetAddress.getByName(servAddr);
        byte[] buf = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, servPort);
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
