package File;

import Entity.ItemDataObject;
import GUI.FileTree;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.*;
import java.util.Vector;

import static GUI.FileTree.*;

public class ItemNode {
    public ItemDataObject m_item;

    public ItemNode(ItemDataObject file) {
        m_item = file;
    }

    public ItemDataObject getFile() {
        return m_item;
    }

    public String toString() {
        return m_item.getName().length() > 0 ? m_item.toString() :
                m_item.getPath(m_item);
    }

    public boolean expand(DefaultMutableTreeNode parent) {
        DefaultMutableTreeNode flag = (DefaultMutableTreeNode) parent.getFirstChild();
        if (flag == null)
            return false;               // No flag
        Object obj = flag.getUserObject();
        if (!(obj instanceof Boolean))
            return false;               // Already expanded

        parent.removeAllChildren();     // Remove Flag

        ItemDataObject[] files = listFiles();
        if (files == null)
            return true;

        Vector<ItemNode> v = new Vector<>();

        for (ItemDataObject file : files) {
            ItemNode newNode = new ItemNode(file);

            boolean isAdded = false;
            for (int i = 0; i < v.size(); i++) {
                ItemNode nd = v.elementAt(i);
                if (newNode.compareTo(nd) < 0) {
                    v.insertElementAt(newNode, i);
                    isAdded = true;
                    break;
                }
            }
            if (!isAdded)
                v.addElement(newNode);
        }

        for (ItemNode nd : v) {
            IconData iData;

            if (nd.hasSubDirs())
            {
                iData = new IconData(resizeIcon(ICON_FOLDER),
                        resizeIcon(ICON_EXPANDEDFOLDER), nd);
            }
            else{
                iData = new IconData(getImageIcon(nd.m_item),
                        getImageIcon(nd.m_item), nd);
            }

            DefaultMutableTreeNode node = new
                    DefaultMutableTreeNode(iData);
            parent.add(node);

            if (nd.hasSubDirs())
                node.add(new DefaultMutableTreeNode(
                        Boolean.TRUE));
        }

        return true;
    }

    public static ImageIcon resizeIcon(ImageIcon temp){
        Image img = temp.getImage();
        img = img.getScaledInstance(13,13,Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public boolean hasSubDirs() {
        ItemDataObject[] files = listFiles();
        if (files == null)
            return false;

        for (ItemDataObject file : files) {
            if (file.isFolder())
                return true;
        }

        return false;
    }

    public int compareTo(ItemNode toCompare) {
        return m_item.getName().compareToIgnoreCase(
                toCompare.m_item.getName());
    }

    protected ItemDataObject[] listFiles() {
        return m_item.listFiles();
    }

    public void displayFile() throws IOException, BadLocationException {
        JFrame frame = new JFrame(this.m_item.getPath(this.m_item));
        ImageIcon img = new ImageIcon("src/Img/txt.png");
        frame.setIconImage(img.getImage());
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        Container cp = frame.getContentPane();
        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setBold(attributeSet, true);
        Document doc = pane.getStyledDocument();

        String str = this.m_item.getTxtData();
        doc.insertString(doc.getLength(), str, null);

        JScrollPane scrollPane = new JScrollPane(pane);
        cp.add(scrollPane, BorderLayout.CENTER);

        frame.setSize(400, 300);
        frame.setVisible(true);
    }
}
