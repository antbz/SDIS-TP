import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastAdvertiser implements Runnable {
    private final String log_msg;
    private final MulticastSocket mSocket;
    private final DatagramPacket mPacket;

    public MulticastAdvertiser(String mcastAddr, String mcastPort, String sAddr, String sPort) throws IOException {
        this.mSocket = new MulticastSocket(Integer.parseInt(mcastPort));
        InetAddress mcastGroup = InetAddress.getByName(mcastAddr);
        String message = sAddr + " " + sPort;
        byte[] buf = message.getBytes();
        this.mPacket = new DatagramPacket(buf, buf.length, mcastGroup, Integer.parseInt(mcastPort));

        this.log_msg = "multicast: " + mcastAddr + " " + mcastPort + " : " + sAddr + " " + sPort;
    }

    @Override
    public void run() {
        try {
            mSocket.send(mPacket);
            System.out.println(log_msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
