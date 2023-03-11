package Reader;

import Helper.Utils;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static Helper.Utils.byteArrayToAsciiString;
import static Helper.Utils.hexStringToByteArray;

public class SectorReader implements AutoCloseable{
    private final DataInputStream inputStream;
    private final int sectorSize;

    public SectorReader(FileInputStream fileInputStream, int sectorSize) throws IOException {
        this.inputStream = new DataInputStream(fileInputStream);
        this.sectorSize = sectorSize;
    }

    public byte[] readSector(long sectorIndex) throws IOException {
        byte[] sectorData = new byte[sectorSize];
        long position = sectorIndex * (long) sectorSize;

        if (inputStream.skip(position) != position) {
            throw new IOException("Unable to skip to sector");
        }

        inputStream.read(sectorData, 0, sectorSize);

        return sectorData;
    }

    public int nSectorPerCluster(String sectorData) {
        String hexString = Utils.getHexValueFromSector("0x0D", sectorData, 1);
        return Utils.hexStringToDecimal(Utils.littleToBigEndian(hexString));
    }

//    TODO: Implement this method with NTFS file system
    public String readFileSystem(String sectorData)
    {
        String res=Utils.getHexValueFromSector("0x52",sectorData,8);
        return byteArrayToAsciiString(hexStringToByteArray(res)).trim();
    }

    public int StartClusterOfRDET(String sectorData) {
        String res = Utils.getHexValueFromSector("0x2C", sectorData, 4);
        return Utils.hexStringToDecimal(Utils.littleToBigEndian(res));
    }

    public int nSectorOfBoostSector(String sectorData) {
        String res = Utils.getHexValueFromSector("0x0E", sectorData, 2);
        return Utils.hexStringToDecimal(Utils.littleToBigEndian(res));
    }

    public int sizeOfFAT(String sectorData) {
        String res = Utils.getHexValueFromSector("0x24", sectorData, 4);
        return Utils.hexStringToDecimal(Utils.littleToBigEndian(res));
    }
    public int numberOfFAT(String sectorData) {
        String res = Utils.getHexValueFromSector("0x10", sectorData, 1);
        return Utils.hexStringToDecimal(Utils.littleToBigEndian(res));
    }

    public void close() throws IOException {

    }

}