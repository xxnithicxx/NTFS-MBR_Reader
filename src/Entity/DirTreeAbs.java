package Entity;

import java.util.Map;

public interface DirTreeAbs {
    String getPath(ItemDataObject name);
    Map<String, ItemDataObject> getAllItems();
    ItemDataObject getRoot();
}
