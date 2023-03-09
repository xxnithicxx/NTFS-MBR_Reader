import Entity.FATDirectoryTree;
import Entity.Global;
import Reader.EntryReader;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        FATDirectoryTree tree = new FATDirectoryTree();

        try (EntryReader entryReader = new EntryReader(Global.mainPath)) {
            ArrayList<ArrayList<String>> entry = EntryReader.splitIntoItem(entryReader.readEntryFromRDET());
            tree.initRoot(entry);
        }

        System.out.println(tree.getPath("Truong Binh.txt"));
    }
}

