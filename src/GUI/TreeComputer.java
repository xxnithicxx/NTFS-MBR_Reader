package GUI;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.tree.*;
import  javax.swing.JFrame;
import File.*;

public class TreeComputer {
    public static final ImageIcon ICON_COMPUTER =
            new ImageIcon("./src/Img/computer.png");
    public static final ImageIcon ICON_DISK =
            new ImageIcon("./src/Img/disk.png");
    public static JTree Main_tree;
    protected DefaultTreeModel Main_model;
    protected JTextField Main_display;
    public static JScrollPane s;
    public static JFrame frameComputer;
    public TreeComputer(){
        frameComputer = new JFrame("My Computer");

        frameComputer.setSize(400, 300);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(
                new IconData(FileNode.resizeIcon(ICON_COMPUTER), null, "Computer"));

        DefaultMutableTreeNode node;
        File[] roots = File.listRoots();
        for (File file : roots) {
            node = new DefaultMutableTreeNode(new IconData(FileNode.resizeIcon(ICON_DISK),
                    null, new FileNode(file)));
            root.add(node);
        }

        Main_model = new DefaultTreeModel(root);
        Main_tree = new JTree(Main_model);
        Main_tree.putClientProperty("JTree.lineStyle", "Angled");
        int sizeicon = 8;
        TreeCellRenderer renderer = new
                IconCellRenderer(sizeicon);
        Main_tree.setCellRenderer(renderer);
        Main_tree.addMouseListener(ml_mainTree);
        Main_tree.setShowsRootHandles(true);
        Main_tree.setEditable(false);
        s = new JScrollPane();
        s.getViewport().add(Main_tree);
        frameComputer.getContentPane().add(s, BorderLayout.CENTER);

        Main_display = new JTextField();
        Main_display.setEditable(false);

        frameComputer.add(Main_display, BorderLayout.NORTH);

        WindowListener wndCloser = new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        };
        frameComputer.addWindowListener(wndCloser);

        frameComputer.setLocationRelativeTo(null);

        frameComputer.setVisible(true);
    }

    MouseListener ml_mainTree = new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
            int selRow = Main_tree.getRowForLocation(e.getX(), e.getY());
            TreePath selPath = Main_tree.getPathForLocation(e.getX(), e.getY());
            if (selRow != -1) {
                if (e.getClickCount() == 2) {
                    assert selPath != null;
                    DefaultMutableTreeNode disk = (DefaultMutableTreeNode) (selPath.getLastPathComponent());

                    new FileTree(disk);
                    frameComputer.setEnabled(false);
                }
            }
        }
    };
    public static void main(String []args)
    {
        new TreeComputer();
    }
}
