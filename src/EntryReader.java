import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

public class EntryReader implements AutoCloseable {
    private final String entryHexString;

    public EntryReader(String entryHexString) throws IOException {
        this.entryHexString = entryHexString;
    }

    public String[] read(String entryHexString){
        String[] pairsArray = entryHexString.split(" ");
        String[] stringArray = new String[pairsArray.length / 64];

        for (int i = 0; i < stringArray.length; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < 64; j++) {
                stringBuilder.append(pairsArray[i * 64 + j]);
                if (j < 63) {
                    stringBuilder.append(" ");
                }
            }
            stringArray[i] = stringBuilder.toString();
            //System.out.println(stringArray[i]);
        }

//        System.out.println(Arrays.toString(stringArray));
        return stringArray;
    }


    @Override
    public void close() throws Exception {

    }
}

class EntryTestReader {
    public static void main(String[] args) {
        String filePath = "\\\\.\\E:";
        int startRDET = 33;
        int sizeRDET = 512;

        // Sector reader
        String entryHexString = null;
        try (SectorReader reader = new SectorReader(new FileInputStream(filePath), sizeRDET)) {
            byte[] sectorData = reader.readSector(startRDET);
            entryHexString = bytesToHexString(sectorData);
            //System.out.println(entryHexString);

            //reader.printFAT(entryHexString);

        } catch (IOException e) {
            System.err.println("Error reading sector: " + e.getMessage());
        }

        // Entry reader
        try (EntryReader reader = new EntryReader(entryHexString)) {
            reader.read(entryHexString);

        } catch (Exception e) {
            System.err.println("Error reading sector: " + e.getMessage());
        }
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }

        return sb.toString();
    }
}