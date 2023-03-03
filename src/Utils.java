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

    // give hex value from offset input and number of bytes
    public static String getHexValue(String offset, String inputString, int bytes) {
        int decimalIndex = Integer.parseInt(offset.substring(2), 16) * 3;
        String hexString = inputString.substring(decimalIndex, decimalIndex + (bytes * 3) - 1);
        return hexToLittleEndian(hexString);
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
        int decimal_res = 0;
        String[] hexStrings = hexString.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String i : hexStrings) {
            assert false;
            sb.append(i);
        }

        hexString = sb.toString();

        decimal_res=Integer.parseInt(hexString,16);

        return decimal_res;
    }

    public static void main(String[] args) {
        System.out.println(hexStringToDecimal("20"));
    }
}
