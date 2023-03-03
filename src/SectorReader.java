import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;

public class SectorReader implements AutoCloseable {
    private final DataInputStream inputStream;
    private final int sectorSize;

    public SectorReader(FileInputStream fileInputStream, int sectorSize) throws IOException {
        this.inputStream = new DataInputStream(fileInputStream);
        this.sectorSize = sectorSize;
    }

    public byte[] readSector(long sectorNumber) throws IOException {
        long position = sectorNumber * sectorSize;
        byte[] sectorData = new byte[sectorSize];
        inputStream.readFully(sectorData);
        //return byteArrayToString(sectorData);
        return sectorData;
    }

    private static String byteArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public int printFAT(String sectorData) throws IOException {
        String[] hexArray = sectorData.split(" ");
        int count = 0;
        for (int i = 0; i < hexArray.length; i++) {
            String hexElement = hexArray[i];
            // int decimalElement = Integer.parseInt(hexElement, 16);
            System.out.print(hexElement + " ");
            count = count + 1;
            if (count == 16) {
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

    public int StartClusterOfRDET(String sectorData) throws IOException {
        // 44->47
        String res = "";
        String[] hexArray = sectorData.split(" ");
        for (int i = 44; i <= 47; i++) {
            String hexElement = hexArray[i];
            res += hexArray[i];
            res += " ";

            int decimalElement = Integer.parseInt(hexElement, 16);
            //System.out.print(decimalElement + " ");
        }

        // Delete the last space
        res = res.substring(0, res.length() - 1);

        // Convert hex to little endian
        res = Utils.hexToLittleEndian(res);

        // Convert hex string to decimal
        int decimal_res = 0;
        String[] lst_res = res.split(" ");

        for (int i = 0; i < lst_res.length; i++) {
            String hexElement = lst_res[i];
            int decimalElement = Integer.parseInt(hexElement, 16);
            decimal_res += decimalElement;
        }

        return decimal_res;
    }

    public int nSectorOfBoostSector(String sectorData) throws IOException {
        // 15->16
        int res = 0;
        String[] hexArray = sectorData.split(" ");
        for (int i = 15; i <= 16; i++) {
            String hexElement = hexArray[i];
            int decimalElement = Integer.parseInt(hexElement, 16);
            res += decimalElement;
        }
        return res;
    }

    public static int hexStringToDecimal(String hexString) {
        int decimal_res = 0;
        String[] hexStringArray = hexString.split(" ");

        StringBuilder sb = new StringBuilder();
        for (String s : hexStringArray) {
            sb.append(s);
        }
        String hexStrings = sb.toString().toLowerCase();
        System.out.println(hexStrings);
        decimal_res=Integer.parseInt(hexStrings,16);
        return decimal_res;
    }

    public BigInteger sizeOfFAT(String sectorData) throws IOException {
         String res = Utils.getHexValue("0x24", sectorData, 4);

        String[] hexArray = res.split(" "); // split the string into an array of individual hexadecimal values
        byte[] bytes = new byte[hexArray.length];

        for (int i = hexArray.length - 1; i >= 0; i--) {
            bytes[hexArray.length - 1 - i] = (byte) Integer.parseInt(hexArray[i], 16); // convert each hexadecimal value to a byte and store it in the byte array in little-endian order
        }
        BigInteger bigInt = new BigInteger(bytes); // create a new BigInteger from the little-endian byte array
        return bigInt;
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
            String hexString = bytesToHexString(sectorData);
            System.out.println(hexString);

            reader.printFAT(hexString);
            //System.out.println(reader.printFAT(sectorData));

            // Number Sector per Cluster -> Done
            System.out.println("Number of Sector per Cluster: ");
            System.out.println(reader.nSectorPerCluster(hexString));

            // Start Cluster of RDET
            System.out.println("Start Cluster of RDET: ");
            int startCLOfRDET = reader.StartClusterOfRDET(hexString);
            System.out.println(startCLOfRDET);

            // Number of Sector of Boost Sector
            System.out.println("Number of Sector of Boost Sector: ");
            int nSectorPerBs = reader.nSectorOfBoostSector(hexString);
            System.out.println(nSectorPerBs);

            // Start Sector of FAT1
            System.out.println("Start Sector of FAT1: ");
            int startSectorOfFAT1 = nSectorPerBs;
            System.out.println(startSectorOfFAT1);

            // Size of FAT
            System.out.println("Size of FAT: ");
            BigInteger sizeFAT = reader.sizeOfFAT(hexString);
            System.out.println(sizeFAT);


        } catch (IOException e) {
            System.err.println("Error reading sector: " + e.getMessage());
        }
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }

        return sb.toString();
    }
}