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

    public ItemEntry(ArrayList<String> entryList) {
        this.entryList = new ArrayList<>();
        parse(entryList);
    }

    public void parse(ArrayList<String> entry) {
        for (int i = entry.size(); i > 0; i--) {
            this.entryList.add(entry.get(i - 1));
        }
    }

    public String getName() {
        if (isDeleted())
            return "Deleted";

        if (this.entryList.size() == 1) {
            String temp;
            temp = getHexValueFromSector("0x00", this.entryList.get(0), 8);
            String name = byteArrayToAsciiString(hexStringToByteArray(temp));
            temp = getHexValueFromSector("0x08", this.entryList.get(0), 3);
            String extension = byteArrayToAsciiString(hexStringToByteArray(temp));

            name = name.trim();

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
                if (b == currentByte && b == (byte) 0x00) {
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
                    if (b == currentByte && b == (byte) 0x00) {
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
                    if (b == currentByte && b == (byte) 0x00) {
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
        if (isDeleted())
            return 0;

        String temp = Utils.getHexValueFromSector("0x1C", this.entryList.get(0), 4);
        temp = Utils.littleToBigEndian(temp);
        return hexStringToDecimal(temp);
    }

    public long getClusterNumber() {
        if (isDeleted())
            return -1;

        if (this.entryList.size() == 1) {
            String temp = Utils.getHexValueFromSector("0x0F", this.entryList.get(0), 4);
            return hexStringToDecimal(temp);
        }

        long sectorNumber = 0;
        for (int i = 1; i < this.entryList.size(); i++) {
            String temp = Utils.getHexValueFromSector("0x0F", this.entryList.get(i), 4);
            sectorNumber += hexStringToDecimal(temp);
        }

//        Convert cluster to Sector

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
}