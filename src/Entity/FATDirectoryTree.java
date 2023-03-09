package Entity;

import java.util.ArrayList;
import java.util.List;

public class FATDirectoryTree {
    private ItemDataObject root = null;

    public void initRoot(ArrayList<ArrayList<String>> entry) {
        ItemEntry item = new ItemEntry(entry.get(0));
        root = new ItemDataObject(item.getName().trim(), entry);
    }

    public ItemDataObject getRoot() {
        return this.root;
    }

    public String getPath(String name) {
        name = name.toUpperCase();
        ItemDataObject temp = this.root;

        if (temp.getName().equals(name)) {
            return temp.getName();
        }

        List<ItemDataObject> childrens = temp.getChildrens();

        for (ItemDataObject item : childrens) {
            String tempPath = item.search(name);
            if (!tempPath.equals("")) {
                return temp.getName() + "\\" + tempPath;
            }
        }

        return "Not Found";
    }
}
