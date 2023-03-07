import Entity.Global;
import Entity.ItemEntry;
import Reader.EntryReader;
import Reader.FATReader;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try (EntryReader entryReader = new EntryReader(Global.mainPath)) {

            ArrayList<ArrayList<String>> entrys = EntryReader.splitIntoItem(entryReader.readEntryFromRDET());

            ItemEntry item = new ItemEntry();
            item.parse(entrys.get(3));


            FATReader fatReader = new FATReader();
            var array=fatReader.readFAT(7);
            System.out.println(array);
        }
    }
}
