package GUI;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.tree.*;
import javax.swing.event.*;
import  javax.swing.JFrame;
import File.*;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

public class TreeComputer extends JFrame{
    public static final ImageIcon ICON_COMPUTER =
            new ImageIcon("./src/Img/computer.png");
    public static final ImageIcon ICON_DISK =
            new ImageIcon("./src/Img/disk.png");
    protected JTree Main_tree;
    protected DefaultTreeModel Main_model;
    protected JTextField Main_display;
    TreeComputer(){
        super("My Computer");
        setSize(400, 300);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(
                new IconData(FileNode.resizeIcon(ICON_COMPUTER), null, "Computer"));

        DefaultMutableTreeNode node;
        File[] roots = File.listRoots();
        for (int k=0; k<roots.length; k++)
        {
            node = new DefaultMutableTreeNode(new IconData(FileNode.resizeIcon(ICON_DISK),
                    null, new FileNode(roots[k])));
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
        JScrollPane s = new JScrollPane();
        s.getViewport().add(Main_tree);
        getContentPane().add(s, BorderLayout.CENTER);


        Main_display = new JTextField();
        Main_display.setEditable(false);
        getContentPane().add(Main_display, BorderLayout.NORTH);

        WindowListener wndCloser = new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        };
        addWindowListener(wndCloser);

        setVisible(true);
    }
    MouseListener ml_mainTree = new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
            int selRow = Main_tree.getRowForLocation(e.getX(), e.getY());
            TreePath selPath = Main_tree.getPathForLocation(e.getX(), e.getY());
            if (selRow != -1) {
                if (e.getClickCount() == 2) {
                    DefaultMutableTreeNode disk = (DefaultMutableTreeNode) (selPath.getLastPathComponent());
                    new TreeDisk(disk);
                }
            }

        }
    };
    public static void main(String []args)
    {
        new TreeComputer();
    }
}
