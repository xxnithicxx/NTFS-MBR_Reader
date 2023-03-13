package Entity;

import Reader.FATEntryReader;

import java.util.*;

public class FATDirectoryTree implements DirTreeAbs {
    private ItemDataObject root = null;

    public FATDirectoryTree() {
        try (FATEntryReader FATEntryReader = new FATEntryReader(Global.mainPath)) {
            ArrayList<ArrayList<String>> entry = Reader.FATEntryReader.splitIntoItem(FATEntryReader.readEntryFromRDET());
            this.initRoot(entry);
        }
    }

    public void initRoot(ArrayList<ArrayList<String>> entry) {
        ItemEntry item = new ItemEntry(entry.get(0));
        root = new ItemDataObject(item.getName().trim(), entry);
    }

    public ItemDataObject getRoot() {
        return this.root;
    }

    public String getPath(ItemDataObject key) {
        ItemDataObject temp = this.root;

        if (temp.equals(key)) {
            return temp.getName();
        }

        List<ItemDataObject> childrens = temp.getChildrens();
        for (ItemDataObject item : childrens) {
            String tempPath = item.searchPath(key);
            if (!tempPath.equals("")) {
                return temp.getName() + "\\" + tempPath;
            }
        }

        return "Not Found";
    }

    public TreeMap<String, ItemDataObject> getAllItems() {
        HashMap<String, ItemDataObject> map = new HashMap<>();
        TreeMap<String, ItemDataObject> items = new TreeMap<>(new ValueComparator(map));

        ItemDataObject temp = this.root;
        List<ItemDataObject> childrens = temp.getChildrens();

        for (ItemDataObject children : childrens) {
            children.getAllItems(map, temp.getName());
        }

        items.putAll(map);
        return items;
    }
}

class ValueComparator implements Comparator<String> {
    Map<String, ItemDataObject> base;

    public ValueComparator(Map<String, ItemDataObject> base) {
        this.base = base;
    }

    public int compare(String a, String b) {
        int countA = 0;
        int countB = 0;

        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) == '\\') {
                countA++;
            }
        }

        for (int i = 0; i < b.length(); i++) {
            if (b.charAt(i) == '\\') {
                countB++;
            }
        }

        if (countA == countB) {
            if (a.length() == b.length())
                return -1;

            return a.length() - b.length();
        }

        return countA - countB;
    }
}