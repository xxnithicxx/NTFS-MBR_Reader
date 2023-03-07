package Reader;

import Entity.Global;
import Helper.Utils;

import java.io.FileInputStream;
import java.util.ArrayList;

public class FATReader {
    public int startSector = Global.startFAT;



    String FATDataString;

    public ArrayList<Integer> readFAT(int startIndex) {
        int nElementPerSector = 512 / 4;
        int sectorIndex = startIndex / nElementPerSector;
        try (SectorReader sectorReader = new SectorReader(new FileInputStream(Global.mainPath), 512)) {
            FATDataString = Utils.bytesToHexString(sectorReader.readSector(startSector + sectorIndex));


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        ArrayList<Integer> res = new ArrayList<>(512 / 4);
        String temp;
        startIndex *= 4 * 3;
        do {
            temp = Utils.getHexValueFromIndex(startIndex, FATDataString, 4);
            System.out.println(Utils.hexStringToDecimal(temp));
            res.add(Utils.hexStringToDecimal(temp));

        } while (!temp.equals("FF FF FF FF"));


        return res;
    }

    public static void main(String[] args) {
        FATReader fatReader=new FATReader();
        System.out.println(fatReader.readFAT(2));
    }
}
