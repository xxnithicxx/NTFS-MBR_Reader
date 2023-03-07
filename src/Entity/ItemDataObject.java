//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package Entity;

import Reader.FileClusterReader;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ItemDataObject {
    private String name;
    private long size;
    private String status;
    private long startCluster;
    private boolean isFolder;
    private List<ItemDataObject> childrens = null;
    private int nextChild;

    public ItemDataObject(String name, long size, String status, long startCluster, boolean isFolder) {
        this.name = name;
        this.size = size;
        this.status = status;
        this.startCluster = startCluster;
        this.isFolder = isFolder;

//  TODO: Check if this is a folder and go to the sector to get the children
    }

    public ItemDataObject(String name, boolean isFolder) {
        this.name = name;
        this.isFolder = isFolder;
    }

    public ItemDataObject(ArrayList<String> entry) {
        ItemEntry itemEntry = new ItemEntry(entry);

        this.name = itemEntry.getName();

        if (itemEntry.isFolder()) {
            this.size = 0;
            this.isFolder = true;
            long startCluster = this.getStartCluster();

//            TODO: Change the file path to global variable when user select
            String filePath = "\\\\.\\E:";
            int bytesPerCluster = 4096; // Default value for NTFS file systems

//            Get cluster data from sector
            byte[] clusterData = null;
            try (FileClusterReader reader = new FileClusterReader(Paths.get(filePath), bytesPerCluster)) {
                clusterData = reader.readCluster(startCluster);
            } catch (IOException e) {
                System.err.println("Error reading cluster: " + e.getMessage());
            }


        }
    }

    public long getSize() {
        if (!this.isFolder) {
            return this.size;
        } else {
            long tempSize = 0L;

            ItemDataObject item;
            for(Iterator var3 = this.childrens.iterator(); var3.hasNext(); tempSize += item.getSize()) {
                item = (ItemDataObject)var3.next();
            }

            return tempSize;
        }
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

    public boolean addChildren(ItemDataObject children) {
        if (children == null) {
            return false;
        } else {
            if (this.childrens == null) {
                this.childrens = new ArrayList();
                this.nextChild = 0;
            }

            this.childrens.add(children);
            return true;
        }
    }

    public boolean addChildren(ItemDataObject children, String parent) {
        if (children == null) {
            return false;
        } else {
            if (this.childrens == null) {
                this.childrens = new ArrayList();
                this.nextChild = 0;
            }

            if (this.name.equals(parent) && this.isFolder) {
                this.childrens.add(children);
                return true;
            } else {
                boolean isAdded = false;
                Iterator var4 = this.childrens.iterator();

                do {
                    if (!var4.hasNext()) {
                        return false;
                    }

                    ItemDataObject item = (ItemDataObject)var4.next();
                    if (item.isFolder) {
                        isAdded = item.addChildren(children, parent);
                    }
                } while(!isAdded);

                return true;
            }
        }
    }

    public ItemDataObject getNextChildren() {
        if (this.childrens == null) {
            return null;
        } else {
            if (this.nextChild > this.childrens.size()) {
                this.nextChild = 0;
            }

            return (ItemDataObject)this.childrens.get(this.nextChild++);
        }
    }

    public boolean isFolder() {
        return this.isFolder;
    }
}
