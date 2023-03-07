package Helper;

import java.util.Arrays;

public class Utils {
    // transfer hex byte in sector to string little endian
    //"43 56 23" -> "23 56 43"
    public static String hexToLittleEndian(String hex) {
        String[] hexArray = hex.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = hexArray.length - 1; i >= 0; i--) {
            sb.append(hexArray[i]);
            sb.append(" ");
        }
        return sb.toString();
    }

    public static String getHexValueFromIndex(int index, String inputString, int bytes) {
        return inputString.substring(index, index + (bytes * 3) - 1);
    }

    // give hex value from offset input and number of bytes
    public static String getHexValueFromSector(String offset, String inputString, int bytes) {
        int decimalIndex = Integer.parseInt(offset.substring(2), 16) * 3;
        return getHexValueFromIndex(decimalIndex,inputString,bytes);
    }


    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    public static int[] checkOnBitFromHexToBinary(String hexString) {
        //0110 0000
        int number = hexStringToDecimal(hexString);
        String binaryString = Integer.toBinaryString(number);
        // array store position of bit 1
        int[] position = new int[binaryString.length()];
        int index = 0;

        for (int i = 0; i < binaryString.length(); i++) {
            if (binaryString.charAt(binaryString.length() - 1 - i) == '1')
                position[index++] = i;
        }
        // resize array to remove unused elements
        position = Arrays.copyOf(position, index);
        return position;
    }


    public static int hexStringToDecimal(String hexString) {
        int decimal_res;
        String[] hexStrings = hexString.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String i : hexStrings) {
            assert false;
            sb.append(i);
        }

        hexString = sb.toString();

        decimal_res = Integer.parseInt(hexString, 16);

        return decimal_res;
    }

    public static String byteArrayToAsciiString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            if (b == 0x00) {
                break;
            }
            sb.append((char) b);
        }
        return sb.toString();
    }

    public static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 3 + 1];

        for (int i = 0; i < len; i += 3) {
            data[i / 3] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }

        return data;
    }
}