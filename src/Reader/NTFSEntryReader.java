package Reader;

import Entity.Global;
import Entity.ItemDataObject;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static Helper.Utils.*;

public class NTFSEntryReader implements AutoCloseable {
    private String entryData;
    private int recordNumber;

    private static int getNextDataRunOffset(String dataRunLength) {
        if (dataRunLength.equals("00")) {
            return 0;
        }

        int numberOfByteLenght = Integer.parseInt(dataRunLength.substring(1, 2));
        int numberOfNextByteLenght = Integer.parseInt(dataRunLength.substring(0, 1));

//        Add 1 to include the length byte itself (header)
        return numberOfByteLenght + numberOfNextByteLenght + 1;
    }

    @Override
    public void close() {

    }

    public ItemDataObject readEntryFromMFT(int i) throws IOException {
        long startSector = (long) Global.MFTStart * Global.sectorPerCluster + (long) i * 2;
        StringBuilder entryDataBD = new StringBuilder();

        SectorReader sectorReader = new SectorReader(new FileInputStream(Global.mainPath), Global.bytesPerSector);
        String sectorData1 = bytesToHexString(sectorReader.readSector(startSector));
        sectorReader = new SectorReader(new FileInputStream(Global.mainPath), Global.bytesPerSector);
        String sectorData2 = bytesToHexString(sectorReader.readSector(startSector + 1));
        entryDataBD.append(sectorData1).append(sectorData2);

        this.entryData = entryDataBD.toString();
        this.recordNumber = i;

        if (this.isSystem() && i != 5)
            return null;

        String name = getEntryName();
        long size = getEntrySize();
        String status = getEntryStatus();

        ItemDataObject item;
        if (this.isFolder()) {
            ArrayList<ItemDataObject> children = getEntryChildren();
            item = new ItemDataObject(name, size, status, true, children);
        } else {
            String data = getEntryData();
            item = new ItemDataObject(name, size, status, data, false);
        }

        return item;
    }

    private ArrayList<ItemDataObject> getEntryChildren() {
        ArrayList<ItemDataObject> children = new ArrayList<>();

        int firstAttributeOffset = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x14", entryData, 2)));
        do {
            String typeID = getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset), entryData, 4);
            long attributeLength = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + 0x04), entryData, 2)));

            if (typeID.equals("90 00 00 00")) {
                int offset =
                        hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + 0x14), entryData, 2)));
