package Reader;

import Helper.Utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import static Helper.Utils.*;

// TODO: Hỏi Trinh các câu hỏi về hàm
public class NTFSReader {
    final int ID_STANDARD = 16;
    final int ID_FILENAME = 48;
    final int ID_DATA = 80;
    final int ID_INDXROOT = 144;

    public static void main(String[] args) throws IOException {
        // Open the NTFS file system image file
        RandomAccessFile ntfsFile = new RandomAccessFile("\\\\.\\F:", "r");
        FileChannel fc = ntfsFile.getChannel();

        // Read the bytes per sector value from the boot sector
        byte[] bootSector = new byte[1024];
        ByteBuffer buffer = ByteBuffer.wrap(bootSector);
        fc.read(buffer, 0x0);

        // BootSector
//            TODO: Add those fields to the Global class
        int bytesPerSector = getUnsignedShort(buffer.array(), 0xb);
        long sectorPerCluster = getUnsignedByte(buffer.array(), 0xd);
        long VolumeSerialNumber = getUnsignedLong(buffer.array(), 0x48);

        String str = Long.toHexString(VolumeSerialNumber);
        int sectorPerTrack = getUnsignedShort(buffer.array(), 0x18);
        long TotalSector = getUnsignedLong(buffer.array(), 0x28);
        int sectorStartDisk = getUnsignedShort2(buffer.array(), 0x1C);

        long MFTStartAtCluster = getUnsignedLong(buffer.array(), 0x30);

        long bytesPerCluster = bytesPerSector * sectorPerCluster;

        // Đọc entry đầu tiên trong bảng MFT để lấy thông tin về cấu trúc entry

        // ĐỊA CHỈ OFFSET CỦA TỪNG MFT ENTRY
        // MỖI MFT ENTRY SẼ CÓ ĐỊA CHỈ CỐ ĐỊNH 1024 ( 2 SECTOR)
        long mftOffset = MFTStartAtCluster * bytesPerSector * sectorPerCluster + 39 * 1024;

        ByteBuffer mftEntry = ByteBuffer.allocate(1024);
        fc.read(mftEntry, mftOffset);
        mftEntry.rewind();

        // HEADER CỦA MASTER FILE TABLE

        // "FILE" OR "BAAD": LỖI
        byte[] signatureBytes = new byte[4];
        mftEntry.get(signatureBytes, 0x0, 4);
        String signature = new String(signatureBytes);

        long LSN = getUnsignedLong(mftEntry.array(), 0x8);
        String strLongSeq = Long.toHexString(LSN);
        int SequenNum = getUnsignedShort(mftEntry.array(), 0x10);
        int ReferenceCount = getUnsignedShort(mftEntry.array(), 0x12);
        int AtributeOfOffsetInformationFile = getUnsignedShort(mftEntry.array(), 0x14);

        // - giá trị 0x01: MFT entry đã được sử dụng
        // - giá trị 0x02: MFT entry của một thư mục
        // - giá trị 0x04, 0x08: không xác định
        int stateEntry = getUnsignedShort(mftEntry.array(), 0x16);

        int byteIsUsed = getUnsignedShort(mftEntry.array(), 0x18);

        int entrySize = getUnsignedShort2(mftEntry.array(), 0x1C);

        int entriesPerCluster = (int) bytesPerCluster / entrySize;

        // tham chiếu đến MFT entry cơ sở
        // 0: mft cơ sở
        long baseMFT = getUnsignedLong(mftEntry.array(), 0x20);

        int IDNextAttr = getUnsignedShort(mftEntry.array(), 0x28);
        // đọc nội dung của bootsector - Header standard

        int LengthOfAttribute = 0;
        int OffsetStartContent = 0;

        while (true) {
            int IDAtr = getUnsignedShort2(mftEntry.array(), AtributeOfOffsetInformationFile);
            if (IDAtr == 65535) {
                break;
            }
            LengthOfAttribute = getUnsignedShort2(mftEntry.array(),
                    AtributeOfOffsetInformationFile + 4);
            int IsResident = getUnsignedByte(mftEntry.array(),
                    AtributeOfOffsetInformationFile + 8);

            int isName = getUnsignedByte(mftEntry.array(), AtributeOfOffsetInformationFile + 9);

            int isNameLength = getUnsignedShort(mftEntry.array(), AtributeOfOffsetInformationFile + 10);

            int flagInfo = getUnsignedShort(mftEntry.array(), AtributeOfOffsetInformationFile + 12);

            int contentSize = getUnsignedShort2(mftEntry.array(),
                    AtributeOfOffsetInformationFile + 16);

            // vị trí bắt đầu nội dung
            OffsetStartContent = getUnsignedShort(mftEntry.array(),
                    AtributeOfOffsetInformationFile + 20);

            // BẮT ĐẦU ĐỌC NỘI DUNG CỦA STANDARD INFORMATION
            if (IDAtr == 16) {
                long timeCreate = getUnsignedLong(mftEntry.array(), AtributeOfOffsetInformationFile
                        + OffsetStartContent);
                Instant instantCreate = Instant
                        .ofEpochMilli((timeCreate - 116444736000000000L) / 10000L);
                ZonedDateTime zonedDateTimeCreate = instantCreate.atZone(ZoneId.systemDefault());

                long timeModified = getUnsignedLong(mftEntry.array(), AtributeOfOffsetInformationFile
                        + OffsetStartContent + 16);
                Instant instanttimeModified = Instant
                        .ofEpochMilli((timeModified - 116444736000000000L) / 10000L);
                ZonedDateTime zonedDateTimeModified = instanttimeModified
                        .atZone(ZoneId.systemDefault());

                long timeAccessed = getUnsignedLong(mftEntry.array(), AtributeOfOffsetInformationFile
                        + OffsetStartContent + 24);
                Instant instanttimeAccessed = Instant
                        .ofEpochMilli((timeModified - 116444736000000000L) / 10000L);
                ZonedDateTime zonedDateTimeAccessed = instanttimeAccessed
                        .atZone(ZoneId.of("Asia/Ho_Chi_Minh"));

                // 1: CHỈ ĐỌC
                // 2: ẨN
                // 4: THUỘC HỆ THỐNG
                // 32: DỰ PHÒNG
                int flag = getUnsignedShort2(mftEntry.array(), AtributeOfOffsetInformationFile
                        + OffsetStartContent + 32);
                long SequenceNumber = getUnsignedLong(mftEntry.array(), AtributeOfOffsetInformationFile
                        + OffsetStartContent + 64);
            } else if (IDAtr == 48) {
                long AddressParentEntry = getUnsignedLong(mftEntry.array(),
                        AtributeOfOffsetInformationFile +
                                OffsetStartContent);
                String strAddressParentEntry = Long.toHexString(AddressParentEntry);

                long timeCreateFileName = getUnsignedLong(mftEntry.array(),
                        AtributeOfOffsetInformationFile +
                                OffsetStartContent + 8);
                Instant instantCreateFileName = Instant
                        .ofEpochMilli((timeCreateFileName - 116444736000000000L) / 10000L);
                ZonedDateTime zonedDateTimeCreateFileName = instantCreateFileName
                        .atZone(ZoneId.systemDefault());
                System.out.println(zonedDateTimeCreateFileName);

                int isAchive = getUnsignedShort2(mftEntry.array(), AtributeOfOffsetInformationFile +
                        OffsetStartContent + 56);
                // attribute loại file name

                int dinhdangtep = getUnsignedByte(mftEntry.array(), AtributeOfOffsetInformationFile
                        + OffsetStartContent + 65);

                int nameLength = getUnsignedByte(mftEntry.array(), AtributeOfOffsetInformationFile
                        + OffsetStartContent + 64);

                long nameFile = getUnsignedLong(mftEntry.array(), AtributeOfOffsetInformationFile
                        + OffsetStartContent + 66);
                String hexName = Integer.toHexString(AtributeOfOffsetInformationFile
                        + OffsetStartContent + 66);
                int hexNumberName = Integer.parseInt(hexName, 16);
                byte[] byteArray = new byte[8];

                ByteBuffer buffer1 = ByteBuffer.wrap(mftEntry.array());
                byte[] nameBytes = readBytesFromBuffer(buffer1, hexNumberName, nameLength * 2);
                String name = Charset.forName("UTF-16LE").decode(ByteBuffer.wrap(nameBytes)).toString();
                int b = 0;
            } else if (IDAtr == 128) {
                if (IsResident == 1) {
                    int AttributeID = getUnsignedShort(mftEntry.array(),
                            AtributeOfOffsetInformationFile + 14);
                    long initialVCN = getUnsignedLong(mftEntry.array(),
                            AtributeOfOffsetInformationFile + 16);
                    long finalVCN = getUnsignedLong(mftEntry.array(),
                            AtributeOfOffsetInformationFile + 24);
                    int dataRunOffset = getUnsignedShort(mftEntry.array(),
                            AtributeOfOffsetInformationFile + 32);
                    long attributeRealSize = getUnsignedLong(mftEntry.array(),
                            AtributeOfOffsetInformationFile + 48);
                    int offsetRunlist = AtributeOfOffsetInformationFile + dataRunOffset;
                    int dataRunHeader = getUnsignedByte(mftEntry.array(), offsetRunlist);

                    int nextDataRunOffset = getNextDataRunOffset(dataRunHeader);

//                    Get Data Run List
                    ArrayList<String> dataRunList = new ArrayList<>();
                    do {
                        byte[] dataRun = readBytesFromBuffer(mftEntry, offsetRunlist, nextDataRunOffset);
                        dataRunList.add(bytesToHexString(dataRun));
                        offsetRunlist += nextDataRunOffset;
                        dataRunHeader = getUnsignedByte(mftEntry.array(), offsetRunlist);
                        nextDataRunOffset = getNextDataRunOffset(dataRunHeader);
                    } while (nextDataRunOffset != 0);

                    int offsetFromLastDataRun = 0;
                    StringBuilder sb = new StringBuilder();
                    for (String dataRun : dataRunList){
                        String header = dataRun.substring(0, 2);

                        int length = Integer.parseInt(header.substring(1, 2));
                        int offset = Integer.parseInt(header.substring(0, 1));

                        String lengthString = dataRun.substring(3, 3 + length * 3 - 1);
                        String offsetString = dataRun.substring(3 + length * 3, dataRun.length() - 1);

                        int lengthValue = hexStringToDecimal(littleToBigEndian(lengthString));
                        int offsetValue = hexStringToDecimal(littleToBigEndian(offsetString));

                        offsetFromLastDataRun += offsetValue;

                        long startCluster = offsetFromLastDataRun * bytesPerCluster;
                        for (int i = 0; i < lengthValue; i++){
                            ByteBuffer dataArray = ByteBuffer.allocate((int) (bytesPerCluster));
                            fc.read(dataArray, startCluster + i * bytesPerCluster);
                            sb.append(new String(dataArray.array()));
                        }
                    }
                    System.out.println(sb);
                }
                else {
                    int OffsetStartDATA = AtributeOfOffsetInformationFile + OffsetStartContent;
                    byte[] byteArrayData = new byte[contentSize * 2];
                    ByteBuffer bufferData = ByteBuffer.wrap(mftEntry.array());
                    String hex = Integer.toHexString(OffsetStartDATA);
                    int hexNumber = Integer.parseInt(hex, 16);
                    byte[] DataBytes = readBytesFromBuffer(bufferData,
                            hexNumber,
                            contentSize);
                    String ContentData = StandardCharsets.US_ASCII.decode(ByteBuffer.wrap(DataBytes))
                            .toString();
                }
            }

            AtributeOfOffsetInformationFile += LengthOfAttribute;
            if (IDAtr == 65535) {
                break;
            }

            if (IDAtr == 144) {
                int OffsetStartIndexRoot = AtributeOfOffsetInformationFile - LengthOfAttribute;

                int IDIndexRoot = getUnsignedShort2(mftEntry.array(), OffsetStartIndexRoot);

                int sizeOfRecordByte = getUnsignedShort2(mftEntry.array(), OffsetStartIndexRoot + 8);

                int OffsetOfNodeHeader = OffsetStartIndexRoot + 16;

                int checkTypeAtribute_OffsetStartHeaderIndexRoot = getUnsignedShort2(mftEntry.array(),
                        OffsetOfNodeHeader + 16);
                int bytePerIndexRecord = getUnsignedShort2(mftEntry.array(),
                        OffsetOfNodeHeader + 16 + 8);

                // Node header
                int NodeHeader = OffsetOfNodeHeader + 16 + 16;
                int OffsetStartFirstIndex = getUnsignedShort2(mftEntry.array(), NodeHeader);
                int OffsetStartLastIndex = getUnsignedShort2(mftEntry.array(), NodeHeader + 4);

                // 0x10: có child node: phải dùng index_allocation
                // 0x00: không cần dùng thêm index_allocation

                int flagIndexRoot = getUnsignedShort2(mftEntry.array(), NodeHeader + 12);
                // loop for index entries in a folder
                int flagIsChildNode = 0;
                int firstIndexEntry = 0;
                firstIndexEntry = NodeHeader + OffsetStartFirstIndex;
                while (flagIsChildNode != 2) {

                    int lengthOfThisEntry = getUnsignedShort(mftEntry.array(), firstIndexEntry + 8);
                    int lengthOfFileName = getUnsignedShort(mftEntry.array(), firstIndexEntry + 10);
                    flagIsChildNode = getUnsignedShort2(mftEntry.array(), firstIndexEntry + 12);
                    int OffsetContentOfEntry = firstIndexEntry + 16;
                    long EntryIsAtMFTEntry = getUnsignedLong(mftEntry.array(),
                            OffsetContentOfEntry);
                    int OffsetOfFileName = OffsetContentOfEntry + 64;

                    // ----------------------------------------------------------------------------

                    int nameLength1 = getUnsignedByte(mftEntry.array(),
                            OffsetOfFileName);
                    long nameFile1 = getUnsignedLong(mftEntry.array(),
                            OffsetOfFileName + 2);
                    String hexName1 = Integer.toHexString(OffsetOfFileName + 2);
                    int hexNumberName1 = Integer.parseInt(hexName1, 16);
                    byte[] byteArray1 = new byte[8];

                    ByteBuffer buffer2 = ByteBuffer.wrap(mftEntry.array());
                    byte[] nameBytes1 = readBytesFromBuffer(buffer2, hexNumberName1,
                            nameLength1 * 2);
                    String name1 = Charset.forName("UTF-16LE").decode(ByteBuffer.wrap(nameBytes1))
                            .toString();

                    firstIndexEntry += lengthOfThisEntry;
                }
                AtributeOfOffsetInformationFile += firstIndexEntry;
            }

            int a = 0;
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
        return bb.getShort();
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

    private static int getNextDataRunOffset(int dataRunLenght) {
        if (dataRunLenght == 0)
            return 0;

        String check = Integer.toHexString(dataRunLenght);

        int numberOfByteLenght = Integer.parseInt(check.substring(1));
        int numberOfNextByteLenght = Integer.parseInt(check.substring(0, 1));

//        Add 1 to include the length byte itself (header)
        return numberOfByteLenght + numberOfNextByteLenght + 1;
    }
}
