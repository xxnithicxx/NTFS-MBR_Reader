package Reader;

import Entity.Global;
import Helper.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class EntryReader implements AutoCloseable {
    private final String filePath;
    private int nSectorPerCl;
    private int numberOfFat;
    private long startClOfRDET;
    private int nSectorPerBs;
    private int sizeFAT;

    public EntryReader(String filePath) {
        this.filePath = filePath;
        this.getInfo();
    }

    /**
     * Read all entries from RDET
     *
     * @return String array of entries
     */
    public String[] readEntry(String entryHexString) {
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

    public static long startSectorFromCluster(int nSectorPerCl, int nSectorPerBs, int sizeFAT, int numberOfFat,
                                              long ClusterIndex) {
        return nSectorPerBs + (long) sizeFAT * numberOfFat + nSectorPerCl * (ClusterIndex - 2);
    }

    public long startSectorFromCluster(long ClusterIndex) {
        return startSectorFromCluster(this.nSectorPerCl, this.nSectorPerBs, this.sizeFAT, numberOfFat, ClusterIndex);
    }

    public void getInfo() {
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

            Global.sizeFAT = sizeFAT;
            Global.startFAT = nSectorPerBs;
            Global.sectorPerCluster = nSectorPerCl;
            Global.numberOfFat = numberOfFat;
            Global.startClOfRDET = startClOfRDET;
            Global.nSectorPerBs = nSectorPerBs;
        } catch (Exception e) {
            System.err.println("Error reading Boot : " + e.getMessage());
        }
    }

    public String[] readEntryFromRDET() {
        long startRDET = startSectorFromCluster(nSectorPerCl, nSectorPerBs, sizeFAT, numberOfFat, startClOfRDET);

        StringBuilder entryHexString = new StringBuilder();
        try (SectorReader reader = new SectorReader(new FileInputStream(filePath), 512)) {
            String temp;
            do {
                byte[] sectorData = reader.readSector(startRDET++);
                temp = Utils.bytesToHexString(sectorData);
                entryHexString.append(temp);
            } while (temp.startsWith("00"));

        } catch (IOException e) {
            System.err.println("Error reading RDET: " + e.getMessage());
        }

        // Read entry
        if (entryHexString.length() % 32 != 0) {
            System.err.println("Error reading RDET: " + entryHexString.length());
        }

        String[] entryArray;
        try (EntryReader reader = new EntryReader(Global.mainPath)) {
            entryArray = reader.readEntry(entryHexString.toString());
        }

        return entryArray;
    }

    public String[] readEntryFromCluster(long startCluster){
        long startRDET = startSectorFromCluster(nSectorPerCl, nSectorPerBs, sizeFAT, numberOfFat, startCluster);

        StringBuilder entryHexString = new StringBuilder();
        try (SectorReader reader = new SectorReader(new FileInputStream(filePath), 512)) {
            String temp;
            do {
                byte[] sectorData = reader.readSector(startRDET++);
                temp = Utils.bytesToHexString(sectorData);
                entryHexString.append(temp);
            } while (temp.startsWith("00"));

        } catch (IOException e) {
            System.err.println("Error reading RDET: " + e.getMessage());
        }

        // Read entry
        if (entryHexString.length() % 32 != 0) {
            System.err.println("Error reading RDET: " + entryHexString.length());
        }

        String[] entryArray;
        try (EntryReader reader = new EntryReader(Global.mainPath)) {
            entryArray = reader.readEntry(entryHexString.toString());
        }

        return entryArray;
    }

    public static boolean typeEntry(String typeByte) {
        return !Objects.equals(typeByte, "0F");
    }

    public static ArrayList<ArrayList<String>> splitIntoItem(String[] Entrys) {
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

    public int getNSectorPerCl() {
        return nSectorPerCl;
    }

    public int getNumberOfFat() {
        return numberOfFat;
    }

    public long getStartClOfRDET() {
        return startClOfRDET;
    }

    public int getNSectorPerBs() {
        return nSectorPerBs;
    }

    public int getSizeFAT() {
        return sizeFAT;
    }

    @Override
    public void close() {

    }

    public static void main(String[] args) throws IOException {
        String filePath = "\\\\.\\D:";

        try (EntryReader entryReader = new EntryReader(filePath)) {
            System.out.println(Arrays.toString(entryReader.readEntryFromRDET()));
        }

    }
}