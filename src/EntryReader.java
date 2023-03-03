import java.io.FileInputStream;
import java.io.IOException;

public class EntryReader implements AutoCloseable {
    private final String entryHexString;

    public EntryReader(String entryHexString) throws IOException {
        this.entryHexString = entryHexString;
    }

    public String[] read(String entryHexString){
        String[] pairsArray = entryHexString.split(" ");
        String[] stringArray = new String[pairsArray.length / 32];

        for (int i = 0; i < stringArray.length; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < 32; j++) {
                stringBuilder.append(pairsArray[i * 64 + j]);
                if (j < 31) {
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
    public static void main(String[] args) throws IOException {
        String filePath = "\\\\.\\D:";

        int startRDET = 33;
        int sizeRDET = 512;

        // Sector reader
        String entryHexString = null;
        try (SectorReader reader = new SectorReader(new FileInputStream(filePath), sizeRDET)) {
            byte[] sectorData = reader.readSector(startRDET);
            entryHexString = Utils.bytesToHexString(sectorData);
            //System.out.println(entryHexString);

            //reader.printFAT(entryHexString);

        } catch (IOException e) {
            System.err.println("Error reading sector: " + e.getMessage());
        }

        // Entry reader
        try (EntryReader reader = new EntryReader(entryHexString)) {
            assert entryHexString != null;
            reader.read(entryHexString);

        } catch (Exception e) {
            System.err.println("Error reading sector: " + e.getMessage());
        }
    }
}
