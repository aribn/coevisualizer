package coeviz.framework.interfaces;

import coeviz.visualization.views.elements.*;
import java.awt.Image;

public abstract class VisView extends PanelWithContextualImageSave {

	public VisView() {
		super();
	}
	
    public abstract String getName();
    public abstract boolean initiallyVisible();
    public abstract void prepare();
	
	public abstract Image getImage();
    public abstract String getImageType();
}
