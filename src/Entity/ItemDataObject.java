//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package Entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemDataObject {
    private String name;
    private long size;
    private String status;
    private long sectorStart;
    private boolean isFolder;
    private List<ItemDataObject> childrens = null;
    private int nextChild;

    public ItemDataObject(String name, long size, String status, long sectorStart, boolean isFolder) {
        this.name = name;
        this.size = size;
        this.status = status;
        this.sectorStart = sectorStart;
        this.isFolder = isFolder;
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

    public long getSectorStart() {
        return this.sectorStart;
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