//                16 is the size of the attribute Index Root
                int flag = hexStringToDecimal(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + offset + 16 + 0x0C), entryData, 1));

                if (flag == 0x00) {
                    int sizeIndexEntry =
                            hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + offset + 16 + 0x04), entryData, 4)));
                    int currentOffset = 32;

                    int childRecordNumber;
                    int parentRecordNumber;
                    do {
                        childRecordNumber =
                                hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + offset + currentOffset), entryData, 6)));
                        parentRecordNumber =
                                hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + offset + currentOffset + 0x10), entryData, 6)));

                        if (parentRecordNumber == 0 && childRecordNumber == 0)
                            break;

                        if (parentRecordNumber == this.recordNumber && childRecordNumber != this.recordNumber) {
                            try {
                                NTFSEntryReader reader = new NTFSEntryReader();
                                ItemDataObject child = reader.readEntryFromMFT(childRecordNumber);
                                if (child != null)
                                    children.add(child);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        int entrySize = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + offset + currentOffset + 0x08), entryData, 2)));

                        currentOffset += entrySize;
                    } while (sizeIndexEntry - currentOffset >= 88);
                    return children;
                } else if (flag == 0x01) {
                    firstAttributeOffset += attributeLength;
                    int offsetToDataRunIndexAllocation =
                            hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + 0x20), entryData, 2)));
                    int nextDataRunOffset =
                            getNextDataRunOffset(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + offsetToDataRunIndexAllocation), entryData, 1));

                    ArrayList<String> dataRunList = new ArrayList<>();
                    do {
                        String dataRun =
                                getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + offsetToDataRunIndexAllocation), entryData, nextDataRunOffset);
                        dataRunList.add(dataRun);
                        offset += nextDataRunOffset;
                        nextDataRunOffset =
                                getNextDataRunOffset(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + offsetToDataRunIndexAllocation + nextDataRunOffset), entryData, 1));
                    } while (nextDataRunOffset != 0);

                    int clusterOffsetFromLastDataRun = 0;
                    StringBuilder sb = new StringBuilder();
                    for (String dt : dataRunList) {
                        String header = dt.substring(0, 2);

                        int length = Integer.parseInt(header.substring(1, 2));

                        String lengthString = dt.substring(3, 3 + length * 3 - 1);
                        String offsetString = dt.substring(3 + length * 3);

                        int lengthValue = hexStringToDecimal(littleToBigEndian(lengthString));
                        int offsetValue = hexStringToDecimal(littleToBigEndian(offsetString));

                        clusterOffsetFromLastDataRun += offsetValue;

                        int bytesPerCluster = Global.bytesPerSector * Global.sectorPerCluster;
                        long startCluster = (long) clusterOffsetFromLastDataRun * bytesPerCluster;
                        for (int i = 0; i < lengthValue; i++) {
                            try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(Global.mainPath))) {
                                dataInputStream.skip(startCluster);
                                byte[] buffer = new byte[bytesPerCluster];
                                dataInputStream.read(buffer);
                                sb.append(bytesToHexString(buffer)).append(" ");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    String indexAllocationData = sb.toString();
                    long firstEntryOffset = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x18",
                            indexAllocationData, 4)));
                    long sizeIndexEntry = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x1C",
                            indexAllocationData, 4)));

                    long currentOffset = 24 + firstEntryOffset;

                    int childRecordNumber;
                    int parentRecordNumber;
                    do {
                        childRecordNumber =
                                hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString((int) currentOffset), indexAllocationData, 6)));
                        parentRecordNumber =
                                hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString((int) currentOffset + 0x10), indexAllocationData, 6)));
                        int entrySize = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString((int) currentOffset + 0x08), indexAllocationData, 2)));

                        if (parentRecordNumber == 0 && childRecordNumber == 0)
                            break;

                        if (parentRecordNumber == this.recordNumber && childRecordNumber != this.recordNumber) {
                            try {
                                NTFSEntryReader reader = new NTFSEntryReader();
                                ItemDataObject child = reader.readEntryFromMFT(childRecordNumber);
                                if (child != null)
                                    children.add(child);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        currentOffset += entrySize;
                    } while (sizeIndexEntry - currentOffset >= 88);
                    return children;
                }
            } else
                firstAttributeOffset += attributeLength;
        } while (true);
    }

    private String getEntryData() {
        int firstAttributeOffset = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x14", entryData, 2)));

        do {
            String typeID = getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset), entryData, 4);
            long attributeLength = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + 0x04), entryData, 2)));
            if (typeID.equals("80 00 00 00")) {
                int non_resident = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + 0x08), entryData, 1)));

                if (non_resident == 0) {
                    long offset =
                            hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + 0x14), entryData, 2)));
                    long size =
                            hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + 0x10), entryData, 4)));

                    return new String(hexStringToByteArray(getHexValueFromSector("0x" + Integer.toHexString((int) offset + firstAttributeOffset), entryData, (int) size)));
                } else {
                    long offset = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + 0x20), entryData, 2)));
                    int nextDataRunOffset = getNextDataRunOffset(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + (int) offset), entryData, 1));

