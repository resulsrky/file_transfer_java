// UdpGonderici.java (NIO - DatagramChannel versiyonu)
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class UdpGonderici {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Kullanim: java UdpGonderici <hedef_ip_adresi>");
            return;
        }
        String hostIp = args[0];

        final String FILENAME = "Ekran Görüntüsü - 2025-08-27 11-56-52.png";
        final int PORT = 9999;
        final int BUFFER_SIZE = 1024;

        File file = new File(FILENAME);
        if (!file.exists()) {
            System.err.println("Hata: " + FILENAME + " dosyasi bulunamadi.");
            return;
        }

        System.out.println("[*] Hedef IP adresi olarak ayarlandi: " + hostIp);

        // try-with-resources ile kanalın ve dosya akışının otomatik kapanmasını sağlıyoruz.
        try (DatagramChannel channel = DatagramChannel.open();
             FileInputStream fis = new FileInputStream(file);
             FileChannel fileChannel = fis.getChannel()) {

            // Alıcının adresini tanımla
            InetSocketAddress targetAddress = new InetSocketAddress(hostIp, PORT);

            // 1. ADIM: Dosya adını gönder
            byte[] filenameBytes = FILENAME.getBytes(StandardCharsets.UTF_8);
            ByteBuffer filenameBuffer = ByteBuffer.wrap(filenameBytes);
            channel.send(filenameBuffer, targetAddress);
            System.out.println("[+] Dosya adi gonderiliyor: " + FILENAME);

            // 2. ADIM: Dosya içeriğini gönder
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            while (fileChannel.read(buffer) > 0) {
                // Buffer'ı okuma moduna geçir (gönderime hazırla)
                buffer.flip();
                channel.send(buffer, targetAddress);
                System.out.println("[+] " + buffer.limit() + " byte'lik parca gonderildi.");
                // Buffer'ı bir sonraki yazma işlemi için temizle
                buffer.clear();
            }

            // 3. ADIM: Transferin bittiğini belirtmek için boş bir buffer gönder
            channel.send(ByteBuffer.allocate(0), targetAddress);
            System.out.println("[*] Dosya gonderme islemi tamamlandi.");

        } catch (IOException e) {
            System.err.println("NIO Hatasi: " + e.getMessage());
            e.printStackTrace();
        }
    }
}