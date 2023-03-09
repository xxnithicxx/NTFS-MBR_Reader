package Entity;

import Helper.Utils;
import Reader.DataReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    public long getSize() {
        if (isDeleted())
            return 0;

        String temp = Utils.getHexValueFromSector("0x1C", this.entryList.get(0), 4);
        temp = Utils.littleToBigEndian(temp);
        return hexStringToDecimal(temp);
    }

    public long getStartCluster() {
        if (isDeleted())
            return -1;

        String high = Utils.getHexValueFromSector("0x14", this.entryList.get(0), 2);
        String low = Utils.getHexValueFromSector("0x1A", this.entryList.get(0), 2);

        return hexStringToDecimal(Utils.littleToBigEndian(low + high));
    }

    public String getTxtData() {
        if (isDeleted())
            return "isDeleted";

        if (isFolder())
            return "isFolder";

        if (this.getName().contains(".TXT") || this.getName().contains(Global.txtUTF16)) {
            byte[] bytes;

            try (DataReader dataReader = new DataReader()) {
                bytes = dataReader.read((int) this.getStartCluster());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return new String(bytes).trim();
        }

        return "isNotTxt";
    }

    public boolean isDeleted() {
        if (this.entryList.get(0).equals("00")) {
            return true;
        } else {
            return this.entryList.get(0).startsWith("E5");
        }
    }

    public boolean isFolder() {
        if (this.isDeleted())
            return false;

        int attribute = hexStringToDecimal(Utils.getHexValueFromSector("0x0B", this.entryList.get(0), 1));
        return (attribute & 0x10) == 0x10;
    }

    private boolean isMainEntry(String entryData) {
        return entryData.startsWith("0F", 33);
    }

    public String getStatus() {
        String temp = Utils.getHexValueFromSector("0x0B", this.entryList.get(0), 1);
        int[] attributes = checkOnBitFromHexToBinary(temp);

        StringBuilder sb = new StringBuilder();
        for (var atr : attributes){
            if (atr == 0)
                sb.append("Read Only");

            if (atr == 1)
                sb.append("|Hidden");
            else
                sb.append("|Normal");
        }

        return sb.toString();
    }
}
