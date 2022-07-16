package panels;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import main.GuiLogic;
import main.VideoMeta;
import xGui.XAccordion;
import xGui.XButton;
import xGui.XCheckBox;
import xGui.XLabel;
import xGui.XPanel;
import xGui.XScrollPane;
import xThemes.XStyle;

public class SourceSelectorPanel extends XPanel {

	private static final long serialVersionUID = 1L;
	private XPanel content = new XPanel(new GridBagLayout());

	private XCheckBox recursive = new XCheckBox("Recursive", true);
	private XLabel currentSource = new XLabel();
	private File source = new File(System.getProperty("user.dir") + "/speed");
	private List<VideoMeta> videoMetas = new ArrayList<>();
	private GuiLogic guiLogic = GuiLogic.getInstance();
	
	public SourceSelectorPanel() {
		super(new BorderLayout());
		guiLogic.setSourceFile(source);
		XPanel header = new XPanel(XStyle.LAYER1);
		XScrollPane leftScroll = new XScrollPane(content);
		add(header, BorderLayout.NORTH);
		add(leftScroll, BorderLayout.CENTER);
		XButton srcButton = new XButton("Source");
		XButton refreshBtn= new XButton("Refresh");
		refreshBtn.addActionListener(e -> updateAvailableVids(source, content, 0));
		updateAvailableVids(source, content, 0);
		srcButton.addActionListener(e -> selectSrcFolder());
		header.add(srcButton);
		header.add(recursive);
		header.add(currentSource);
		header.add(new XButton("Build all", e -> buildAll()));
	}
	
	private void buildAll() {
		buildRecusrive(0);
	}
	
	private void buildRecusrive(int i) {
		if(i >= videoMetas.size()) return;
		if(!videoMetas.get(i).isUse()) buildRecusrive(i + 1);
		guiLogic.processVideoMeta(videoMetas.get(i), succsess -> {
			if(succsess) buildRecusrive(i + 1);
		});
	}
	
	private void selectSrcFolder() {
		JFileChooser fileChooser = new JFileChooser(source);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fileChooser.showOpenDialog(this);
		if(returnVal == 0) {
			source = fileChooser.getSelectedFile();
			currentSource.setText("Source: " + source.getName());
			currentSource.setToolTipText(source.getAbsolutePath());
			updateAvailableVids(source, content, 0);
		}
	}
	
	private void updateAvailableVids(File dir, XPanel parentPanel, int recursion) {
		if(dir == null) return;
		if(recursion == 0) {
			videoMetas = new ArrayList<>();
			content.removeAll();
		}
		for (File f : dir.listFiles()) {
			if(f.isDirectory() && recursive.isSelected()) {
				XPanel folderBody = new XPanel(new GridBagLayout(), XStyle.BACKGROUND);
				GridBagConstraints gbc = new GridBagConstraints();
				XAccordion acc = new XAccordion(folderBody, f.getName(), false);
				gbc.gridy = parentPanel.getComponentCount();
				gbc.gridx = 0;
				gbc.insets = new Insets(1,0,0,0);
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.weightx = 1;
				parentPanel.add(acc, gbc);
				updateAvailableVids(f, folderBody, recursion + 1);
			} else {
				pushFileToVideoMetas(f, parentPanel, recursion);
			}
		}
	}
	
	private void pushFileToVideoMetas(File f, XPanel parentPanel, int recusrion) {
//		Check existing
		for (VideoMeta videoMeta : videoMetas) {
			if(videoMeta.fileFits(f)) {
				videoMeta.push(f);
				return;
			}
		}
//		create new
		VideoMeta meta = new VideoMeta(f, recusrion);
		videoMetas.add(meta);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = parentPanel.getComponentCount();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		parentPanel.add(meta, gbc);
	}
}