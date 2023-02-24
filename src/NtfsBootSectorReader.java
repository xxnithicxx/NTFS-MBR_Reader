import java.io.FileInputStream;
import java.io.IOException;

public class NtfsBootSectorReader {
    public static void main(String[] args) {
        String diskPath = "\\\\.\\C:"; // Replace C: with the drive letter of the NTFS partition you want to read
        long sectorOffset = 0; // The offset of the partition boot sector within the partition (usually 0)
        
        try (FileInputStream fis = new FileInputStream(diskPath)) {
            byte[] buffer = new byte[512];
            int bytesRead = fis.read(buffer, (int)sectorOffset, 512);

            if (bytesRead == 512) {
                // Convert the buffer to a hex string
                String hexString = bytesToHexString(buffer);

                // Print the hex string to the console
                System.out.println(hexString);
            } else {
                System.err.println("Error: Could not read partition boot sector");
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }

        return sb.toString();
    }

    public static int hexIndexToDecimalIndex(String hexIndex, String inputString) {
        int decimalIndex = Integer.parseInt(hexIndex.substring(2), 16);
        return decimalIndex * 3 - 3; // Because each byte is represented by 2 characters with 1 space in the hex
        // string and start at 1 instead of 0 so we minus 3
    }

    public static String getHexValue(String hexIndex, String inputString, int bytes) {
        int decimalIndex = hexIndexToDecimalIndex(hexIndex, inputString);
        return inputString.substring(decimalIndex, decimalIndex + (bytes * 3) - 1);
    }
}

class TestReader {
    public static void main(String[] args) {
        String inputString = "EB 52 90 4E 54 46 53 20 20 20 20 00 02 08 00 00 00 00 00 00 00 F8 00 00 3F 00 FF 00 00 B8 08 00 00 00 00 00 80 00 80 00 FF EF 90 1B 00 00 00 00 00 00 0C 00 00 00 00 00 02 00 00 00 00 00 00 00 F6 00 00 00 01 00 00 00 40 1B 06 4C 41 06 4C D8 00 00 00 00 FA 33 C0 8E D0 BC 00 7C FB 68 C0 07 1F 1E 68 66 00 CB 88 16 0E 00 66 81 3E 03 00 4E 54 46 53 75 15 B4 41 BB AA 55 CD 13 72 0C 81 FB 55 AA 75 06 F7 C1 01 00 75 03 E9 DD 00 1E 83 EC 18 68 1A 00 B4 48 8A 16 0E 00 8B F4 16 1F CD 13 9F 83 C4 18 9E 58 1F 72 E1 3B 06 0B 00 75 DB A3 0F 00 C1 2E 0F 00 04 1E 5A 33 DB B9 00 20 2B C8 66 FF 06 11 00 03 16 0F 00 8E C2 FF 06 16 00 E8 4B 00 2B C8 77 EF B8 00 BB CD 1A 66 23 C0 75 2D 66 81 FB 54 43 50 41 75 24 81 F9 02 01 72 1E 16 68 07 BB 16 68 52 11 16 68 09 00 66 53 66 53 66 55 16 16 16 68 B8 01 66 61 0E 07 CD 1A 33 C0 BF 0A 13 B9 F6 0C FC F3 AA E9 FE 01 90 90 66 60 1E 06 66 A1 11 00 66 03 06 1C 00 1E 66 68 00 00 00 00 66 50 06 53 68 01 00 68 10 00 B4 42 8A 16 0E 00 16 1F 8B F4 CD 13 66 59 5B 5A 66 59 66 59 1F 0F 82 16 00 66 FF 06 11 00 03 16 0F 00 8E C2 FF 0E 16 00 75 BC 07 1F 66 61 C3 A1 F6 01 E8 09 00 A1 FA 01 E8 03 00 F4 EB FD 8B F0 AC 3C 00 74 09 B4 0E BB 07 00 CD 10 EB F2 C3 0D 0A 41 20 64 69 73 6B 20 72 65 61 64 20 65 72 72 6F 72 20 6F 63 63 75 72 72 65 64 00 0D 0A 42 4F 4F 54 4D 47 52 20 69 73 20 63 6F 6D 70 72 65 73 73 65 64 00 0D 0A 50 72 65 73 73 20 43 74 72 6C 2B 41 6C 74 2B 44 65 6C 20 74 6F 20 72 65 73 74 61 72 74 0D 0A 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 8A 01 A7 01 BF 01 00 00 55 AA";
        String hexIndex = "0x01";
        int index = NtfsBootSectorReader.hexIndexToDecimalIndex(hexIndex, inputString);
        System.out.println(index);
        System.out.println("Value of Drive name is: " + NtfsBootSectorReader.getHexValue(hexIndex, inputString,3));
    }
}