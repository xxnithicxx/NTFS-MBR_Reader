import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class DataReader implements AutoCloseable {
    private final String filePath;
    private final int sizeDataPerCluster;

    public DataReader(String filePath, int sizeDataPerCluster) {
        this.filePath = filePath;
        this.sizeDataPerCluster = sizeDataPerCluster;
    }
    public String[] read(long[] clusters) throws IOException {
        // Create Sector Reader
        SectorReader sectorReader = new SectorReader(new FileInputStream(filePath), sizeDataPerCluster);

        ArrayList<String> stringArray = new ArrayList<>();

        for (int i = 0; i < clusters.length; i++) {
            if (i == clusters.length-1){
                // Check EOF
                //----------------------------------------------------------------------------Here
                System.out.println("Checking EOF...");
            }
            else{
                int clusterId = (int) clusters[i];

                // Convert into SectorId
                int SectorId = 16384; // Test need to create function here based on clusterId

                // For testing
                System.out.println("SectorId = " + SectorId);

                byte[] sectorData = sectorReader.readSector(SectorId);
                String entryHexString = Utils.bytesToHexString(sectorData);

                stringArray.add(entryHexString);

                // For testing
//                System.out.println(entryHexString);
//                break;
            }
        }
        // Convert to static array
        String[] resArray = new String[stringArray.size()];
        for (int i = 0; i < stringArray.size(); i++) {
            resArray[i] = stringArray.get(i);
        }

        return resArray;
    }

    @Override
    public void close() throws Exception {

    }
}
class DataReaderTest {
    public static void main(String[] args) throws Exception {
        String filePath = "\\\\.\\E:";

        // Input
        long[] clusters = {1, 2, 54, 5};
        int sizeDataPerCluster = 512;

        try (DataReader dataReader = new DataReader(filePath, sizeDataPerCluster)) {
            String[] sectorData = dataReader.read(clusters);

            ArrayList<ArrayList<String>> items = EntryReader.splitIntoItem(sectorData);

            System.out.println((items));
        }
    }
}