import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.Paths;
import java.util.Arrays;

public class NTFSSectorReader implements AutoCloseable{
    private final FileChannel fileChannel;
    private final int sectorSize;

    public NTFSSectorReader(Path filePath, int sectorSize) throws IOException {
        this.fileChannel = FileChannel.open(filePath, StandardOpenOption.READ);
        this.sectorSize = sectorSize;
    }

    public byte[] readSector(long sectorNumber) throws IOException {
        long position = sectorNumber * sectorSize;
        ByteBuffer buffer = ByteBuffer.allocate(sectorSize);
        fileChannel.position(position);
        fileChannel.read(buffer);
        return buffer.array();
    }

    public void close() throws IOException {
        fileChannel.close();
    }
}

class Test {
    public static void main(String[] args) {
        Path filePath = Paths.get("F:");
        int sectorSize = 512;
        long sectorNumber = 1234;

        try (NTFSSectorReader reader = new NTFSSectorReader(filePath, sectorSize)) {
            byte[] sectorData = reader.readSector(sectorNumber);
            System.out.println("Sector data: " + toHexString(sectorData));
        } catch (IOException e) {
            System.err.println("Error reading sector: " + e.getMessage());
        }
    }

    private static String toHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