//                    Get Data Run List
                    ArrayList<String> dataRunList = new ArrayList<>();
                    do {
                        String dataRun =
                                getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + (int) offset), entryData, nextDataRunOffset);
                        dataRunList.add(dataRun);
                        offset += nextDataRunOffset;
                        nextDataRunOffset =
                                getNextDataRunOffset(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + (int) offset), entryData, 1));
                    } while (nextDataRunOffset != 0);

                    int clusterOffsetFromLastDataRun = 0;
                    StringBuilder sb = new StringBuilder();
                    for (String dataRun : dataRunList) {
                        String header = dataRun.substring(0, 2);

                        int length = Integer.parseInt(header.substring(1, 2));

                        String lengthString = dataRun.substring(3, 3 + length * 3 - 1);
                        String offsetString = dataRun.substring(3 + length * 3);

                        int lengthValue = hexStringToDecimal(littleToBigEndian(lengthString));
                        int offsetValue = hexStringToDecimal(littleToBigEndian(offsetString));

                        clusterOffsetFromLastDataRun += offsetValue;

                        int bytesPerCluster = Global.bytesPerSector * Global.sectorPerCluster;
                        long startCluster = (long) clusterOffsetFromLastDataRun * bytesPerCluster;
                        for (int i = 0; i < lengthValue; i++) {
                            try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(Global.mainPath))) {
                                dataInputStream.skip(startCluster + (long) i * bytesPerCluster);
                                byte[] buffer = new byte[bytesPerCluster];
                                dataInputStream.read(buffer);
                                sb.append(new String(buffer));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    return sb.toString();
                }
            } else {
                firstAttributeOffset += attributeLength;
            }
        } while (true);
    }

    private long getEntrySize() {
        if (this.isFolder()) {
            return 0;
        }

        int firstAttributeOffset = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x14", entryData, 2)));

//        Get file size from $DATA attribute, we can get it from the $FILE_NAME attribute too
        do {
            String typeID = getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset), entryData, 4);
            long attributeLength = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + 0x04), entryData, 2)));
            if (typeID.equals("80 00 00 00")) {
                int non_resident = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + 0x08), entryData, 1)));

                if (non_resident == 0) {
                    return hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + 0x10), entryData, 4)));
                } else {
                    return hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString(firstAttributeOffset + 0x30), entryData, 8)));
                }
            } else {
                firstAttributeOffset += attributeLength;
            }
        } while (true);
    }

    private boolean isFolder() {
        long firstAttributeOffset = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x14", entryData, 2)));
        do {
            String typeID = getHexValueFromSector("0x" + Integer.toHexString((int) firstAttributeOffset), entryData, 4);

            if (typeID.equals("30 00 00 00")) {
                long offset = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString((int) firstAttributeOffset + 0x14), entryData, 2)));
                long flag =
                        hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString((int) (firstAttributeOffset + offset + 0x38)), entryData, 4)));

                return (flag & 0x10000000L) == 0x10000000L;
            } else {
                long attributeLength = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString((int) firstAttributeOffset + 0x04), entryData, 2)));
                firstAttributeOffset += attributeLength;
            }
        } while (true);
    }

    private boolean isSystem() {
        long firstAttributeOffset = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x14", entryData, 2)));
        do {
            String typeID = getHexValueFromSector("0x" + Integer.toHexString((int) firstAttributeOffset), entryData, 4);

            if (typeID.equals("30 00 00 00")) {
                long offset = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString((int) firstAttributeOffset + 0x14), entryData, 2)));
                long flag =
                        hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString((int) (firstAttributeOffset + offset + 0x38)), entryData, 4)));

                return (flag & 0x00000004L) == 0x00000004L;
            } else {
                long attributeLength = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString((int) firstAttributeOffset + 0x04), entryData, 2)));
                firstAttributeOffset += attributeLength;
            }
        } while (true);
    }

    private String getEntryStatus() {
        long firstAttributeOffset = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x14", entryData, 2)));
        do {
            String typeID = getHexValueFromSector("0x" + Integer.toHexString((int) firstAttributeOffset), entryData, 4);

            if (typeID.equals("30 00 00 00")) {
                long offset = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString((int) firstAttributeOffset + 0x14), entryData, 2)));
                long flag =
                        hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString((int) (firstAttributeOffset + offset + 0x38)), entryData, 2)));

                StringBuilder sb = new StringBuilder();

                if ((flag & 0x00000001L) == 0x00000001L) {
                    sb.append("|Read Only");
                }

                if ((flag & 0x00000002L) == 0x00000002L) {
                    sb.append("|Hidden");
                } else {
                    sb.append("|Normal");
                }

                return sb.toString();
            } else {
                long attributeLength = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString((int) firstAttributeOffset + 0x04), entryData, 2)));
                firstAttributeOffset += attributeLength;
            }
        } while (true);
    }

    private String getEntryName() {
        long firstAttributeOffset = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x14", entryData, 2)));
        do {
            String typeID = getHexValueFromSector("0x" + Integer.toHexString((int) firstAttributeOffset), entryData, 4);

            if (typeID.equals("30 00 00 00")) {
                long offset = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString((int) firstAttributeOffset + 0x14), entryData, 2)));
                long nameLength = hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString((int) (firstAttributeOffset + offset + 0x40)), entryData, 1)));
                byte[] bytesName = hexStringToByteArray(getHexValueFromSector("0x" + Integer.toHexString((int) (firstAttributeOffset + offset + 0x42)), entryData, (int) nameLength * 2));

//                TODO: Check if it's a long name sequence

                return new String(bytesName, StandardCharsets.UTF_16LE);
            } else {
                long attributeLength =
                        hexStringToDecimal(littleToBigEndian(getHexValueFromSector("0x" + Integer.toHexString((int) firstAttributeOffset + 0x04), entryData, 2)));
                firstAttributeOffset += attributeLength;
            }
        } while (true);
    }

}