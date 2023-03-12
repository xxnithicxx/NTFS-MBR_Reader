package Entity;

import java.util.Map;

public class NTFSDirectoryTree implements DirTreeAbs{
    private ItemDataObject root = null;
    public NTFSDirectoryTree() {
    }

    @Override
    public String getPath(ItemDataObject name) {
        return null;
    }

    @Override
    public Map<String, ItemDataObject> getAllItems() {
        return null;
    }

    @Override
    public ItemDataObject getRoot() {
        return this.root;
    }
}
