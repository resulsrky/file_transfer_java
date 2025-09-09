// UdpAlici.java (Güncellenmiş)
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class UdpAlici {

    public static void main(String[] args) {
        final int PORT = 9999;
        final int BUFFER_SIZE = 1024;

        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("[*] " + socket.getLocalSocketAddress() + " adresinde dinleme baslatildi...");

            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // 1. ADIM: Dosya adı paketini al
            socket.receive(packet);
            int filenameLength = packet.getLength();
            String receivedFilename = new String(packet.getData(), 0, filenameLength, StandardCharsets.UTF_8);

            System.out.println("[+] Gelen dosya adi: " + receivedFilename);

            // 2. ADIM: Dosyayı gelen isimle yazmaya başla
            try (FileOutputStream fos = new FileOutputStream(receivedFilename)) {

                while (true) {
                    // Veri paketini al
                    socket.receive(packet);
                    int dataLength = packet.getLength();
                    String senderAddress = packet.getAddress().getHostAddress();

                    // Eğer gelen paketin boyutu 0 ise, transferin bittiği anlamına gelir.
                    if (dataLength == 0) {
                        System.out.println("[-] Veri akisi sonlandi. Dosya alimi tamamlandi.");
                        break;
                    }

                    // Gelen veriyi (sadece dolu olan kısmı) dosyaya yaz
                    fos.write(packet.getData(), 0, dataLength);

                    System.out.println("[+] " + senderAddress + " adresinden " + dataLength + " byte'lik veri alindi.");
                }

                System.out.println("[*] '" + receivedFilename + "' basariyla kaydedildi.");
            }

        } catch (SocketException e) {
            System.err.println("Soket hatasi: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Dosya I/O hatasi: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("[*] Islem sonlandi.");
        }
    }
}