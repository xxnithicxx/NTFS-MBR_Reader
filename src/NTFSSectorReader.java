import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class NTFSSectorReader implements AutoCloseable{
    private final DataInputStream inputStream;
    private final int sectorSize;

    public NTFSSectorReader(FileInputStream fileInputStream, int sectorSize) throws IOException {
        this.inputStream = new DataInputStream(fileInputStream);
        this.sectorSize = sectorSize;
    }

    public String readSector(int sectorNumber) throws IOException {
        int position = sectorNumber * sectorSize;
        byte[] sectorData = new byte[sectorSize];
        inputStream.readFully(sectorData,position,sectorSize);
        return Utils.bytesToHexString(sectorData);
    }

    public int printFAT(String sectorData) throws IOException {
        String[] hexArray = sectorData.split(" ");
        int count = 0;
        for (int i = 0; i < hexArray.length; i++) {
            String hexElement = hexArray[i];
            // int decimalElement = Integer.parseInt(hexElement, 16);
            System.out.print(hexElement + " ");
            count = count + 1;
            if (count == 16){
                count = 0;
                System.out.print("\n");
            }
        }
        return 0;
    }

    public int nSectorPerCluster(String sectorData) throws IOException {
        String[] hexArray = sectorData.split(" ");
        return Integer.parseInt(hexArray[13]);
    }

    public String StartClusterOfRDET(String sectorData) throws IOException {
        // 44->47
        String res = "";
        String[] hexArray = sectorData.split(" ");
        for (int i = 44; i <= 47; i++) {
            String hexElement = hexArray[i];
            int decimalElement = Integer.parseInt(hexElement, 16);
            System.out.print(decimalElement + " ");
        }
        return hexArray[44];
    }

    public int nSectorOfBoostSector(String sectorData) throws IOException {
        // 15->16
        int res = 0;
        String[] hexArray = sectorData.split(" ");
        for (int i = 15; i <= 16; i++) {
            String hexElement = hexArray[i];
            int decimalElement = Integer.parseInt(hexElement, 16);
            res += decimalElement;
            //System.out.print(decimalElement + " ");
        }
        return res;
    }

    public int startSectorOfFAT1(String sectorData, int nSectorPerBs) throws IOException {
        return nSectorPerBs*512;
    }



    public void close() throws IOException {
        inputStream.close();
    }

}

class Test {
    public static void main(String[] args) {
        String filePath = "\\\\.\\C:";
        int sectorSize = 512;
        int sectorNumber = 0;

        try (NTFSSectorReader reader = new NTFSSectorReader(new FileInputStream(filePath), sectorSize)) {
            String sectorData = reader.readSector(sectorNumber);
            System.out.println(sectorData);
            reader.printFAT(sectorData);
            //System.out.println(reader.printFAT(sectorData));

            // Number Sector per Cluster -> Done
            System.out.println("Number of Sector per Cluster: ");
            System.out.println(reader.nSectorPerCluster(sectorData));

            // Start Cluster of RDET
            System.out.println("Start Cluster of RDET: ");
            System.out.println(reader.StartClusterOfRDET(sectorData));

            // Number of Sector of Boost Sector
            System.out.println("Number of Sector of Boost Sector: ");
            int nSectorPerBs = reader.nSectorOfBoostSector(sectorData);
            System.out.println(nSectorPerBs);



            // Start Sector of FAT1
            System.out.println("Start Sector of FAT1: ");
            System.out.println(reader.startSectorOfFAT1(sectorData, nSectorPerBs));


        } catch (IOException e) {
            System.err.println("Error reading sector: " + e.getMessage());
        }




    }
}
