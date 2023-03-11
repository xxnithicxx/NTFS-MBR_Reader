//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package Entity;

import Reader.DataReader;
import Reader.EntryReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemDataObject {
    private final String name;
    private final boolean isFolder;
    private long size;
    private String status;
    private long startCluster;
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
                if (itemEntry.isDeleted())
                    continue;
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
            if (itemEntry.isDeleted())
                continue;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemDataObject that = (ItemDataObject) o;

        if (getSize() != that.getSize()) return false;
        if (getStartCluster() != that.getStartCluster()) return false;
        if (!getName().equals(that.getName())) return false;
        return getStatus().equals(that.getStatus());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + (int) (getSize() ^ (getSize() >>> 32));
        result = 31 * result + getStatus().hashCode();
        result = 31 * result + (int) (getStartCluster() ^ (getStartCluster() >>> 32));
        return result;
    }

    public String searchPath(ItemDataObject item) {
        if (this.equals(item)) {
            return this.name;
        }

        if (this.isFolder) {
            for (ItemDataObject i : this.childrens) {
                String temp = i.searchPath(item);
                if (!temp.equals("")) {
                    return this.name + "\\" + temp;
                }
            }
        }

        return "";
    }

    public void getAllItems(HashMap<String, ItemDataObject> items, String path) {
        if (this.isFolder) {
            for (ItemDataObject item : this.childrens) {
                item.getAllItems(items, path + "\\" + this.name);
            }
        }

        String absolutePath = path + "\\" + this.name;

        if (this.isFolder)
            absolutePath += "\\";

        items.put(absolutePath, this);
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

    public String getPath(ItemDataObject itemNode) {
        return Global.dirTreeAbs.getPath(itemNode);
    }

    public String getTxtData() {
        byte[] bytes;

        try (DataReader dataReader = new DataReader()) {
            bytes = dataReader.read((int) this.getStartCluster());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new String(bytes).trim();
    }

    @Override
    public String toString() {
        return "ItemDataObject{" +
                "name='" + name + '\'' +
                ", size=" + size +
                ", status='" + status + '\'' +
                ", startCluster=" + startCluster +
                '}';
    }

    public String getExtension() {
        if (this.isFolder)
            return "";

        String[] temp = this.name.split("\\.");

        if (temp.length == 1)
            return "";

        return temp[temp.length - 1];
    }

    public ItemDataObject[] listFiles() {
        if (!this.isFolder())
            return null;

        ItemDataObject[] temp = new ItemDataObject[this.childrens.size()];

        for (int i = 0; i < this.childrens.size(); i++) {
            temp[i] = this.childrens.get(i);
        }

        return temp;
    }
}
