package Reader;

import Entity.Global;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileClusterReader implements AutoCloseable{
    private final FileChannel fileChannel;
    private final int bytesPerCluster;

    public FileClusterReader(Path filePath, int bytesPerCluster) throws IOException {
        this.fileChannel = FileChannel.open(filePath, StandardOpenOption.READ);

        try (FATEntryReader FATEntryReader = new FATEntryReader("\\\\.\\E:")) {
            this.bytesPerCluster = FATEntryReader.getNSectorPerCl() * 512;
        }
    }

    public byte[] readCluster(long clusterNumber) throws IOException {
        long position;
        try (FATEntryReader FATEntryReader = new FATEntryReader(Global.mainPath)) {
            position = FATEntryReader.startSectorFromCluster(clusterNumber);
        }

        ByteBuffer buffer = ByteBuffer.allocate(bytesPerCluster);
        fileChannel.position(position);
        fileChannel.read(buffer);
        return buffer.array();
    }

    public void close() throws IOException {
        fileChannel.close();
    }
}
