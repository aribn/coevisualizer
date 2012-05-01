package coeviz.visualization.views;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.io.File;

import coeviz.visualization.*;
import coeviz.visualization.views.elements.*;
import coeviz.framework.interfaces.*;
import coeviz.visualization.ViewerPanel;


public class ProgressViewPanel extends ViewerPanel {

    private ChildPanel cp;
	Viewer v; 
	int memWin; 
	int memUpdateFreq;
    
    public ProgressViewPanel(String runDir, Viewer v, int genCount, int maxPixel,
							 int memWin, int memUpdateFreq) {
		super(runDir);
		
		this.v = v; 
		this.memWin = memWin;
		this.memUpdateFreq = memUpdateFreq;
		
		if (memWin == 1) genCount = 1; 
		
        cp = new ChildPanel(genCount, maxPixel);

        TitledBorder border = new TitledBorder(new LineBorder(Color.gray), "[4] Cumulative Memory");
        border.setTitleColor(Color.black);
        this.setBorder(border);
        
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        Dimension size = new Dimension(genCount, genCount);
        Dimension bigger = new Dimension(genCount+2*Rule.SIZE, genCount+2*Rule.SIZE);
        Dimension smaller = new Dimension(150, 150);
		
        Rule columnView = new Rule(Rule.HORIZONTAL, true);
        columnView.setPreferredWidth((int)size.getWidth());
		
        Rule rowView = new Rule(Rule.VERTICAL, true);
        rowView.setPreferredHeight((int)size.getHeight());

        JScrollPane pictureScrollPane = new JScrollPane(cp);

        pictureScrollPane.setSize(bigger);
        pictureScrollPane.setPreferredSize(bigger);
        pictureScrollPane.setMaximumSize(bigger);
        pictureScrollPane.setViewportBorder(BorderFactory.createLineBorder(Color.black));
        pictureScrollPane.setColumnHeaderView(columnView);
        pictureScrollPane.setRowHeaderView(rowView);
        pictureScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,  new Corner());
        pictureScrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER,  new Corner());
        pictureScrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new Corner());
        pictureScrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, new Corner());
		pictureScrollPane.setWheelScrollingEnabled(true); 

        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        pictureScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

		this.setSize(bigger);
		this.setPreferredSize(bigger);
        this.setMinimumSize(smaller);
		this.setMaximumSize(new Dimension(10000, (int)bigger.getHeight()));
		
        
        this.add(pictureScrollPane);
        
    }

    public void prepare() {
        cp.prepare();
    }
	
    public void colorRect(int x, int y, int color, int size) {
        cp.colorRect(x,y,color, size);
    }
	
    public void doPaint() {
        cp.repaint();
    }
	
    public String getName() {
        return "Cumulative Memory";
    }
	
    public boolean initiallyVisible() {
        return false;
    }
    
    public void doClick(MouseEvent e) {
        int i = e.getX();
        int j = e.getY();
		v.renderWins(j,i); 
    }
    
	public void autoSaveImage() {
		cp.autoSavePNG(); 
	}
	
	public void updateState(int gen, int[] newest_cand_results, int[] newest_test_results) {
		if (((gen % memUpdateFreq) == 0) && (memWin!=1)) {
			int start = gen-(memUpdateFreq*(memWin-1));
			for (int mem_index = 0; mem_index < memWin; mem_index++) {
				if (start+(memUpdateFreq*mem_index) >= 0) {
					colorRect( gen,  (start+(memUpdateFreq*mem_index)),   newest_cand_results[mem_index], memUpdateFreq);
					colorRect( (start+(memUpdateFreq*mem_index)),  gen,   newest_test_results[mem_index], memUpdateFreq);
				}
			}
		}	
	}
	
	
	/************* Double-buffered child panel can save snapshot image to disk ***************/


    public class ChildPanel 
		extends PanelWithContextualImageSave implements Scrollable, MouseMotionListener {

        private Image buffer;
        private Graphics bufferGraphics;
        private int maxPixel;
        private int genCount;

        private Color backgroundColor;
        private Dimension size;
        private int maxUnitIncrement = (int)( (double)Toolkit.getDefaultToolkit().getScreenResolution() / (double)2.54 );

        public ChildPanel(int genCount, int maxPixel) {
            super();
			setLoc(getCurrentImageDir());

            backgroundColor = Color.red;

            this.setOpaque(false);
            this.maxPixel = maxPixel;
            this.genCount = genCount;

            this.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.setAlignmentY(Component.CENTER_ALIGNMENT);

            size = new Dimension(genCount, genCount);
            this.setSize(size);
            this.setPreferredSize(size);

            //Let the user scroll by dragging to outside the window.
            setAutoscrolls(true);
            addMouseMotionListener(this);
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) { 
					if (!e.isPopupTrigger()) doClick(e); 
				}
			});
			
        }

		public void prepare() {
            buffer = this.createImage(genCount,genCount);
            bufferGraphics = buffer.getGraphics();
            bufferGraphics.setColor(backgroundColor);
            bufferGraphics.fillRect(0,0,genCount,genCount);
            bufferGraphics.setColor(Color.black);
            bufferGraphics.drawLine(0,0,genCount,genCount);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(buffer,0,0,this);
        }

        public void colorRect(int x, int y, int color, int size) {
            float c = (1.0f * color / maxPixel);
            bufferGraphics.setColor(new Color(c,c,c));
            bufferGraphics.fillRect(y,x, size, size);
        }

        public Dimension getPreferredSize() {
            return size;
        }
		
		
		/************* For PanelWithContextualImageSave ***************/

		
        public String getImageType() {
            return "ProgressView";
        }
        public Image getImage() {
            return buffer;
        }
		public int getGeneration() {
			return getGen();
		}
		
		
		
		/************* For MouseMotionListener interface ***************/

		
		
		
        //Methods required by the MouseMotionListener interface:
        public void mouseMoved(MouseEvent e) { }
		
        public void mouseDragged(MouseEvent e) {
            //The user is dragging us, so scroll!
            Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
            scrollRectToVisible(r);
        }

		
		
		
		/************* For Scrollable interface ***************/




        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        public int getScrollableUnitIncrement(Rectangle visibleRect,
                                              int orientation,
                                              int direction) {
            //Get the current position.
            int currentPosition = 0;
            if (orientation == SwingConstants.HORIZONTAL) {
                currentPosition = visibleRect.x;
            } else {
                currentPosition = visibleRect.y;
            }

            //Return the number of pixels between currentPosition
            //and the nearest tick mark in the indicated direction.
            if (direction < 0) {
                int newPosition = currentPosition -
                (currentPosition / maxUnitIncrement)
                * maxUnitIncrement;
                return (newPosition == 0) ? maxUnitIncrement : newPosition;
            } else {
                return ((currentPosition / maxUnitIncrement) + 1)
                * maxUnitIncrement
                - currentPosition;
            }
        }

        public int getScrollableBlockIncrement(Rectangle visibleRect,
                                               int orientation,
                                               int direction) {
            if (orientation == SwingConstants.HORIZONTAL) {
                return visibleRect.width - maxUnitIncrement;
            } else {
                return visibleRect.height - maxUnitIncrement;
            }
        }

        public boolean getScrollableTracksViewportWidth() {
            return false;
        }

        public boolean getScrollableTracksViewportHeight() {
            return false;
        }

        public void setMaxUnitIncrement(int pixels) {
            maxUnitIncrement = pixels;
        }
    }
}