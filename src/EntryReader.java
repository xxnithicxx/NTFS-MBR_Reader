import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.ArrayList;

public class EntryReader implements AutoCloseable {
    private final String entryHexString;

    public EntryReader(String entryHexString) throws IOException {
        this.entryHexString = entryHexString;
    }

    public String[] read(String entryHexString){
        String[] pairsArray = entryHexString.split(" ");
        ArrayList<String> stringArray = new ArrayList<String>();
        int maxEntry = pairsArray.length / 32;

        for (int i = 0; i < maxEntry; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < 32; j++) {
                stringBuilder.append(pairsArray[i * 32 + j]);
                if (j < 31) {
                    stringBuilder.append(" ");
                }
            }
            // Check ending up reading entries.
//            if (stringBuilder.toString().startsWith("00 00")) {
//                break;
//            }
            // stringArray[i] = stringBuilder.toString();
            stringArray.add(stringBuilder.toString());

        }
        // Convert to static array
        String[] resArray = new String[stringArray.size()];
        for (int i = 0; i < stringArray.size(); i++) {
            resArray[i] = stringArray.get(i);
        }

        return resArray;
    }
    @Override
    public void close() throws Exception {

    }
}

class EntryTestReader {
    public static void main(String[] args) {
        String filePath = "\\\\.\\E:";
        int nSectorPerCl = 0;
        int startClOfRDET = 0;
        int nSectorPerBs = 0;
        int sizeFAT = 0;



        // Get RDET info
        int sectorSize = 512;
        int sectorNumber = 0;
        try (SectorReader reader = new SectorReader(new FileInputStream(filePath), sectorSize)) {
            byte[] sectorData = reader.readSector(sectorNumber);
            String hexString = Utils.bytesToHexString(sectorData);

            nSectorPerCl = reader.nSectorPerCluster(hexString);
            startClOfRDET = reader.StartClusterOfRDET(hexString);
            nSectorPerBs = reader.nSectorOfBoostSector(hexString);
            sizeFAT = reader.sizeOfFAT(hexString);
        } catch (IOException e) {
            System.err.println("Error reading sector: " + e.getMessage());
        }




        // RDET reader
        int startRDET = startSectorOfRDET(nSectorPerCl, nSectorPerBs, sizeFAT, startClOfRDET);
        System.out.println(startRDET);
        int sizeRDET = 512;
        String entryHexString = null;
        try (SectorReader reader = new SectorReader(new FileInputStream(filePath), sizeRDET)) {
            byte[] sectorData = reader.readSector(startRDET);
            entryHexString = bytesToHexString(sectorData);
        } catch (IOException e) {
            System.err.println("Error reading sector: " + e.getMessage());
        }




        // Entry reader
        try (EntryReader reader = new EntryReader(entryHexString)) {
            String[] entryArray = reader.read(entryHexString);
            System.out.println(entryArray.length);
            for (int i=0; i < entryArray.length; i++){
                System.out.println(entryArray[i]);
            }

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
    private static int startSectorOfRDET(int nSectorPerCl, int nSectorPerBs, int sizeFAT, int startClOfRDET) {
        return nSectorPerBs + sizeFAT*2 + nSectorPerCl*(startClOfRDET-2);
    }
}