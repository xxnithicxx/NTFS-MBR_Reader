package GUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.tree.*;
import javax.swing.event.*;
import File.*;
import static javax.swing.text.StyleConstants.Size;

public class FileTree1 extends JFrame implements TreeSelectionListener
{
    public static final ImageIcon ICON_COMPUTER =
            new ImageIcon("computer.gif");
    public static final ImageIcon ICON_DISK =
            new ImageIcon("disk.gif");
    public static final ImageIcon ICON_FOLDER =
            new ImageIcon("./Img/folder.png");
    public static final ImageIcon ICON_EXPANDEDFOLDER =
            new ImageIcon("./Img/openfolder.png");
    public static final ImageIcon ICON_IMG =
            new ImageIcon("./Img/image.png");
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

    protected JTree  m_tree;
    protected DefaultTreeModel m_model;
    protected JTextField m_display;

    public FileTree1()
    {
        super("My Computer");
        setSize(400, 300);

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(
                new IconData(ICON_COMPUTER, null, "Computer"));

        DefaultMutableTreeNode node;
        File[] roots = File.listRoots();
        for (int k=0; k<roots.length; k++)
        {
            node = new DefaultMutableTreeNode(new IconData(ICON_DISK,
                    null, new FileNode(roots[k])));
            top.add(node);
            node.add( new DefaultMutableTreeNode(Boolean.valueOf(true)));
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

//        m_tree.addTreeSelectionListener(new
//                DirSelectionListener());
        m_tree.addMouseListener(ml);
        m_tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        m_tree.addTreeSelectionListener(this);
        m_tree.setShowsRootHandles(true);
        m_tree.setEditable(false);

        JScrollPane s = new JScrollPane();
        s.getViewport().add(m_tree);
        getContentPane().add(s, BorderLayout.CENTER);

        m_display = new JTextField();
        m_display.setEditable(false);
        getContentPane().add(m_display, BorderLayout.NORTH);

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

    @Override
    public void valueChanged(TreeSelectionEvent e) {
//        DefaultMutableTreeNode DefaultMutableTreeNode;

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

            Thread runner = new Thread()
            {
                public void run()
                {
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
                    JOptionPane.showMessageDialog(this, "Please open another applitcation to open", "NOTIFICATION", JOptionPane.WARNING_MESSAGE);

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

    Image resizeIcon(String path, Label label){
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image dimg = img.getScaledInstance(label.getWidth(), label.getHeight(),
                Image.SCALE_SMOOTH);
        return dimg;
    }

    public static void main(String argv[])
    {
        new FileTree1();
    }
}

