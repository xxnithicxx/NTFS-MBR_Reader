import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class EntryReader implements AutoCloseable {
    private final String filePath;

    public EntryReader(String filePath) {
        this.filePath = filePath;
    }

    public String[] read(String entryHexString) {
        String[] pairsArray = entryHexString.split(" ");
        ArrayList<String> stringArray = new ArrayList<>();
        int maxEntry = pairsArray.length / 32;

        for (int i = 0; i < maxEntry; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < 32; j++) {
                stringBuilder.append(pairsArray[i * 32 + j]);
                if (j < 31) {
                    stringBuilder.append(" ");
                }
            }
            //Check ending up reading entries.
            if (stringBuilder.toString().startsWith("00")) {
                break;
            }
            stringArray.add(stringBuilder.toString());

        }
        // Convert to static array
        String[] resArray = new String[stringArray.size()];
        for (int i = 0; i < stringArray.size(); i++) {
            resArray[i] = stringArray.get(i);
        }

        return resArray;
    }

    public static int startSectorOfRDET(int nSectorPerCl, int nSectorPerBs, int sizeFAT, int numberOfFat, int startClOfRDET) {
        return nSectorPerBs + sizeFAT * numberOfFat + nSectorPerCl * (startClOfRDET - 2);
    }

    public String[] readEntry() {
        int nSectorPerCl = 0;
        int startClOfRDET = 0;
        int nSectorPerBs = 0;
        int sizeFAT = 0;
        int numberOfFat = 0;


        // Get RDET info
        int sectorSize = 512;
        int sectorIndex = 0;
        try (SectorReader reader = new SectorReader(new FileInputStream(filePath), sectorSize)) {
            byte[] sectorData = reader.readSector(sectorIndex);
            String hexString = Utils.bytesToHexString(sectorData);

            nSectorPerCl = reader.nSectorPerCluster(hexString);
            startClOfRDET = reader.StartClusterOfRDET(hexString);
            nSectorPerBs = reader.nSectorOfBoostSector(hexString);
            sizeFAT = reader.sizeOfFAT(hexString);
            numberOfFat = reader.numberOfFAT(hexString);
        } catch (Exception e) {
            System.err.println("Error reading Boot : " + e.getMessage());
        }
        int startRDET = startSectorOfRDET(nSectorPerCl, nSectorPerBs, sizeFAT, numberOfFat, startClOfRDET);

        int sizeRDET = 512;
        String entryHexString = null;
        try (SectorReader reader = new SectorReader(new FileInputStream(filePath), sizeRDET)) {
            byte[] sectorData = reader.readSector(startRDET);
            entryHexString = Utils.bytesToHexString(sectorData);
        } catch (IOException e) {
            System.err.println("Error reading RDET: " + e.getMessage());
        }


        // Entry reader
        assert entryHexString != null;
        String[] entryArray = new String[entryHexString.length() / 32];
        try (EntryReader reader = new EntryReader(entryHexString)) {
            entryArray = reader.read(entryHexString);
        } catch (Exception e) {
            System.err.println("Error reading sector: " + e.getMessage());
        }

        return entryArray;
    }

    public boolean typeEntry(String typeByte) {
        return !Objects.equals(typeByte, "0F");
    }

    public ArrayList<ArrayList<String>> splitIntoItem(String[] Entrys) {
        ArrayList<ArrayList<String>> res = new ArrayList<>();
        ArrayList<String> temp = new ArrayList<>();
        for (String i : Entrys) {
            String typeByte = Utils.getHexValueFromSector("0x0B", i, 1);
            boolean type = typeEntry(typeByte);

            temp.add(i);
            if (type) {
                res.add(temp);
                temp = new ArrayList<>();

            }
        }
        
        return res;
    }

    @Override
    public void close() {

    }
}

class EntryTestReader {
    public static void main(String[] args) throws IOException {
        String filePath = "\\\\.\\D:";
        try (EntryReader entryReader = new EntryReader(filePath)) {
            String[] Entrys=entryReader.readEntry();

            ArrayList<ArrayList<String>> items= entryReader.splitIntoItem(Entrys);
            System.out.println((items));
        }
    }
}