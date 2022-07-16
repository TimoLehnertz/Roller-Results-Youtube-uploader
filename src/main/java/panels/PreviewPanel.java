package panels;

import java.awt.BorderLayout;

import main.GuiLogic;
import main.YTVideo;
import xGui.XPanel;

public class PreviewPanel extends XPanel {

	private static final long serialVersionUID = 1L;

	public PreviewPanel() {
		super(new BorderLayout());
		GuiLogic.getInstance().registerPreview(this);
	}
	
	public void clear() {
		removeAll();
		revalidate();
		repaint();
	}
	
	public void displayYtVideo(YTVideo video) {
		removeAll();
		add(video, BorderLayout.CENTER);
		revalidate();
		repaint();
	}
}