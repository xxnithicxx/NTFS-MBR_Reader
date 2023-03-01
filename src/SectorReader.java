import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class SectorReader implements AutoCloseable{
    private final DataInputStream inputStream;
    private final int sectorSize;

    public SectorReader(FileInputStream fileInputStream, int sectorSize) throws IOException {
        this.inputStream = new DataInputStream(fileInputStream);
        this.sectorSize = sectorSize;
    }

    public String readSector(int sectorNumber) throws IOException {
        int position = sectorNumber * sectorSize;
        byte[] sectorData = new byte[sectorSize];
        inputStream.readFully(sectorData,position,sectorSize);
        return Utils.bytesToHexString(sectorData);
    }

    public void close() throws IOException {
        inputStream.close();
    }

}

class Test {
    public static void main(String[] args) {
        String filePath = "\\\\.\\C:";
        int sectorSize = 512;
        int sectorNumber = 0;

        try (SectorReader reader = new SectorReader(new FileInputStream(filePath), sectorSize)) {
            String sectorData = reader.readSector(sectorNumber);
            System.out.println(sectorData);

        } catch (IOException e) {
            System.err.println("Error reading sector: " + e.getMessage());
        }
    }
}
