package File;
import java.io.File;

import GUI.TreeDirectory;

import javax.swing.tree.DefaultMutableTreeNode;

public class FileOperation {
    public void dirTree(String path, DefaultMutableTreeNode top){
        File sf = new File (path);
        if (!sf.exists()) {
        }
        else{
            DefaultMutableTreeNode file = new DefaultMutableTreeNode();
            if (sf.isDirectory())
            {
                File[] fileList = sf.listFiles();
                for (File f:fileList)
                {
                    if(f.isDirectory())
                    {
                        DefaultMutableTreeNode subfolder = new DefaultMutableTreeNode(f.getName());
                        top.add(subfolder);

                        dirTree(f.getAbsolutePath(),subfolder);

                    }
                    else {
                        System.out.println(f.getName());
                        file = new DefaultMutableTreeNode(new FileInfo(f.getName(),f.getAbsolutePath()));
//                        setIcon(file.ICON_EXCEL)
                        top.add(file);
                    }
                }
            }
            else {
                System.out.println(sf.getName());
                file = new DefaultMutableTreeNode(new FileInfo(sf.getName(),sf.getAbsolutePath()));
                top.add(file);
            }

        }

    }
}