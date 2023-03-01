import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class EntryReader implements AutoCloseable {
    private final DataInputStream inputStream;
    private final int sectorSize;

    public EntryReader(FileInputStream fileInputStream, int startSector) throws IOException {
        this.inputStream = new DataInputStream(fileInputStream);
        this.sectorSize = startSector;
    }

//    public String readSector(int sectorNumber) throws IOException {
//        int position = sectorNumber * sectorSize;
//        byte[] sectorData = new byte[sectorSize];
//        inputStream.readFully(sectorData, position, sectorSize);
//        return Utils.bytesToHexString(sectorData);
//    }

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
            //System.out.print(decimalElement + " ");
        }
        return res;
    }

    public int sizeOfFAT(String sectorData) throws IOException {
        // 36->39
        String res = "";
        String[] hexArray = sectorData.split(" ");
        for (int i = 36; i <= 39; i++) {
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


    public void close() throws IOException {
        inputStream.close();
    }
}

class EntryTestReader {
    public static void main(String[] args) {
        String filePath = "\\\\.\\E:";
        int sectorSize = 512;
        int startSector = 16;

        try (EntryReader reader = new EntryReader(new FileInputStream(filePath), sectorSize)) {
            byte[] sectorData = reader.readSector(startSector);
            String hexString = bytesToHexString(sectorData);
            System.out.println(hexString);



//            reader.printFAT(hexString);
//            //System.out.println(reader.printFAT(sectorData));
//
//            // Number Sector per Cluster -> Done
//            System.out.println("Number of Sector per Cluster: ");
//            System.out.println(reader.nSectorPerCluster(hexString));


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