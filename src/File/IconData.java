package File;

import javax.swing.*;

public class IconData {
    protected Icon m_icon;
    protected Icon m_expandedIcon;
    protected Object m_data;

    public IconData(Icon icon, Icon expandedIcon, Object data) {
        m_icon = icon;
        m_expandedIcon = expandedIcon;
        m_data = data;
    }
    public IconData(Object obtemp) {
        m_data = obtemp;
    }

    public Icon getIcon() {
        return m_icon;
    }

    public Icon getExpandedIcon() {
        return m_expandedIcon != null ? m_expandedIcon : m_icon;
    }

    public Object getObject() {
        return m_data;
    }

    @Override
    public String toString() {
        return m_data.toString();
    }
}
