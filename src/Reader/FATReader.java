package Reader;

import Helper.Utils;

import java.io.FileInputStream;
import java.io.IOException;

public class FATReader {
    public int startSector;

    public FATReader(int startSector) {
        this.startSector = startSector;
    }
    String FATDataString;
    public String[] readFAT(int startIndex) throws IOException {
        try (SectorReader sectorReader = new SectorReader(new FileInputStream("\\\\.\\E:"), 512)) {
             FATDataString= Utils.bytesToHexString(sectorReader.readSector(startSector));


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        int[] res=new int[512/4];
        String temp=(Utils.getHexValueFromIndex(startIndex,FATDataString,4));

        return null;
    }
}
