package Reader;

import Entity.Global;
import Helper.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import static Reader.EntryReader.startSectorFromCluster;

public class DataReader implements AutoCloseable {
    public String[] read(int clusterIndex) throws IOException {
        int sizeCluster = Global.sectorPerCluster * 512;
        // Create Sector Reader
        SectorReader sectorReader = new SectorReader(new FileInputStream(Global.mainPath), sizeCluster);

        FATReader fat = new FATReader();
        ArrayList<Integer> clusterChain = fat.readFAT(clusterIndex);

        ArrayList<String> stringArray = new ArrayList<>();

        for (int i = 0; i < clusterChain.size(); i++) {
            if (i == clusterChain.size() - 1) {
                // Check EOF
                //----------------------------------------------------------------------------Here
                System.out.println("Checking EOF...");
            } else {
                int clusterId = clusterChain.get(i);

                // Convert into SectorId
                long SectorId = startSectorFromCluster(Global.sectorPerCluster, Global.nSectorPerBs, Global.sizeFAT,
                        Global.numberOfFat, clusterId);

                // For testing
                System.out.println("SectorId = " + SectorId);

                byte[] sectorData = sectorReader.readSector(SectorId);
                String entryHexString = Utils.bytesToHexString(sectorData);

                stringArray.add(entryHexString);

                // For testing
//                System.out.println(entryHexString);
//                break;
            }
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