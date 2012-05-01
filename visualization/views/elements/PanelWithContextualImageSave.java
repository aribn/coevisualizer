package coeviz.visualization.views.elements;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;

// for saving images.
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.imageio.*;
import java.io.*;

import coeviz.framework.interfaces.*;

public abstract class PanelWithContextualImageSave extends JPanel {
	
    public JPopupMenu popup;
    private File loc;
    private JFileChooser fc;
	
    public PanelWithContextualImageSave() {
        super();
		
		//if (saveable) {
		fc = new JFileChooser();
		
		// Make the popup menu.
		popup = new JPopupMenu();
		final String jpg = "Save as JPG...";
		final String png = "Save as PNG...";
		JMenuItem menuItemJPG = new JMenuItem(jpg);
		JMenuItem menuItemPNG = new JMenuItem(png);
		menuItemJPG.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { saveImage("jpg"); } });
		menuItemPNG.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { saveImage("png"); } });
		popup.add(menuItemJPG);
		popup.add(menuItemPNG);
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) { if (!maybeShowPopup(e)) handleClick(e); }
			public void mouseReleased(MouseEvent e) { maybeShowPopup(e); }
			private boolean maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup.show(e.getComponent(), e.getX(), e.getY());
					return true;
				}
				else return false;
			}
		});
	}
	
	public void recordPops(Game game, Candidate[] cPop, Test[] tPop) {
		
		String gameName = game.getClass().getName();
		gameName = gameName.substring( 1+gameName.lastIndexOf("_"), gameName.length()); 
		
		String repNameCand = ""; 
		String repNameTest = ""; 
		
		repNameCand = cPop[0].getClass().getName();
		repNameCand = repNameCand.substring( 1+repNameCand.lastIndexOf("_"), repNameCand.length()); 
		repNameCand = "" + cPop.length + "c_" + repNameCand; 
		
		repNameTest = tPop[0].getClass().getName();
		repNameTest = repNameTest.substring( 1+repNameTest.lastIndexOf("_"), repNameTest.length()); 
		repNameTest = "" + tPop.length + "t_" + repNameTest; 
		
		String filenameCand = "Init_"+gameName+"_"+repNameCand+".txt"; 
		String filenameTest = "Init_"+gameName+"_"+repNameTest+".txt"; 
		
		System.out.println(filenameCand); 
		System.out.println(filenameTest); 
		String line = "";
		
		try {
			
			File dir = new File(System.getProperty("user.dir"));
			if (!dir.isDirectory())
				throw new IllegalArgumentException("no such directory");
			
			dir = new File(dir.getParent());
			dir = new File(dir, "scripts");
			dir = new File(dir, "gecco");
			
			File candFile = new File(dir, filenameCand);
			File testFile = new File(dir, filenameTest);
			
			PrintStream psCand = new PrintStream(new BufferedOutputStream(new FileOutputStream(candFile,false)),true);
			PrintStream psTest = new PrintStream(new BufferedOutputStream(new FileOutputStream(testFile,false)),true);
			
			// write all values and print a new line
			for (int i=0; i<cPop.length; i++) 
				psCand.print(cPop[i].toString() + "\t");
			psCand.println("");
			
			// write all values and print a new line
			for (int i=0; i<tPop.length; i++) 
				psTest.print(tPop[i].toString() + "\t");
			psTest.println("");
			
			psCand.flush();
			psTest.flush();
			psCand.close();
			psTest.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void handleClick(MouseEvent e) {
		//System.out.println(e.toString());
	}
	
	public void addMenuItem(JMenuItem jmi) {
		popup.add(jmi); 
	}
	
	public void autoSavePNG() {
		Image img = getImage();
		int width = img.getWidth(this);
		int height = img.getHeight(this);
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.setColor(Color.red);
		g2d.fillRect(0, 0, width, height);
		boolean success = g2d.drawImage(img, new AffineTransform(), this);
		g2d.dispose();
		RenderedImage rendImage = (RenderedImage) bufferedImage;
		try {
			String filename = getImageType() + ".png"; 
			File file = new File(loc, filename);
			System.out.println(file.getAbsolutePath()); 
			ImageIO.write(rendImage, "png", file);
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	
	public void saveImage(String type) {
		Image img = getImage();
		int width = img.getWidth(this);
		int height = img.getHeight(this);
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.setColor(Color.red);
		g2d.fillRect(0, 0, width, height);
		boolean success = g2d.drawImage(img, new AffineTransform(), this);
		g2d.dispose();
		RenderedImage rendImage = (RenderedImage) bufferedImage;
		try {
			
			fc.setDialogTitle("Save image as...");
			File file = new File(loc, "" + getGeneration() + "_" + getImageType() + "." + type);
			File can = new File (file.getCanonicalPath());
			fc.setSelectedFile(can);
			
			if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file2 = fc.getSelectedFile();
				ImageIO.write(rendImage, type, file2);
			}
			
		} catch (IOException e) { e.printStackTrace(); }
		
	}
	
	
	public void setLoc(File f) { loc = f; }
	
	public abstract Image getImage();
	public abstract String getImageType();
	public abstract int getGeneration(); 
	}