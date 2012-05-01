package coeviz.visualization.views;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

import coeviz.visualization.ViewerPanel;


public class PropertiesViewPanel extends ViewerPanel {

    Vector jtfs;
    JPanel main;
    
    public PropertiesViewPanel(String runDir, Properties properties) {
        super(runDir);

        TitledBorder border = new TitledBorder(new LineBorder(Color.gray), "[1] Parameters");
        border.setTitleColor(Color.black);
        this.setBorder(border);

        GridLayout grid = new GridLayout();
        JPanel container = new JPanel();
        
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setAlignmentY(Component.CENTER_ALIGNMENT);

        container.setLayout(grid);
        container.setOpaque(false);

        int propCount = 0;
        jtfs = new Vector();
        
        try {

            for (Enumeration e = properties.propertyNames(); e.hasMoreElements() ;) {

                String nextProperty = e.nextElement().toString();
                String nextValue = properties.getProperty(nextProperty);

                JLabel jl = new JLabel(nextProperty + ": ", SwingConstants.RIGHT);
                JLabel jtf = new JLabel(nextValue);

                jl.setFont(new Font("Monospaced", Font.PLAIN, 10));
                jtf.setFont(new Font("Monospaced", Font.PLAIN, 10));

                jtfs.add(jtf);

                container.add(jl);
                container.add(jtf);

                propCount++;
            }

            grid.setColumns(2);
            grid.setRows(propCount);

            this.add(container);
            
        } catch (Exception ex) {ex.printStackTrace(); }
    }

    public String getName() {
        return "Parameters";
    }
    public boolean initiallyVisible() {
        return false;
    }
	public void prepare() {}
	
	public void autoSaveImage() {}
}