package Reader;

import Entity.Global;
import Helper.Utils;

import java.io.FileInputStream;

public class NTFSReader {
    public NTFSReader() {
        getInfo();
    }

    private void getInfo() {
//        Đọc VBR của ổ đĩa đang xét
        try (SectorReader sectorReader = new SectorReader(new FileInputStream(Global.mainPath), 512)) {
            String sectorData = Utils.bytesToHexString(sectorReader.readSector(0));

            Global.bytesPerSector = Utils.hexStringToDecimal(Utils.littleToBigEndian(Utils.getHexValueFromSector("0x0B", sectorData, 2)));
            Global.sectorPerCluster = Utils.hexStringToDecimal(Utils.littleToBigEndian(Utils.getHexValueFromSector("0x0D", sectorData, 1)));
            Global.MFTStart = Utils.hexStringToDecimal(Utils.littleToBigEndian(Utils.getHexValueFromSector("0x30", sectorData, 8)));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
