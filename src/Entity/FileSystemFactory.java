package Entity;

import Reader.EntryReader;

public class FileSystemFactory {
    public static DirTreeAbs getFileSystem() {
        try (EntryReader ignored = new EntryReader(Global.mainPath)) {
            if (Global.fileSystem.contains("FAT32")) {
                return new FATDirectoryTree();
            } else {
                return null;
            }
        }
    }
}
