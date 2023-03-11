package GUI;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.tree.*;
import javax.swing.event.*;
import File.*;

import static File.FileNode.resizeIcon;

public class TreeDisk
{
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
    public static final ImageIcon ICON_TXT=
            new ImageIcon("./src/Img/txt.png");

    private JTree  m_tree;
    protected DefaultTreeModel m_model;
    protected JTextField m_display;
    protected JFrame frameDisk;

//    public void rootMain()
//    {
//        DefaultMutableTreeNode root = new DefaultMutableTreeNode(
//                new IconData(FileNode.resizeIcon(ICON_COMPUTER), null, "Computer"));
//
//        DefaultMutableTreeNode node;
//        File[] roots = File.listRoots();
//        for (int k=0; k<roots.length; k++)
//        {
//            node = new DefaultMutableTreeNode(new IconData(FileNode.resizeIcon(ICON_DISK),
//                    null, new FileNode(roots[k])));
//            root.add(node);
//            node.add( new DefaultMutableTreeNode(Boolean.valueOf(true)));
//        }
//        DefaultTreeModel Main_model = new DefaultTreeModel(root);
//        JTree Main_tree = new JTree(Main_model);
//        Main_tree.putClientProperty("JTree.lineStyle", "Angled");
//        int sizeicon = 8;
//        TreeCellRenderer renderer = new
//                IconCellRenderer(sizeicon);
//        Main_tree.setCellRenderer(renderer);
//        Main_tree.addMouseListener(ml_mainTree);
//        Main_tree.setShowsRootHandles(true);
//        Main_tree.setEditable(false);
//        JScrollPane s = new JScrollPane();
//        s.getViewport().add(Main_tree);
//        getContentPane().add(s, BorderLayout.CENTER);
//
//        JTextField Main_display;
//        Main_display = new JTextField();
//        Main_display.setEditable(false);
//        getContentPane().add(Main_display, BorderLayout.NORTH);
//
//        WindowListener wndCloser = new WindowAdapter()
//        {
//            public void windowClosing(WindowEvent e)
//            {
//                System.exit(0);
//            }
//        };
//        addWindowListener(wndCloser);
//
//        setVisible(true);
//    }

    public TreeDisk(DefaultMutableTreeNode disk)
    {
        frameDisk= new JFrame(String.valueOf(disk.getPath()));
        frameDisk.setSize(400, 300);



//        File[] roots = File.listRoots();
//        IconData iconDataDisk= new IconData(disk.getUserObject());
//        FileNode nodeDisk = new FileNode(iconDataDisk.getObject());
//        File f = new File(nodeDisk.m_file.getAbsolutePath());
//        File f = new File(disk.getParentPath());

//
//        FileNode fnode = getFileNode(disk);
//        if (fnode == null) {
//            return;
//        }
//
//
//        File[] listFile = fnode.m_file.listFiles();
//        IconData idata=null;
//
//        for (File sf : listFile) {
//            if (sf.isDirectory()) {
//                idata = new IconData(resizeIcon(ICON_FOLDER),
//                        resizeIcon(ICON_EXPANDEDFOLDER), new FileNode(sf));
//            } else {
//                idata = new IconData(FileNode.iconFile(sf),
//                        FileNode.iconFile(sf), new FileNode(sf));
//            }
//            DefaultMutableTreeNode node = new
//                    DefaultMutableTreeNode(idata);
//            disk.add(node);
//        }
        FileNode t = getFileNode(disk);
        t.expand(disk);
//        for (int k=0; k<roots.length; k++)
//        {
//            node = new DefaultMutableTreeNode(new IconData(FileNode.resizeIcon(ICON_DISK),
//                    null, new FileNode(roots[k])));
//            disk.add(node);
//            node.add( new DefaultMutableTreeNode(Boolean.valueOf(true)));
//        }
        m_model = new DefaultTreeModel(disk);
        m_tree = new JTree(m_model);

        m_tree.putClientProperty("JTree.lineStyle", "Angled");
        int sizeicon = 8;
        TreeCellRenderer renderer = new
                IconCellRenderer(sizeicon);
        m_tree.setCellRenderer(renderer);

        m_tree.addTreeExpansionListener(new
                DirExpansionListener());

        m_tree.addMouseListener(ml);
        m_tree.setShowsRootHandles(true);
        m_tree.setEditable(false);

        JScrollPane s = new JScrollPane();
        s.getViewport().add(m_tree);
        frameDisk.getContentPane().add(s, BorderLayout.CENTER);
        frameDisk.addWindowListener(new java.awt.event.WindowAdapter(){
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {

            }
        });
        m_display = new JTextField();
        m_display.setEditable(false);
        frameDisk.getContentPane().add(m_display, BorderLayout.NORTH);

        frameDisk.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        frameDisk.setVisible(true);
    }



