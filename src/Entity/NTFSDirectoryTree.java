package Entity;

import Reader.NTFSEntryReader;
import Reader.NTFSReader;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class NTFSDirectoryTree implements DirTreeAbs {
    private ItemDataObject root;

    public NTFSDirectoryTree() {
        try (NTFSEntryReader NTFSEntryReader = new NTFSEntryReader()) {
//            Khởi tạo thông tin cho NTFS
            NTFSReader ntfsReader = new NTFSReader();
            this.root = NTFSEntryReader.readEntryFromMFT(5);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPath(ItemDataObject name) {
        ItemDataObject temp = this.root;

        if (temp.equals(name)) {
            return temp.getName();
        }

        List<ItemDataObject> childrens = temp.getChildrens();
        for (ItemDataObject item : childrens) {
            String tempPath = item.searchPath(name);
            if (!tempPath.equals("")) {
                return temp.getName() + "\\" + tempPath;
            }
        }

        return "Not Found";
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
