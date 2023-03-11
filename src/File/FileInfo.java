package File;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner;
public class FileInfo {
    public String FileName;
    public String pathFile;
    public static final ImageIcon ICON_EXCEL =
            new ImageIcon("./Img/excel.png");
    public static final ImageIcon ICON_WORD =
            new ImageIcon("./Img/word.png");
    public static final ImageIcon ICON_PDF =
            new ImageIcon("./Img/pdffile.png");
    public static final ImageIcon ICON_PPT =
            new ImageIcon("./Img/powerpoint.png");
    public static final ImageIcon ICON_WINRAR =
            new ImageIcon("./Img/winrar.png");
    public FileInfo(String name, String path) {
        FileName = name;
        pathFile = path;
//            path = getClass().getResource(path);
        if (path == null) {
            System.err.println("Couldn't find file: "
                    + name);
        }
    }

    public FileInfo(Object object) {
    }

    public String toString() {
        return FileName;
    }
    public void displayFile(String path) throws IOException, BadLocationException {
        JFrame frame = new JFrame("JTextPane Example");
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
//            pane.add(str);
            System.out.println(str);
//            attributeSet = new SimpleAttributeSet();
            doc.insertString(doc.getLength(), str, null);
        }
        file.close();

        JScrollPane scrollPane = new JScrollPane(pane);
        cp.add(scrollPane, BorderLayout.CENTER);

        frame.setSize(400, 300);
        frame.setVisible(true);
    }

    public void readFileText() throws IOException {
        var fileName = "src/resources/thermopylae.txt";

        try (var br = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), StandardCharsets.UTF_8))) {

            String line;

            while ((line = br.readLine()) != null) {

                System.out.println(line);
            }
        }

    }
}
