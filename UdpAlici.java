// UdpAlici.java (NIO - DatagramChannel versiyonu)
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

public class UdpAlici {

    public static void main(String[] args) {
        final int PORT = 9999;
        final int BUFFER_SIZE = 1024; // Buffer boyutu artırılabilir (örn: 8192)

        // try-with-resources ile kanalın otomatik kapanmasını sağlıyoruz.
        try (DatagramChannel channel = DatagramChannel.open()) {

            // 1. ADIM: Soket seçeneğini ayarla (bind işleminden önce)
            channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);

            // 2. ADIM: Kanalı belirtilen porta bağla
            channel.bind(new InetSocketAddress(PORT));

            System.out.println("[*] " + channel.getLocalAddress() + " adresinde dinleme baslatildi...");

            // Veri alışverişi için ByteBuffer oluştur
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            // 3. ADIM: Dosya adı paketini al
            // receive() metodu gönderenin adresini döndürür.
            channel.receive(buffer);

            // Buffer'ı okuma moduna al (limit'i mevcut pozisyona ayarlar, pozisyonu 0 yapar)
            buffer.flip();

            // Gelen veriyi String'e çevir
            String receivedFilename = StandardCharsets.UTF_8.decode(buffer).toString();
            System.out.println("[+] Gelen dosya adi: " + receivedFilename);

            // 4. ADIM: Dosya içeriğini al ve yaz
            try (FileOutputStream fos = new FileOutputStream(receivedFilename)) {
                while (true) {
                    // Buffer'ı bir sonraki yazma işlemi için temizle
                    buffer.clear();
                    SocketAddress senderAddress = channel.receive(buffer);

                    // Buffer'ı okuma moduna al
                    buffer.flip();

                    int dataLength = buffer.remaining(); // Alınan veri boyutu

                    if (dataLength == 0) {
                        System.out.println("[-] Veri akisi sonlandi. Dosya alimi tamamlandi.");
                        break;
                    }

                    // Buffer'daki veriyi dosyaya yazmak için bir byte dizisine aktar
                    byte[] data = new byte[dataLength];
                    buffer.get(data);
                    fos.write(data);

                    System.out.println("[+] " + senderAddress + " adresinden " + dataLength + " byte'lik veri alindi.");
                }
                System.out.println("[*] '" + receivedFilename + "' basariyla kaydedildi.");
            }

        } catch (IOException e) {
            System.err.println("NIO Hatasi: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("[*] Islem sonlandi.");
        }
    }
}