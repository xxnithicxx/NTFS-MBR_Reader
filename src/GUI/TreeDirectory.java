package GUI;

import File.*;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import javax.swing.JTree;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import java.io.IOException;
import java.net.URL;
import java.awt.GridLayout;

public class TreeDirectory extends JPanel implements TreeSelectionListener {
    private JEditorPane htmlPane;
    private JTree tree;
    private URL helpURL;
    private static boolean DEBUG = false;
    private static boolean playWithLineStyle = false;
    private static String lineStyle = "Horizontal";
    private static boolean useSystemLookAndFeel = false;
    public TreeDirectory(String path) {
        super(new GridLayout(1,0));

        //Create the nodes.
        int pos = path.lastIndexOf("/");
        System.out.println(pos);
        String name = path.substring(pos+1,path.length());
        DefaultMutableTreeNode top =
                new DefaultMutableTreeNode(name);
        FileOperation f = new FileOperation();
        f.dirTree(path,top);
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        tree.addTreeSelectionListener(this);

        if (playWithLineStyle) {
            System.out.println("line style = " + lineStyle);
            tree.putClientProperty("JTree.lineStyle", lineStyle);
        }
        //Create the scroll pane and add the tree to it.
        JScrollPane treeView = new JScrollPane(tree);
        add(treeView);
    }

    /** Required by TreeSelectionListener interface. */
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();

        if (node == null) return;

        Object nodeInfo = node.getUserObject();
        if (node.isLeaf()) {
            FileInfo file = (FileInfo)nodeInfo;
            try {
                file.displayFile(file.pathFile);
            } catch (IOException | BadLocationException ex) {
                throw new RuntimeException(ex);
            }
            if (DEBUG) {
//                System.out.print(book.bookURL + ":  \n    ");
            }
        } else {
//            displayFile(helpURL);
        }
        if (DEBUG) {
//            System.out.println(nodeInfo.toString());
        }
    }

    private static void createAndShowGUI(String path) {
        if (useSystemLookAndFeel) {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Couldn't use system look and feel.");
            }
        }

        //Create and set up the window.
        JFrame frame = new JFrame("TreeDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new TreeDirectory(path));

        //Display the window.
//        frame.pack();
        frame.setSize(800,500);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI("C:/");
            }
        });
    }
}
