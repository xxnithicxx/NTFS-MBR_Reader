package Reader;

import Entity.Global;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static Reader.EntryReader.startSectorFromCluster;

public class DataReader implements AutoCloseable {
    public byte[] read(int clusterIndex) {
        FATReader fat = new FATReader();
        ArrayList<Integer> clusterChain = fat.readFAT(clusterIndex);

        ArrayList<Byte> data = new ArrayList<>();

        for (int clusterId : clusterChain) {
            for (int i = 0; i < Global.sectorPerCluster; i++) {
                SectorReader sectorReader;
                try {
                    sectorReader = new SectorReader(new FileInputStream(Global.mainPath), 512);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Convert into SectorId
                long SectorId = startSectorFromCluster(Global.sectorPerCluster, Global.nSectorPerBs, Global.sizeFAT,
                        Global.numberOfFat, clusterId);
                SectorId += i;

                byte[] sectorData;
                try {
                    sectorData = sectorReader.readSector(SectorId);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                for (byte sectorDatum : sectorData) {
                    data.add(sectorDatum);
                }
            }
        }

        // Convert to static array
        byte[] resArray = new byte[data.size()];
        for (int i = 0; i < data.size(); i++) {
            resArray[i] = data.get(i);
        }

        return resArray;
    }

    @Override
    public void close() throws Exception {

    }
}