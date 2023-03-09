package Reader;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class NTFSReader {
        public static void main(String[] args) throws IOException {
                // Open the NTFS file system image file
                RandomAccessFile ntfsFile = new RandomAccessFile("\\\\.\\F:", "r");
                FileChannel fc = ntfsFile.getChannel();

                // Read the bytes per sector value from the boot sector
                byte[] bootSector = new byte[1024];
                ByteBuffer buffer = ByteBuffer.wrap(bootSector);
                fc.read(buffer, 0x0);

                // bootsector

                int bytesPerSector = getUnsignedShort(buffer.array(), 0xb);

                long sectorPerCluster = getUnsignedByte(buffer.array(), 0xd);

                long VolumeSerialNumber = getUnsignedLong(buffer.array(), 0x48);
                String str = Long.toHexString(VolumeSerialNumber);
                int sectorPerTrack = getUnsignedShort(buffer.array(), 0x18);
                long TotalSector = getUnsignedLong(buffer.array(), 0x28);
                int sectorStartDisk = getUnsignedShort2(buffer.array(), 0xC);

                long MFTStartAtCluster = getUnsignedLong(buffer.array(), 0x30);
                int referenceCount = getUnsignedShort(buffer.array(), 0x12);
                long mftOffset = MFTStartAtCluster * bytesPerSector * sectorPerCluster + 1024 * 55;

                // mftOffset += 21411840;

                long bytesPerCluster = bytesPerSector * sectorPerCluster;

                // Đọc entry đầu tiên trong bảng MFT để lấy thông tin về cấu trúc entry

                ByteBuffer mftEntry = ByteBuffer.allocate((int) 1024);
                fc.read(mftEntry, mftOffset);
                mftEntry.flip();

                byte[] signatureBytes = new byte[4];
                mftEntry.get(signatureBytes, 0x0, 4);
                String signature = new String(signatureBytes);

                int entrySize = getUnsignedShort2(mftEntry.array(), 0x1C);

                int entriesPerCluster = (int) bytesPerCluster / entrySize;

                int AtributeOfOffsetInformationFile = getUnsignedShort(mftEntry.array(), 0x14);
                int ReferenceCount = getUnsignedShort(mftEntry.array(), 0x12);

                // - giá trị 0x01: MFT entry đã được sử dụng
                // - giá trị 0x02: MFT entry của một thư mục
                // - giá trị 0x04, 0x08: không xác định
                int stateEntry = getUnsignedShort(mftEntry.array(), 0x16);
                int byteIsUsed = getUnsignedShort(mftEntry.array(), 0x18);
                long baseMFT = getUnsignedLong(mftEntry.array(), 0x20);
                int IDNextAttr = getUnsignedShort(mftEntry.array(), 0x28);
                // đọc nội dung của bootsector - Header standard information

                while (true) {
                        int IDAtr = getUnsignedShort2(mftEntry.array(), AtributeOfOffsetInformationFile);

                        int attrLengthStandardInformation = getUnsignedShort2(mftEntry.array(),
                                        AtributeOfOffsetInformationFile + 4);

                        int isName = getUnsignedByte(mftEntry.array(), AtributeOfOffsetInformationFile + 9);

                        int isNameLength = getUnsignedShort(mftEntry.array(), AtributeOfOffsetInformationFile + 10);

                        int flagInfo = getUnsignedShort(mftEntry.array(), AtributeOfOffsetInformationFile + 12);

                        // qua nội dung standard information
                        // cho biết standInformation file dài bao nhiêu để nhảy qua file name

                        int contentSizeStandardIformation = getUnsignedShort2(mftEntry.array(),
                                        AtributeOfOffsetInformationFile + 16);

                        // vị trí bắt đầu nội dung của standard information
                        int OffsetStartContentStandardInformation = getUnsignedShort(mftEntry.array(),
                                        AtributeOfOffsetInformationFile + 20);

                        // BẮT ĐẦU ĐỌC NỘI DUNG CỦA STANDARD INFFORMATION

                        long timeCreate = getUnsignedLong(mftEntry.array(),
                                        AtributeOfOffsetInformationFile + OffsetStartContentStandardInformation);
                        Instant instantCreate = Instant.ofEpochMilli((timeCreate - 116444736000000000L) / 10000L);
                        ZonedDateTime zonedDateTimeCreate = instantCreate.atZone(ZoneId.systemDefault());
                        System.out.println(zonedDateTimeCreate);
                        // Date date = (Date) Date.from(zonedDateTime.toInstant());

                        long timeModified = getUnsignedLong(mftEntry.array(),
                                        AtributeOfOffsetInformationFile + OffsetStartContentStandardInformation + 16);
                        Instant instanttimeModified = Instant
                                        .ofEpochMilli((timeModified - 116444736000000000L) / 10000L);
                        ZonedDateTime zonedDateTimeModified = instanttimeModified.atZone(ZoneId.systemDefault());
                        System.out.println(zonedDateTimeModified);

                        long timeAccessed = getUnsignedLong(mftEntry.array(),
                                        AtributeOfOffsetInformationFile + OffsetStartContentStandardInformation + 24);
                        Instant instanttimeAccessed = Instant
                                        .ofEpochMilli((timeModified - 116444736000000000L) / 10000L);
                        ZonedDateTime zonedDateTimeAccessed = instanttimeAccessed.atZone(ZoneId.of("Asia/Ho_Chi_Minh"));
                        System.out.println(zonedDateTimeAccessed);

                        int flag = getUnsignedShort2(mftEntry.array(),
                                        AtributeOfOffsetInformationFile + OffsetStartContentStandardInformation + 32);
                        // ĐỌC HEADER CỦA FILE NAME
                        int idTypeFileName = getUnsignedShort2(mftEntry.array(), AtributeOfOffsetInformationFile +
                                        attrLengthStandardInformation);
                        int atrLengthFileName = getUnsignedShort2(mftEntry.array(), AtributeOfOffsetInformationFile +
                                        attrLengthStandardInformation + 4);
                        int residentOrNot = getUnsignedByte(mftEntry.array(), AtributeOfOffsetInformationFile +
                                        attrLengthStandardInformation + 8);
                        int IsNamedFilename = getUnsignedShort2(mftEntry.array(), AtributeOfOffsetInformationFile +
                                        attrLengthStandardInformation + 10);
                        int flagFilename = getUnsignedShort(mftEntry.array(),
                                        AtributeOfOffsetInformationFile + attrLengthStandardInformation +
                                                        +12);
                        int OffsetStartContentFileName = getUnsignedShort(mftEntry.array(),
                                        AtributeOfOffsetInformationFile + attrLengthStandardInformation + 20);
                        // BẮT ĐẦU ĐỌC THÔNG TIN CỦA FILE NAME
                        long AddressParentEntry = getUnsignedLong(mftEntry.array(),
                                        AtributeOfOffsetInformationFile + attrLengthStandardInformation +
                                                        OffsetStartContentFileName);
                        String strAddressParentEntry = Long.toHexString(AddressParentEntry);

                        long timeCreateFileName = getUnsignedLong(mftEntry.array(),
                                        AtributeOfOffsetInformationFile + attrLengthStandardInformation +
                                                        OffsetStartContentFileName + 8);
                        Instant instantCreateFileName = Instant
                                        .ofEpochMilli((timeCreate - 116444736000000000L) / 10000L);
                        ZonedDateTime zonedDateTimeCreateFileName = instantCreate.atZone(ZoneId.systemDefault());
                        System.out.println(zonedDateTimeCreateFileName);

                        int isAchive = getUnsignedShort2(mftEntry.array(), AtributeOfOffsetInformationFile +
                                        attrLengthStandardInformation + OffsetStartContentFileName + 56);
                        // attribute loại file name

                        int dinhdangtep = getUnsignedByte(mftEntry.array(),
                                        AtributeOfOffsetInformationFile + attrLengthStandardInformation +
                                                        OffsetStartContentFileName + 65);
                        int nameLength = getUnsignedByte(mftEntry.array(),
                                        AtributeOfOffsetInformationFile + attrLengthStandardInformation +
                                                        OffsetStartContentFileName + 64);
                        long nameFile = getUnsignedLong(mftEntry.array(),
                                        AtributeOfOffsetInformationFile + attrLengthStandardInformation +
                                                        OffsetStartContentFileName + 66);
                        String hexName = Integer
                                        .toHexString(AtributeOfOffsetInformationFile + attrLengthStandardInformation +
                                                        OffsetStartContentFileName + 66);
                        int hexNumberName = Integer.parseInt(hexName, 16);
                        byte[] byteArray = new byte[8];

                        ByteBuffer buffer1 = ByteBuffer.wrap(mftEntry.array());
                        byte[] nameBytes = readBytesFromBuffer(buffer1, hexNumberName, nameLength * 2);
                        String name = Charset.forName("UTF-16LE").decode(ByteBuffer.wrap(nameBytes)).toString();

                        int OffsetStartDATA = AtributeOfOffsetInformationFile + attrLengthStandardInformation
                                        + atrLengthFileName;
                        int IDData = getUnsignedShort2(mftEntry.array(), OffsetStartDATA);
                        int atrLengthData = getUnsignedShort2(mftEntry.array(), OffsetStartDATA + 4);
                        int IsResidentOrNot = getUnsignedByte(mftEntry.array(), OffsetStartDATA + 8);

                        int OffsetStartContentData = getUnsignedShort(mftEntry.array(), OffsetStartDATA + 20);
                        int lengthContent = getUnsignedShort2(mftEntry.array(), OffsetStartDATA + 16);
                        byte[] byteArrayData = new byte[lengthContent * 2];
                        ByteBuffer bufferData = ByteBuffer.wrap(mftEntry.array());
                        String hex = Integer.toHexString(OffsetStartDATA + OffsetStartContentData);
                        int hexNumber = Integer.parseInt(hex, 16);
                        byte[] DataBytes = readBytesFromBuffer(bufferData,
                                        hexNumber,
                                        nameLength * 2);
                        String ContentData = Charset.forName("UTF-16LE").decode(ByteBuffer.wrap(DataBytes))
                                        .toString();

                        int OffsetStartIndexRoot = AtributeOfOffsetInformationFile + attrLengthStandardInformation
                                        + atrLengthFileName + atrLengthData;
                        int IDIndexRoot = getUnsignedShort2(mftEntry.array(), OffsetStartIndexRoot);
                        int attriLengObjectID = getUnsignedShort2(mftEntry.array(), OffsetStartIndexRoot + 4);

                        int OffsetStartIndexRoot1 = AtributeOfOffsetInformationFile + attrLengthStandardInformation
                                        + atrLengthFileName + atrLengthData + attriLengObjectID;
                        int IDIndexRoot1 = getUnsignedShort2(mftEntry.array(), OffsetStartIndexRoot1);
                        int attriLengObjectID1 = getUnsignedShort2(mftEntry.array(), OffsetStartIndexRoot1 + 4);
                        int sizeOfEachINDXRecord = getUnsignedShort2(mftEntry.array(), OffsetStartIndexRoot1 + 8);

                        int OffsetStartIndexAllocation = AtributeOfOffsetInformationFile + attrLengthStandardInformation
                                        + atrLengthFileName + atrLengthData + attriLengObjectID + attriLengObjectID1;
                        int flagINDX = getUnsignedByte(mftEntry.array(), OffsetStartIndexRoot1 + 12);

                        int IDIndexAllocation = getUnsignedShort2(mftEntry.array(), OffsetStartIndexAllocation);
                        int attriLengINDXAllocation = getUnsignedShort2(mftEntry.array(),
                                        OffsetStartIndexAllocation + 4);
                        int totalEntryInArrINDXAllocation = getUnsignedShort(mftEntry.array(),
                                        OffsetStartIndexAllocation + 6);
                        long clusterNumber = getUnsignedLong(mftEntry.array(), OffsetStartIndexAllocation + 16);
                        int a = 0;
                        // AtributeOfOffsetInformationFile += atrLengthData;

                        // AtributeOfOffsetInformationFile += attrLengthStandardInformation +
                        // atrLengthFileName;
                        // AtributeOfOffsetInformationFile += 1024;
                        // attribute loại file name
                        // -----------------------------------------------------------------------------------------------

                }

        }

        private static int getUnsignedByte(byte[] bytes, int offset) {
                return bytes[offset] & 0xff;
        }

        private static int getUnsignedShort(byte[] buffer, int offset) {
                ByteBuffer bb = ByteBuffer.wrap(buffer, offset, 2);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                return bb.getShort() & 0xffff;
        }

        private static int getUnsignedShort2(byte[] buffer, int offset) {
                ByteBuffer bb = ByteBuffer.wrap(buffer, offset, 4);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                return bb.getShort() & 0xffff;
        }

        private static long getUnsignedLong(byte[] buffer, int offset) {
                ByteBuffer bb = ByteBuffer.wrap(buffer, offset, 8);
                bb.order(ByteOrder.LITTLE_ENDIAN);

                return bb.getLong() & 0xFFFFFFFFFFFFFFFEL;

        }

        private static byte[] readBytesFromBuffer(ByteBuffer buffer, int offset, int length) {

                byte[] bytes = new byte[length];
                buffer.position(offset);
                buffer.get(bytes);
                return bytes;
        }

}
