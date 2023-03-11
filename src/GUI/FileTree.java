package GUI;

import Entity.DirTreeAbs;
import Entity.FileSystemFactory;
import Entity.Global;
import Entity.ItemDataObject;
import File.*;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static Entity.Global.dirTreeAbs;
import static File.ItemNode.resizeIcon;

public class FileTree  {
    public static final ImageIcon ICON_COMPUTER =
            new ImageIcon("./src/Img/computer.png");
    public static final ImageIcon ICON_DISK =
            new ImageIcon("./src/Img/disk.png");
    public static final ImageIcon ICON_FOLDER =
            new ImageIcon("./src/Img/folder.png");
    public static final ImageIcon ICON_EXPANDEDFOLDER =
            new ImageIcon("./src/Img/openfolder.png");
    public static final ImageIcon ICON_IMG =
            new ImageIcon("./src/Img/image.png");
    public static final ImageIcon ICON_EXCEL =
            new ImageIcon("./src/Img/excel.png");
    public static final ImageIcon ICON_WORD =
            new ImageIcon("./src/Img/word.png");
    public static final ImageIcon ICON_PDF =
            new ImageIcon("./src/Img/pdffile.png");
    public static final ImageIcon ICON_PPT =
            new ImageIcon("./src/Img/powerpoint.png");
    public static final ImageIcon ICON_WINRAR =
            new ImageIcon("./src/Img/winrar.png");
    public static final ImageIcon ICON_TXT =
            new ImageIcon("./src/Img/txt.png");

    protected JTree m_tree;
    protected DefaultTreeModel m_model;
    protected JTextField m_display;
    protected JFrame frameDisk;
    MouseListener ml = new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
            int selRow = m_tree.getRowForLocation(e.getX(), e.getY());
            TreePath selPath = m_tree.getPathForLocation(e.getX(), e.getY());
            if (selRow != -1) {
                if (e.getClickCount() == 1) {
                    mySingleClick(selRow, selPath);
                } else if (e.getClickCount() == 2) {
                    myDoubleClick(selRow, selPath);
                }
            }
        }
    };

    public FileTree(DefaultMutableTreeNode disk) {
        frameDisk= new JFrame(String.valueOf(disk.getPath()));
        frameDisk.setSize(400, 300);

        Global.mainPath = "\\\\.\\" + disk.getPath()[1].toString().substring(0, 1) + ":";

        dirTreeAbs = FileSystemFactory.getFileSystem();
        List<ItemDataObject> items = dirTreeAbs.getRoot().getChildrens();

        ImageIcon icon = resizeIcon(ICON_DISK);
//        Init the disk node
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(
                new IconData(icon, null, new ItemNode(dirTreeAbs.getRoot())));

        for (ItemDataObject item : items) {
            icon = getImageIcon(item);
//            Check why the icon can't be resized
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(
                    new IconData(icon, null, new ItemNode(item)));

            if (item.isFolder())
                node.add(new DefaultMutableTreeNode(Boolean.TRUE));

            top.add(node);
        }

        m_model = new DefaultTreeModel(top);
        m_tree = new JTree(m_model);

        m_tree.putClientProperty("JTree.lineStyle", "Angled");
        int sizeicon = 8;
        TreeCellRenderer renderer = new
                IconCellRenderer(sizeicon);
        m_tree.setCellRenderer(renderer);
        m_tree.addTreeExpansionListener(new
                DirExpansionListener());
        m_tree.addMouseListener(ml);
        m_tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        m_tree.setShowsRootHandles(true);
        m_tree.setEditable(false);

        JScrollPane s = new JScrollPane();
        s.getViewport().add(m_tree);
        frameDisk.getContentPane().add(s, BorderLayout.CENTER);

        m_display = new JTextField();
        m_display.setEditable(false);
        frameDisk.getContentPane().add(m_display, BorderLayout.NORTH);

        WindowListener wndCloser = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        };

        frameDisk.addWindowListener(new java.awt.event.WindowAdapter(){
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                TreeComputer.frameComputer.setEnabled(true);
            }
        });

        frameDisk.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frameDisk.setVisible(true);
    }

    public static ImageIcon getImageIcon(ItemDataObject item) {
        ImageIcon icon;
        if (item.isFolder())
            icon = ICON_FOLDER;
        else {
            if (isTextFile(item.getName())) {
                return resizeIcon(ICON_TXT);
            }

            String extension = item.getExtension();

            switch (extension) {
                case "jpg":
                case "png":
                case "gif":
                case "bmp":
                    icon = ICON_IMG;
                    break;
                case "doc":
                case "docx":
                    icon = ICON_WORD;
                    break;
                case "xls":
                case "xlsx":
                    icon = ICON_EXCEL;
                    break;
                case "ppt":
                case "pptx":
                    icon = ICON_PPT;
                    break;
                case "pdf":
                    icon = ICON_PDF;
                    break;
                case "rar":
                case "zip":
                    icon = ICON_WINRAR;
                    break;
                default:
                    icon = ICON_FOLDER;
                    break;
            }
        }

        return resizeIcon(icon);
    }

    public static boolean isTextFile(String file) {
        file = file.toLowerCase();
        return file.endsWith(".txt") || file.endsWith(".rtf") || file.endsWith(".md")
                || file.endsWith(".markdown") || file.endsWith(".html") || file.endsWith(".htm")
                || file.endsWith(".xml") || file.endsWith(".json") || file.endsWith(".yml")
                || file.endsWith(".yaml") || file.endsWith(".csv") || file.endsWith(".tsv")
                || file.endsWith(".sql");
    }

    DefaultMutableTreeNode getTreeNode(TreePath path) {
        return (DefaultMutableTreeNode) (path.getLastPathComponent());
    }

    ItemNode getFileNode(DefaultMutableTreeNode node) {
        if (node == null)
            return null;
        Object obj = node.getUserObject();
        if (obj instanceof IconData)
            obj = ((IconData) obj).getObject();
        if (obj instanceof ItemNode)
            return (ItemNode) obj;
        else
            return null;
    }

    //    Add tree to get ablosute path
    public void mySingleClick(int row, TreePath path) {
        DefaultMutableTreeNode node = getTreeNode(path);
        ItemNode fnode = getFileNode(node);
        if (fnode != null) {
            m_display.setText(dirTreeAbs.getPath(fnode.getFile()));
        } else
            m_display.setText("");
    }

    public void myDoubleClick(int row, TreePath path) {
        DefaultMutableTreeNode node = getTreeNode(path);
        ItemNode fnode = getFileNode(node);

        if (node.isLeaf() && !fnode.getFile().isFolder()) {
            try {
                if (isTextFile(fnode.getFile().getName())) {
                    fnode.displayFile();
                } else {
                    JOptionPane.showMessageDialog(frameDisk, "Please open another applitcation to open", "NOTIFICATION", JOptionPane.WARNING_MESSAGE);
                }
            } catch (IOException | BadLocationException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    // Make sure expansion is threaded and updating the tree model
    // only occurs within the event dispatching thread.
    class DirExpansionListener implements TreeExpansionListener {
        public void treeExpanded(TreeExpansionEvent event) {
            final DefaultMutableTreeNode node = getTreeNode(event.getPath());
            final ItemNode fnode = getFileNode(node);
            Thread runner = new Thread() {
                public void run() {
                    if (fnode != null && fnode.expand(node)) {
                        Runnable runnable = new Runnable() {
                            public void run() {
                                m_model.reload(node);
                            }
                        };
                        SwingUtilities.invokeLater(runnable);
                    }
                }
            };
            runner.start();
        }

        public void treeCollapsed(TreeExpansionEvent event) {}
    }
}