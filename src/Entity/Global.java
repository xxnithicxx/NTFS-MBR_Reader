package Entity;


import java.nio.charset.StandardCharsets;

public class Global {
    public static String mainPath = "";
    public static String fileSystem = "";
    public static int startFAT = 0;
    public static int sizeFAT = 0;
    public static int sectorPerCluster = 0;
    public static int numberOfFat = 0;
    public static int nSectorPerBs = 0;
    public static long startClOfRDET = 0;
    public static DirTreeAbs dirTreeAbs = null;
    public static int bytesPerSector = 512;
    public static int MFTStart = 0;
    public final static String txtUTF16 = new String(new byte[]{0x54, 0x00, 0x58, 0x00, 0x54, 0x00},
            StandardCharsets.UTF_16);
}
