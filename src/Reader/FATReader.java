package Reader;

import Entity.Global;
import Helper.Utils;

import java.io.FileInputStream;
import java.util.ArrayList;

public class FATReader {
    public int startFAT = Global.startFAT;

    String FATDataString;

    public ArrayList<Integer> readFAT(int startIndex) {
        int nElementPerSector = 512 / 4;
        int sectorIndex = startIndex / nElementPerSector;
        try (SectorReader sectorReader = new SectorReader(new FileInputStream(Global.mainPath), 512)) {
            FATDataString = Utils.bytesToHexString(sectorReader.readSector(startFAT + sectorIndex));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        ArrayList<Integer> res = new ArrayList<>(512 / 4);
        String temp;
        startIndex -= sectorIndex * nElementPerSector;
        startIndex *= 4 * 3;
        do {
            temp = Utils.getHexValueFromIndex(startIndex, FATDataString, 4);
            temp = Utils.hexToLittleEndian(temp);
            System.out.println(temp);
            res.add(Utils.hexStringToDecimal(temp));
            startIndex += 4 * 3;
        } while (!temp.equals("0F FF FF FF"));

        return res;
    }
}
