package File;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class IconCellRenderer
        extends JLabel
        implements TreeCellRenderer {
    protected Color m_textSelectionColor;
    protected Color m_textNonSelectionColor;
    protected Color m_bkSelectionColor;
    protected Color m_bkNonSelectionColor;
    protected Color m_borderSelectionColor;

    protected boolean m_selected;
    private int iconSize;

    public IconCellRenderer(int iconSize) {
        super();
        this.iconSize = iconSize;
        m_textSelectionColor = UIManager.getColor(
                "Tree.selectionForeground");
        m_textNonSelectionColor = UIManager.getColor(
                "Tree.textForeground");
        m_bkSelectionColor = UIManager.getColor(
                "Tree.selectionBackground");
        m_bkNonSelectionColor = UIManager.getColor(
                "Tree.textBackground");
        m_borderSelectionColor = UIManager.getColor(
                "Tree.selectionBorderColor");
        setOpaque(false);
    }
    @Override
//    public Component getTreeCellRendererComponent(JTree tree, Object value,
//                                                  boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
//
//        Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
//
//        if (value instanceof DefaultMutableTreeNode) {
//            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
//            Object userObject = node.getUserObject();
//            if (userObject instanceof ImageIcon) {
//                ImageIcon icon = (ImageIcon) userObject;
//                Image img = icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
//                ImageIcon scaledIcon = new ImageIcon(img);
//                setIcon(scaledIcon);
//            }
//        }
//        return c;
//    }
    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value, boolean sel, boolean expanded, boolean leaf,
                                                  int row, boolean hasFocus) {
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) value;
        Object obj = node.getUserObject();
        setText(obj.toString());

        if (obj instanceof Boolean)
            setText("Retrieving data...");

        if (obj instanceof IconData) {
            IconData idata = (IconData) obj;
//            tree.setCellRenderer((TreeCellRenderer)idata.m_icon);
//            idata.size=10;
//            tree.setCellRenderer((TreeCellRenderer) idata);
            if (expanded)
                setIcon(idata.getExpandedIcon());
            else
                setIcon(idata.getIcon());
        } else
            setIcon(null);

        setFont(tree.getFont());
        setForeground(sel ? m_textSelectionColor :
                m_textNonSelectionColor);
        setBackground(sel ? m_bkSelectionColor :
                m_bkNonSelectionColor);
        m_selected = sel;
        return this;
    }

    public void paintComponent(Graphics g) {
        Color bColor = getBackground();
        Icon icon = getIcon();

        g.setColor(bColor);
        int offset = 0;
        if (icon != null && getText() != null)
            offset = (icon.getIconWidth() + getIconTextGap());
        g.fillRect(offset, 0, getWidth() - 1 - offset,
                getHeight() - 1);

        if (m_selected) {
            g.setColor(m_borderSelectionColor);
            g.drawRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);
        }
        super.paintComponent(g);
    }
}
