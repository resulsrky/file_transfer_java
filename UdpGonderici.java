// UdpGonderici.java (Güncellenmiş)
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UdpGonderici {

    public static void main(String[] args) {
        final String FILENAME = "WhatsApp Image 2025-08-02 at 23.56.37.jpeg";
        final String HOST = "127.0.0.1"; // Alıcının IP adresini yazın.
        final int PORT = 9999;
        final int BUFFER_SIZE = 1024;

        File file = new File(FILENAME);
        if (!file.exists()) {
            System.err.println("Hata: " + FILENAME + " dosyasi bulunamadi.");
            return;
        }

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(HOST);

            // 1. ADIM: Dosya adını gönder
            byte[] filenameBytes = FILENAME.getBytes(StandardCharsets.UTF_8);
            DatagramPacket filenamePacket = new DatagramPacket(filenameBytes, filenameBytes.length, address, PORT);
            socket.send(filenamePacket);
            System.out.println("[+] Dosya adi gonderiliyor: " + FILENAME);

            // 2. ADIM: Dosya içeriğini parça parça gönder
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;

                while ((bytesRead = fis.read(buffer)) != -1) {
                    DatagramPacket packet = new DatagramPacket(buffer, bytesRead, address, PORT);
                    socket.send(packet);
                    System.out.println("[+] " + bytesRead + " byte'lik parca gonderildi.");
                }

                // 3. ADIM: Transferin bittiğini belirtmek için 0-byte'lık bir paket gönder
                byte[] endMarker = new byte[0];
                DatagramPacket endPacket = new DatagramPacket(endMarker, 0, address, PORT);
                socket.send(endPacket);

                System.out.println("[*] Dosya gonderme islemi tamamlandi.");
            }

        } catch (UnknownHostException e) {
            System.err.println("Host bulunamadi: " + HOST);
            e.printStackTrace();
        } catch (SocketException e) {
            System.err.println("Soket olusturma hatasi: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Dosya okuma/gonderme hatasi: " + e.getMessage());
            e.printStackTrace();
        }
    }
}