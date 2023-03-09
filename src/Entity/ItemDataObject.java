//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package Entity;

import Reader.DataReader;
import Reader.EntryReader;

import java.util.ArrayList;
import java.util.List;

public class ItemDataObject {
    private final String name;
    private long size;
    private String status;
    private long startCluster;
    private boolean isFolder;
    private List<ItemDataObject> childrens = null;

    public ItemDataObject(String name, long size, String status, long startCluster, boolean isFolder) {
        this.name = name;
        this.size = size;
        this.status = status;
        this.startCluster = startCluster;
        this.isFolder = isFolder;

        if (isFolder) {
            this.childrens = new ArrayList<>();

            DataReader dataReader = new DataReader();
            EntryReader entryReader = new EntryReader(Global.mainPath);
            ArrayList<ArrayList<String>> entries;

            entries = EntryReader.splitIntoItem(entryReader.readEntryFromCluster(startCluster));

            for (int i = 2; i < entries.size(); i++) {
                ItemEntry itemEntry = new ItemEntry(entries.get(i));
                ItemDataObject item = new ItemDataObject(itemEntry.getName(), itemEntry.getSize(), itemEntry.getStatus(), itemEntry.getStartCluster(), itemEntry.isFolder());
                this.childrens.add(item);
            }
        }
    }

    //    Only use this for add base drive
    public ItemDataObject(String name, ArrayList<ArrayList<String>> entry) {
        this.name = name;
        this.isFolder = true;
        this.childrens = new ArrayList<>();

        for (int i = 2; i < entry.size(); i++) {
            ItemEntry itemEntry = new ItemEntry(entry.get(i));
            ItemDataObject item = new ItemDataObject(itemEntry.getName(), itemEntry.getSize(), itemEntry.getStatus(), itemEntry.getStartCluster(), itemEntry.isFolder());
            this.childrens.add(item);
        }
    }

    public long getSize() {
        if (!this.isFolder) {
            return this.size;
        } else {
            long tempSize = 0L;

            for (var i : this.childrens)
                tempSize += i.getSize();

            return tempSize;
        }
    }

    public String search(String name) {
        if (this.name.toUpperCase().equals(name)) {
            return this.name;
        }

        if (this.isFolder) {
            for (ItemDataObject item : this.childrens) {
                String temp = item.search(name);
                if (!temp.equals("")) {
                    return this.name + "\\" + temp;
                }
            }
        }

        return "";
    }

    public String getStatus() {
        return this.status;
    }

    public long getStartCluster() {
        return this.startCluster;
    }

    public String getName() {
        return this.name;
    }

    public List<ItemDataObject> getChildrens() {
        return this.childrens;
    }

    public boolean isFolder() {
        return this.isFolder;
    }
}
