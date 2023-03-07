package Entity;

import Helper.Utils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static Helper.Utils.*;

public class ItemEntry {
    private final ArrayList<String> entryList;

    public ItemEntry() {
        this.entryList = new ArrayList<>();
    }

    public void parse(ArrayList<String> entry) {
        for (int i = entry.size(); i > 0; i--) {
            this.entryList.add(entry.get(i - 1));
        }
    }

    public String getName() {
        if (this.entryList.size() == 1) {
            String temp;
            temp = getHexValueFromSector("0x00", this.entryList.get(0), 8);
            String name = byteArrayToAsciiString(hexStringToByteArray(temp));
            temp = getHexValueFromSector("0x08", this.entryList.get(0), 3);
            String extension = byteArrayToAsciiString(hexStringToByteArray(temp));

            return name + "." + extension;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (int i = 1; i < this.entryList.size(); i++) {
            ArrayList<Byte> bytes = new ArrayList<>();
            String temp = this.entryList.get(i);
            boolean isLast = false;
            byte[] tempBytes = hexStringToByteArray(getHexValueFromSector("0x01", temp, 10));
            byte currentByte = 0x00;

            for (byte b : tempBytes) {
                if (Byte.compare(b, currentByte) == 0 && Byte.compare(b, (byte) 0x00) == 0) {
                    isLast = true;
                    break;
                }

                bytes.add(b);
                currentByte = b;
            }

            if (!isLast) {

                tempBytes = hexStringToByteArray(getHexValueFromSector("0x0E", temp, 12));
                currentByte = 0x00;

                for (byte b : tempBytes) {
                    if (Byte.compare(b, currentByte) == 0 && Byte.compare(b, (byte) 0x00) == 0) {
                        isLast = true;
                        break;
                    }

                    bytes.add(b);
                    currentByte = b;
                }
            }


            if (!isLast) {
                tempBytes = hexStringToByteArray(getHexValueFromSector("0x1C", temp, 4));
                currentByte = 0x00;

                for (byte b : tempBytes) {
                    if (Byte.compare(b, currentByte) == 0 && Byte.compare(b, (byte) 0x00) == 0) {
                        break;
                    }

                    bytes.add(b);
                    currentByte = b;
                }

            }

            byte[] byteArray = new byte[bytes.size()];
            for (int j = 0; j < bytes.size(); j++) {
                byteArray[j] = bytes.get(j);
            }

            outputStream.write(byteArray, 0, byteArray.length);
        }

        return outputStream.toString(StandardCharsets.UTF_16LE);
    }

//     TODO: Implement get attributes

    public long getSize() {
        String temp = Utils.getHexValueFromSector("0x1C", this.entryList.get(0), 4);
        return hexStringToDecimal(temp);
    }

    public long getSectorNumber() {
        if (this.entryList.size() == 1) {
            String temp = Utils.getHexValueFromSector("0x0F", this.entryList.get(0), 4);
            return hexStringToDecimal(temp);
        }

        long sectorNumber = 0;
        for (int i = 1; i < this.entryList.size(); i++) {
            String temp = Utils.getHexValueFromSector("0x0F", this.entryList.get(i), 4);
            sectorNumber += hexStringToDecimal(temp);
        }

        return sectorNumber;
    }

    public boolean isDeleted() {
        if (this.entryList.get(0).equals("00")) {
            return true;
        } else {
            return this.entryList.get(0).equals("E5");
        }
    }

    public boolean isFolder() {
        return this.entryList.get(0).equals("2E");
    }

    public boolean isMainEntry(String entryData) {
        return entryData.startsWith("0F", 33);
    }

    public static void main(String[] args) {
        ArrayList<String> entry = new ArrayList<>();
        entry.add("44 63 00 78 00 00 00 FF FF FF FF 0F 00 82 FF FF FF FF FF FF FF FF FF FF FF FF 00 00 FF FF FF FF");
        entry.add("03 6E 00 67 00 20 00 74 00 AD 1E 0F 00 E1 70 00 20 00 74 00 69 00 6E 00 2E 00 00 00 64 00 6F 00");
        entry.add("02 75 00 A3 1E 6E 00 20 00 6C 00 0F 00 E1 ED 00 20 00 68 00 C7 1E 20 00 74 00 00 00 68 00 D1 1E");
        entry.add("01 50 00 72 00 6F 00 6A 00 65 00 0F 00 E1 63 00 74 00 20 00 31 00 20 00 2D 00 00 00 20 00 51 00");
        entry.add("50 52 4F 4A 45 43 7E 32 44 4F 43 20 00 67 11 4D 64 56 64 56 00 00 43 50 5B 56 07 00 48 54 00 00");
        ItemEntry itemEntry = new ItemEntry();
        itemEntry.parse(entry);

        String str = itemEntry.getName();
//      This is the UTF-16LE encoding of the string
        byte[] utf16Bytes = str.getBytes(StandardCharsets.UTF_16LE);

        System.out.println(str);

//        for (byte b : utf16Bytes) {
//            System.out.printf("%02X ", b);
//        }

        System.out.println(itemEntry.getSize());
    }
}