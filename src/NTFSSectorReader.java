import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class NTFSSectorReader implements AutoCloseable{
    private final DataInputStream inputStream;
    private final int sectorSize;

    public NTFSSectorReader(FileInputStream fileInputStream, int sectorSize) throws IOException {
        this.inputStream = new DataInputStream(fileInputStream);
        this.sectorSize = sectorSize;
    }

    public String readSector(long sectorNumber) throws IOException {
        long position = sectorNumber * sectorSize;
        byte[] sectorData = new byte[sectorSize];
        inputStream.readFully(sectorData);
        return byteArrayToString(sectorData);
    }

    public void close() throws IOException {
        inputStream.close();
    }

    private static String byteArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

class Test {
    public static void main(String[] args) {
        String filePath = "\\\\.\\F:";
        int sectorSize = 512;
        long sectorNumber = 1234;

        try (NTFSSectorReader reader = new NTFSSectorReader(new FileInputStream(filePath), sectorSize)) {
            String sectorData = reader.readSector(sectorNumber);
            //System.out.println(sectorData);
            StringBuilder hexBuilder = new StringBuilder();
            for (int i = 0; i < sectorData.length(); i += 2) {
                hexBuilder.append(sectorData.substring(i, i + 2)).append(" ");
            }
            String hexString = hexBuilder.toString().toUpperCase();
            System.out.println(hexString);
        } catch (IOException e) {
            System.err.println("Error reading sector: " + e.getMessage());
        }




    }
}
