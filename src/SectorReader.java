import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;


public class SectorReader implements AutoCloseable {
    private final DataInputStream inputStream;
    private final int sectorSize;

    public SectorReader(FileInputStream fileInputStream, int sectorSize) throws IOException {
        this.inputStream = new DataInputStream(fileInputStream);
        this.sectorSize = sectorSize;
    }

//    public byte[] readSector(int sectorNumber) throws IOException {
//        int position = sectorNumber * sectorSize;
//        byte[] sectorData = new byte[sectorSize];
//
//        inputStream.read(sectorData,position,sectorSize);
//        //return byteArrayToString(sectorData);
//        return sectorData;
//    }
    public byte[] readSector(int sectorNumber) throws IOException {
        byte[] sectorData = new byte[sectorSize];
        long position = (long) sectorNumber * (long) sectorSize;
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
        String hexString = Utils.getHexValue("0x0D", sectorData, 1);
        return Utils.hexStringToDecimal(hexString);
    }

    public int StartClusterOfRDET(String sectorData) throws IOException {
        String res = Utils.getHexValue("0x2C", sectorData, 4);
        return Utils.hexStringToDecimal(res);
    }

    public int nSectorOfBoostSector(String sectorData) throws IOException {
        String res = Utils.getHexValue("0x0E", sectorData, 2);
        return Utils.hexStringToDecimal(res);
    }

    public int sizeOfFAT(String sectorData) throws IOException {
        String res = Utils.getHexValue("0x24", sectorData, 4);
        return Utils.hexStringToDecimal(res);
    }

    public void close() throws IOException {
        inputStream.close();
    }
}

class TestRead {
    public static void main(String[] args) {
        String filePath = "\\\\.\\E:";
        int sectorSize = 512;
        int sectorNumber = 0;

        try (SectorReader reader = new SectorReader(new FileInputStream(filePath), sectorSize)) {
            byte[] sectorData = reader.readSector(sectorNumber);
            String hexString = Utils.bytesToHexString(sectorData);
            //System.out.println(hexString);

            reader.printFAT(hexString);
            //System.out.println(reader.printFAT(sectorData));

            // Number Sector per Cluster -> Done
            System.out.println("Number of Sector per Cluster: ");
            int nSectorPerCl = reader.nSectorPerCluster(hexString);
            System.out.println(nSectorPerCl);

            // Start Cluster of RDET
            System.out.println("Start Cluster of RDET: ");
            int startClOfRDET = reader.StartClusterOfRDET(hexString);
            System.out.println(startClOfRDET);

            // Number of Sector of Boost Sector
            System.out.println("Number of Sector of Boost Sector: ");
            int nSectorPerBs = reader.nSectorOfBoostSector(hexString);
            System.out.println(nSectorPerBs);

            // Start Sector of FAT1
            System.out.println("Start Sector of FAT1: ");
            System.out.println(nSectorPerBs);

            // Size of FAT
            System.out.println("Size of FAT: ");
            int sizeFAT = reader.sizeOfFAT(hexString);
            System.out.println(sizeFAT);


        } catch (IOException e) {
            System.err.println("Error reading sector: " + e.getMessage());
        }
    }


}