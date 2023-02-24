package GUI;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class TreeDirectory {
    private JTree root;

    public TreeDirectory() {
        JFrame jFrame = new JFrame("Tree Directory");

        this.root = new JTree();

        jFrame.add(root);
        jFrame.pack();

        jFrame.setVisible(true);
    }

    public static void main(String[] args) {
        new TreeDirectory();
    }

//    Write function to add node to tree

//    Write function to remove node from tree

}