    DefaultMutableTreeNode getTreeNode(TreePath path)
    {
        return (DefaultMutableTreeNode)(path.getLastPathComponent());
    }

    FileNode getFileNode(DefaultMutableTreeNode node)
    {
        if (node == null)
            return null;
        Object obj = node.getUserObject();
        if (obj instanceof IconData)
            obj = ((IconData)obj).getObject();
        if (obj instanceof FileNode)
            return (FileNode)obj;
        else
            return null;
    }


    // Make sure expansion is threaded and updating the tree model
    // only occurs within the event dispatching thread.
    class DirExpansionListener implements TreeExpansionListener
    {
        public void treeExpanded(TreeExpansionEvent event)
        {
            final DefaultMutableTreeNode node = getTreeNode(
                    event.getPath());
            final FileNode fnode = getFileNode(node);
            Object obtemp = node.getUserObject();
            IconData tempicon = new IconData(obtemp);
            System.out.println(tempicon.getIcon());
            Thread runner = new Thread()
            {
                public void run()
                {
                    System.out.println(tempicon.getIcon());
                    if (fnode != null && fnode.expand(node))
                    {
                        Runnable runnable = new Runnable()
                        {
                            public void run()
                            {
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


    MouseListener ml = new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
            int selRow = m_tree.getRowForLocation(e.getX(), e.getY());
            TreePath selPath = m_tree.getPathForLocation(e.getX(), e.getY());
            if(selRow != -1) {
                if(e.getClickCount() == 1) {
                    mySingleClick(selRow,selPath);
                }
                else if(e.getClickCount() == 2) {
                    myDoubleClick(selRow, selPath);
                }
            }
        }
    };

    public void mySingleClick(int row, TreePath path) {
        DefaultMutableTreeNode node = getTreeNode(
                path);
        FileNode fnode = getFileNode(node);
        if (fnode != null){
            m_display.setText(fnode.getFile().
                    getAbsolutePath());
        }
        else
            m_display.setText("");
    }
    public void myDoubleClick(int row, TreePath path) {
        DefaultMutableTreeNode node = getTreeNode(
                path);
        FileNode fnode = getFileNode(node);

        if (node.isLeaf()) {
            try {

                if (isTextFile(fnode.getFile()))
                {
                    fnode.displayFile(fnode.m_file.getAbsolutePath());
                }
                else{
                    JOptionPane.showMessageDialog(frameDisk, "Please open another applitcation to open", "NOTIFICATION", JOptionPane.WARNING_MESSAGE);

                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (BadLocationException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public boolean isTextFile(File file) {
        if (file.getName().endsWith(".txt") ||
                file.getName().endsWith(".rtf") ||file.getName().endsWith(".md")
                || file.getName().endsWith(".markdown")||file.getName().endsWith(".html") ||file.getName().endsWith(".htm")
                ||file.getName().endsWith(".xml")||file.getName().endsWith(".json")||file.getName().endsWith(".yml")
                ||file.getName().endsWith(".yaml")||file.getName().endsWith(".csv")
                ||file.getName().endsWith(".tsv")||file.getName().endsWith(".sql"))
            return true;
        else return false;
    }
}

