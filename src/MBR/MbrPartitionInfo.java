package MBR;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MbrPartitionInfo {
    private Partition[] partitions = new Partition[4];

    public MbrPartitionInfo() {
        try {
            InputStream inputStream = new FileInputStream("\\\\.\\PhysicalDrive2");
            byte[] buffer = new byte[512];
            int byteRead = inputStream.read(buffer, 0, buffer.length);

            if (byteRead != 512) {
                System.out.println("Error reading MBR");
                return;
            }

//            0x01BE start address of partition table
//            0x01FE end address of partition table
//            0x10 size of partition table entry
            int count = 0;
            for (int i = 0x01BE; i <= 0x01FE; i += 0x10) {
                int partitionType = buffer[i + 4];
//                The start sector is a 32-bit unsigned integer that specifies the absolute starting sector for the
//                partition. (LBA)
                long startSector = readLittleEndian(buffer, i + 8, 4);
                long numSectors = readLittleEndian(buffer, i + 12, 4);
//                512 bytes per sector (default)

                this.partitions[count++] = new Partition(partitionType, startSector, numSectors);

                if (count == 4) {
                    break;
                }
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getPartitionName(int partitionType, byte[] buffer, int i) {
        if (partitionType == 0x0B || partitionType == 0x0C) {
            return new String(buffer, i + 0x1C, 0x0C).trim();
        } else {
            return "";
        }
    }

    private static long readLittleEndian(byte[] buffer, int offset, int length) {
        long result = 0;
        for (int i = 0; i < length; i++) {
//            1. First, we need to do a bitwise AND operation with the original byte value and 0xFF. This is to make sure the value is positive. For example, if the original byte value is -1 (represented in 2's complement), then after the operation, the value will be 255. If the original byte value is 0x80, then after the operation, the value will be 128.
//            2. Next, we need to shift the value to the right position. For example, if the byte array is {0x01, 0x02, 0x03, 0x04}, we need to shift the byte at index 0 to the right 0 * 8 bits, the byte at index 1 to the right 1 * 8 bits, the byte at index 2 to the right 2 * 8 bits, and the byte at index 3 to the right 3 * 8 bits.
//            3. Finally, we need to do a bitwise OR operation with the previous result and the current result to combine the value.
            result |= ((long) (buffer[offset + i] & 0xFF)) << (i * 8);
        }
        return result;
    }

    private static String getPartitionTypeName(int partitionType) {
        return switch (partitionType) {
            case 0x01 -> "FAT12";
            case 0x04, 0x06, 0x0E -> "FAT16";
            case 0x07, 0x17, 0x1B, 0x1C -> "NTFS";
            case 0x0B, 0x0C -> "FAT32";
            default -> "Unknown";
        };
    }

    public static void main(String[] args) {
        MbrPartitionInfo mbrPartitionInfo = new MbrPartitionInfo();
        for (Partition partition : mbrPartitionInfo.partitions) {
            if (partition != null) {
                System.out.println(mbrPartitionInfo);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Partition Table")
                .append("Partition Type\tStart Sector\tNumber of Sectors\tPartition Name");
        for (Partition partition : partitions) {
            if (partition != null) {
                stringBuilder.append(getPartitionTypeName(partition.getPartitionType()))
                        .append("\t\t")
                        .append(partition.getPartitionStart())
                        .append("\t\t")
                        .append(partition.getPartitionSize());
            }
        }

        return stringBuilder.toString();
    }
}