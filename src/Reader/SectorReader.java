package Reader;

import Helper.Utils;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class SectorReader implements AutoCloseable{
    private final DataInputStream inputStream;
    private final int sectorSize;

    public SectorReader(FileInputStream fileInputStream, int sectorSize) throws IOException {
        this.inputStream = new DataInputStream(fileInputStream);
        this.sectorSize = sectorSize;
    }

    public byte[] readSector(long sectorIndex) throws IOException {
        byte[] sectorData = new byte[sectorSize];
        long position = (long) sectorIndex * (long) sectorSize;
        inputStream.skip(position);
        int bytesRead = inputStream.read(sectorData, 0, sectorSize);
        if (bytesRead < sectorSize) {
            throw new IOException("Unable to read entire sector");
        }
        return sectorData;
    }

    public void printFAT(String sectorData) throws IOException {
        String[] hexArray = sectorData.split(" ");
        int count = 0;
        for (String hexElement : hexArray) {
            // int decimalElement = Integer.parseInt(hexElement, 16);
            System.out.print(hexElement + " ");
            count = count + 1;
            if (count == 16) {
                count = 0;
                System.out.print("\n");
            }
        }
    }

    public int nSectorPerCluster(String sectorData) throws IOException {
        String hexString = Utils.getHexValueFromSector("0x0D", sectorData, 1);
        return Utils.hexStringToDecimal(Utils.littleToBigEndian(hexString));
    }

    public int StartClusterOfRDET(String sectorData) throws IOException {
        String res = Utils.getHexValueFromSector("0x2C", sectorData, 4);
        return Utils.hexStringToDecimal(Utils.littleToBigEndian(res));
    }

    public int nSectorOfBoostSector(String sectorData) throws IOException {
        String res = Utils.getHexValueFromSector("0x0E", sectorData, 2);
        return Utils.hexStringToDecimal(Utils.littleToBigEndian(res));
    }

    public int sizeOfFAT(String sectorData) throws IOException {
        String res = Utils.getHexValueFromSector("0x24", sectorData, 4);
        return Utils.hexStringToDecimal(Utils.littleToBigEndian(res));
    }
    public int numberOfFAT(String sectorData) {
        String res = Utils.getHexValueFromSector("0x10", sectorData, 1);
        return Utils.hexStringToDecimal(Utils.littleToBigEndian(res));
    }

    public void close() throws IOException {
        inputStream.close();
    }

}