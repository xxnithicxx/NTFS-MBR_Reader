package Entity;

import Reader.FATEntryReader;

public class FileSystemFactory {
    public static DirTreeAbs getFileSystem() {
        try (FATEntryReader ignored = new FATEntryReader(Global.mainPath)) {
            if (Global.fileSystem.contains("FAT32")) {
                return new FATDirectoryTree();
            } else {
                return new NTFSDirectoryTree();
            }
        }
    }
}
