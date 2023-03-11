import Entity.FATDirectoryTree;
import Entity.Global;
import Entity.ItemDataObject;
import Reader.EntryReader;

import java.util.ArrayList;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        FATDirectoryTree tree = new FATDirectoryTree();

        try (EntryReader entryReader = new EntryReader(Global.mainPath)) {
            ArrayList<ArrayList<String>> entry = EntryReader.splitIntoItem(entryReader.readEntryFromRDET());
            tree.initRoot(entry);
        }

        Map<String, ItemDataObject> items = tree.getAllItems();

        for (Map.Entry<String, ItemDataObject> item : items.entrySet()) {
            System.out.println(item.getValue());
        }
    }
}
