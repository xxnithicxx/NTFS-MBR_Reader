package MBR;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class MBRReader {
    public String getMBR() {
        try {
            InputStream inputStream = new FileInputStream("\\\\.\\PhysicalDrive1");
            byte[] buffer = new byte[512];
            inputStream.read(buffer, 0, buffer.length);
//            Convert the buffer to a hex string
            inputStream.close();
            return bytesToHexString(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }

        return sb.toString();
    }
//
//    public static int hexIndexToDecimalIndex(String hexIndex) {
//        int decimalIndex = Integer.parseInt(hexIndex.substring(2), 16);
//        return decimalIndex * 3; // Because each byte is represented by 2 characters with 1 space in the hex string
//    }

//    public static String getHexValue(String hexIndex, String inputString, int bytes) {
//        int decimalIndex = hexIndexToDecimalIndex(hexIndex);
//        return inputString.substring(decimalIndex, decimalIndex + (bytes * 3) - 1);
//    }
//
//    public static String hexToLittleEndian(String hex) {
//        String[] hexArray = hex.split(" ");
//        StringBuilder sb = new StringBuilder();
//        for (int i = hexArray.length - 1; i >= 0; i--) {
//            sb.append(hexArray[i]);
//            sb.append(" ");
//        }
//        return sb.toString();
//    }

    public static void main(String[] args) {
//        MBRReader mbrReader = new MBRReader();
//        String inputString = mbrReader.getMBR();
//        String hexIndex = "0x01FE";
//
//        System.out.println(inputString + "\n");
//        System.out.println(hexToLittleEndian(getHexValue(hexIndex, inputString, 2)));
        //System.out.println(Utils);


    }
}
