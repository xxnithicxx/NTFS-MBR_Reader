import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class NTFSFileClusterReader implements AutoCloseable{
    private final FileChannel fileChannel;
    private final int bytesPerCluster;

    public NTFSFileClusterReader(Path filePath, int bytesPerCluster) throws IOException {
        this.fileChannel = FileChannel.open(filePath, StandardOpenOption.READ);
        this.bytesPerCluster = bytesPerCluster;
    }

    public byte[] readCluster(long clusterNumber) throws IOException {
        long position = clusterNumber * bytesPerCluster;
        ByteBuffer buffer = ByteBuffer.allocate(bytesPerCluster);
        fileChannel.position(position);
        fileChannel.read(buffer);
        return buffer.array();
    }

    public void close() throws IOException {
        fileChannel.close();
    }

    public static void main(String[] args) {
        String filePath = "\\\\.\\F:";
        int bytesPerCluster = 4096; // Default value for NTFS file systems

        try (NTFSFileClusterReader reader = new NTFSFileClusterReader(Paths.get(filePath), bytesPerCluster)) {
            long clusterNumber = 0;
            byte[] clusterData = reader.readCluster(clusterNumber);
            System.out.println("Cluster data: " + Arrays.toString(clusterData));
        } catch (IOException e) {
            System.err.println("Error reading cluster: " + e.getMessage());
        }
    }
}
