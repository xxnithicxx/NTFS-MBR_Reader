import Entity.Global;
import Entity.ItemEntry;
import Reader.EntryReader;
import Reader.FATReader;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try (EntryReader entryReader = new EntryReader(Global.mainPath)) {
            ArrayList<ArrayList<String>> entrys = EntryReader.splitIntoItem(entryReader.readEntryFromRDET());
            for (var i : entrys) {
                ItemEntry item = new ItemEntry();
                item.parse(i);
                System.out.println(item.getName());
                System.out.println(item.getSize());
            }
        }
    }
}
