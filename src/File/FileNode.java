package File;

import GUI.TreeDisk;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.*;
import java.util.Vector;

import static GUI.TreeDisk.ICON_EXPANDEDFOLDER;
import static GUI.TreeDisk.ICON_FOLDER;

public class FileNode {
    public File m_file;

    public FileNode(File file) {
        m_file = file;
    }
    public FileNode(Object object)
    {
        m_file = (File) object;
    }
    public File getFile() {
        return m_file;
    }

    public String toString() {
        return m_file.getName().length() > 0 ? m_file.getName() :
                m_file.getPath();
    }

    public boolean expand(DefaultMutableTreeNode parent) {
//        if(parent)
//        DefaultMutableTreeNode flag =
//                (DefaultMutableTreeNode) parent.getFirstChild();
//        if (flag == null)    // No flag
//            return false;
//        Object obj = flag.getUserObject();
//        if (!(obj instanceof Boolean))
//            return false;      // Already expanded
//
//        parent.removeAllChildren();  // Remove Flag

        File[] files = listFiles();
        if (files == null)
            return true;

        Vector v = new Vector();

        for (int k = 0; k < files.length; k++) {
            File f = files[k];

            FileNode newNode = new FileNode(f);

            boolean isAdded = false;
            for (int i = 0; i < v.size(); i++) {
                FileNode nd = (FileNode) v.elementAt(i);
                if (newNode.compareTo(nd) < 0) {
                    v.insertElementAt(newNode, i);
                    isAdded = true;
                    break;
                }
            }
            if (!isAdded)
                v.addElement(newNode);
        }

        for (int i = 0; i < v.size(); i++) {
            FileNode nd = (FileNode) v.elementAt(i);
            IconData idata=null;
            if (nd.hasSubDirs())
            {
                idata = new IconData(resizeIcon(ICON_FOLDER),
                        resizeIcon(ICON_EXPANDEDFOLDER), nd);
            }
            else{
                idata = new IconData(iconFile(nd.m_file),
                        nd.iconFile(nd.m_file), nd);
            }
            DefaultMutableTreeNode node = new
                    DefaultMutableTreeNode(idata);
            parent.add(node);

            if (nd.hasSubDirs())
                node.add(new DefaultMutableTreeNode(
                        Boolean.valueOf(true)));
        }

        return true;
    }
    public static ImageIcon iconFile(File file){
        ImageIcon temp = null;
        if (file.isFile() && (
                file.getName().endsWith(".jpg") ||
                        file.getName().endsWith(".jpeg") ||
                        file.getName().endsWith(".png") ||
                        file.getName().endsWith(".gif") ||
                        file.getName().endsWith(".bmp"))) {
            temp = TreeDisk.ICON_IMG;
        }
        else if (file.isFile() && (
                file.getName().endsWith(".xlsx") ||
                        file.getName().endsWith(".xls"))) {
            temp =  TreeDisk.ICON_EXCEL;
        }
        else if (file.isFile() && (
                file.getName().endsWith(".docx") ||
                        file.getName().endsWith(".doc"))) {
            temp = TreeDisk.ICON_WORD;
        }
        else if (file.isFile() && (
                file.getName().endsWith(".pptx") ||
                        file.getName().endsWith(".ppt"))) {
            temp=  TreeDisk.ICON_PPT;
        }
        else if (file.isFile() &&
                file.getName().endsWith(".pdf")) {
            temp =  TreeDisk.ICON_PDF;
        }
        else if (file.isFile() &&
                file.getName().endsWith(".rar")) {
            temp =  TreeDisk.ICON_WINRAR;
        }
        else if (file.isFile() &&
                file.getName().endsWith(".txt")) {
            temp = TreeDisk.ICON_TXT;
        }
        else return null;

        return resizeIcon(temp);
    }

    public static ImageIcon resizeIcon(ImageIcon temp){
        Image img = temp.getImage();
        img = img.getScaledInstance(13,13,Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public boolean hasSubDirs() {
        File[] files = listFiles();
        if (files == null)
            return false;
        for (int k = 0; k < files.length; k++) {
            if (files[k].isDirectory())
                return true;
        }
        return false;
    }

    public int compareTo(FileNode toCompare) {
        return m_file.getName().compareToIgnoreCase(
                toCompare.m_file.getName());
    }

    protected File[] listFiles() {
        if (m_file == null) return null;
        if (!m_file.isDirectory())
            return null;
        try {
            return m_file.listFiles();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error reading directory " + m_file.getAbsolutePath(),
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }
    public void displayFile(String path) throws IOException, BadLocationException {

        JFrame frame = new JFrame(path.substring(path.lastIndexOf('/')+1,path.length()));
        ImageIcon img = new ImageIcon("/Users/user/Practice/Tree/TestTree/src/Img/txt.png");
        frame.setIconImage(img.getImage());
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        Container cp = frame.getContentPane();
        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setBold(attributeSet, true);
        Document doc = pane.getStyledDocument();


        String namepath = path;
        RandomAccessFile file = new RandomAccessFile(namepath, "r");
        String str;
        InputStream inputStream = new FileInputStream(path);
        byte[] buffer = new byte[512];
        inputStream.read(buffer, 0, buffer.length);
        while ((str = file.readLine()) != null) {
            System.out.println(str);
            doc.insertString(doc.getLength(), str, null);
        }
        file.close();

        JScrollPane scrollPane = new JScrollPane(pane);
        cp.add(scrollPane, BorderLayout.CENTER);

        frame.setSize(400, 300);
        frame.setVisible(true);
    }
}
