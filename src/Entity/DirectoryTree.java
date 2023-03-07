//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package Entity;

import javax.naming.NameAlreadyBoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DirectoryTree {
    private ItemDataObject root = null;
    private final Map<String, Integer> fileNames = new HashMap();

    public DirectoryTree() {
    }

    public boolean add(ItemDataObject item, String parent) throws NameAlreadyBoundException {
        if (item == null) {
            return false;
        } else if (this.root == null) {
            this.root = item;
            this.addName(item.getName());
            return true;
        } else if (parent == null && this.root.isFolder()) {
            this.root.addChildren(item);
            this.addName(item.getName());
            return true;
        } else if (this.fileNames.containsKey(parent) && (Integer)this.fileNames.get(parent) > 1) {
            throw new NameAlreadyBoundException(parent);
        } else if (this.root.getName().equals(parent)) {
            this.root.addChildren(item);
            this.addName(item.getName());
            return true;
        } else {
            boolean isAdded = false;
            Iterator var4 = this.root.getChildrens().iterator();

            do {
                if (!var4.hasNext()) {
                    return false;
                }

                ItemDataObject children = (ItemDataObject)var4.next();
                if (children.isFolder()) {
                    isAdded = children.addChildren(item, parent);
                }
            } while(!isAdded);

            this.addName(item.getName());
            return true;
        }
    }

    public void addName(String name) {
        if (this.fileNames.containsKey(name)) {
            int count = (Integer)this.fileNames.get(name);
            this.fileNames.put(name, count + 1);
        } else {
            this.fileNames.put(name, 1);
        }
    }

    public ItemDataObject getRoot() {
        return this.root;
    }

    public String getPath(String name) {
        ItemDataObject temp = this.root;

        StringBuilder path;
        for(path = new StringBuilder(); temp != null; temp = temp.getNextChildren()) {
            if (temp.getName().equals(name)) {
                path.append(temp.getName());
                break;
            }

            path.append(temp.getName()).append("/");
        }

        return path.toString();
    }

    public static void main(String[] args) {
        ItemDataObject temp1 = new ItemDataObject("temp1", 0L, "show", 0L, true);
        ItemDataObject temp2 = new ItemDataObject("temp2", 0L, "show", 0L, false);
        ItemDataObject temp3 = new ItemDataObject("temp3", 0L, "show", 0L, true);
        ItemDataObject temp4 = new ItemDataObject("temp4", 0L, "show", 0L, false);
        ItemDataObject temp5 = new ItemDataObject("temp1", 0L, "show", 0L, true);
        ItemDataObject temp6 = new ItemDataObject("temp6", 0L, "show", 0L, false);
        DirectoryTree tree = new DirectoryTree();

        try {
            tree.add(temp1, (String)null);
            tree.add(temp2, "temp1");
            tree.add(temp3, "temp1");
            tree.add(temp4, "temp3");
            tree.add(temp5, "temp3");
            tree.add(temp6, "temp1");
        } catch (NameAlreadyBoundException var9) {
            System.out.println(var9.getMessage());
        }

        System.out.println(tree.getPath("temp5"));
    }
}
