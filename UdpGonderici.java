// UdpGonderici.java (Güncellenmiş - IP Adresi Argüman Olarak Alınıyor)
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UdpGonderici {

    public static void main(String[] args) {

        // 1. ADIM: Komut satırı argümanı kontrolü
        // Programla birlikte bir IP adresi verilip verilmediğini kontrol et.
        if (args.length == 0) {
            System.err.println("Hata: Lutfen bir hedef IP adresi belirtin.");
            System.err.println("Kullanim: java UdpGonderici <hedef_ip_adresi>");
            return; // Programı sonlandır
        }

        // Verilen ilk argümanı IP adresi olarak al.
        String hostIp = args[0];

        // Ayarlar
        final String FILENAME = "WhatsApp Image 2025-08-02 at 23.56.37.jpeg";
        final int PORT = 9999;
        final int BUFFER_SIZE = 1024;

        File file = new File(FILENAME);
        if (!file.exists()) {
            System.err.println("Hata: " + FILENAME + " dosyasi bulunamadi.");
            return;
        }

        System.out.println("[*] Hedef IP adresi olarak ayarlandi: " + hostIp);

        try (DatagramSocket socket = new DatagramSocket()) {
            // 2. ADIM: Argümandan gelen IP adresini kullan
            InetAddress address = InetAddress.getByName(hostIp);

            // Dosya adını gönder
            byte[] filenameBytes = FILENAME.getBytes(StandardCharsets.UTF_8);
            DatagramPacket filenamePacket = new DatagramPacket(filenameBytes, filenameBytes.length, address, PORT);
            socket.send(filenamePacket);
            System.out.println("[+] Dosya adi gonderiliyor: " + FILENAME);

            // Dosya içeriğini parça parça gönder
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;

                while ((bytesRead = fis.read(buffer)) != -1) {
                    DatagramPacket packet = new DatagramPacket(buffer, bytesRead, address, PORT);
                    socket.send(packet);
                    System.out.println("[+] " + bytesRead + " byte'lik parca gonderildi.");
                }

                // Transferin bittiğini belirtmek için 0-byte'lık bir paket gönder
                byte[] endMarker = new byte[0];
                DatagramPacket endPacket = new DatagramPacket(endMarker, 0, address, PORT);
                socket.send(endPacket);

                System.out.println("[*] Dosya gonderme islemi tamamlandi.");
            }

        } catch (UnknownHostException e) {
            System.err.println("Hata: Host bulunamadi veya gecersiz IP adresi: " + hostIp);
        } catch (IOException e) {
            System.err.println("Dosya veya soket hatasi: " + e.getMessage());
        }
    }
}