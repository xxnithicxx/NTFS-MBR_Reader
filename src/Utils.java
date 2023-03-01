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
        int decimalIndex = Integer.parseInt(offset.substring(2), 16)*3;
        return inputString.substring(decimalIndex, decimalIndex + (bytes * 3) - 1);
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }

        return sb.toString();
    }
}
