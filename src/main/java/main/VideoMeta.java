package main;

import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;

import uils.Utils;
import xGui.XButton;
import xGui.XCheckBox;
import xGui.XPanel;
import xThemes.XStyle;

public class VideoMeta extends XPanel {

	private static final long serialVersionUID = 1L;
	
	public File videoFile;
	public File metaFile;
	public File thumbFile;
	private XCheckBox use = new XCheckBox("", true);
	XButton videoFileBtn = new XButton();
	XButton metaFileBtn = new XButton();
	XButton thumbFileBtn = new XButton();
	XButton playBtn = new XButton(XButton.STYLE_FOREGROUND, "play.png", 12, 12);
	private int depth;
	
	public VideoMeta(File f, int depth) {
		super(new FlowLayout(FlowLayout.LEFT), XStyle.LAYER1);
		this.depth = depth;
		push(f);
		
		videoFileBtn.addActionListener(e -> openVideo());
		metaFileBtn.addActionListener(e -> openMeta());
		thumbFileBtn.addActionListener(e -> openThumb());
		playBtn.addActionListener(e -> GuiLogic.getInstance().processVideoMeta(this, null));
		
		playBtn.setToolTipText("Send video to preview");
		
		add(use);
		add(playBtn);
		add(videoFileBtn);
		add(metaFileBtn);
		add(thumbFileBtn);
	}
	
	private void openVideo() {
		if(videoFile == null) return;
		try {
			Runtime.getRuntime().exec("explorer.exe /select," + videoFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void openMeta() {
		if(metaFile == null) return;
		try {
			Runtime.getRuntime().exec("explorer.exe /select," + metaFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void openThumb() {
		if(thumbFile == null) return;
		try {
			Runtime.getRuntime().exec("explorer.exe /select," + thumbFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean fileFits(File f) {
		String fName = Utils.getFileName(f);
		if(videoFile != null && Utils.getFileName(videoFile).contentEquals(fName)) return true;
		if(metaFile != null && Utils.getFileName(metaFile).contentEquals(fName)) return true;
		if(thumbFile != null && Utils.getFileName(thumbFile).contentEquals(fName)) return true;
		return false;
	}

	public void push(File f) {
		if(Utils.isFileOneOf(f, "mp4", "MOV", "MPEG4", "MP4", "AVI", "WMV", "MPEGPS", "FLV")) {
			videoFile = f;
			videoFileBtn.setText(Utils.fileElipse(f.getName(), 8));
			videoFileBtn.setToolTipText(f.getName());
		} else if(Utils.isFileOneOf(f, "jpeg", "jpg", "png")) {
			thumbFile = f;
			thumbFileBtn.setText(Utils.fileElipse(f.getName(), 8));
			thumbFileBtn.setToolTipText(f.getName());
		} else if(Utils.isFileOneOf(f, "txt", "json")) {
			metaFile = f;
			metaFileBtn.setText(Utils.fileElipse(f.getName(), 8));
			metaFileBtn.setToolTipText(f.getName());			
		}
		if(isComplete()) {
			XStyle style = XStyle.SUCCSESS;
			style.backgroundFilter = XStyle.ColorFilter.dark;
			updateStyle(style);
		} else {
			XStyle style = XStyle.WARNING;
			style.backgroundFilter = XStyle.ColorFilter.dark;
			updateStyle(style);
		}
	}
	
	private String readMetaFile() {
		if(metaFile == null) throw new RuntimeException("Meta JSON file for video " + getName() + " not found");
		try {
			return new String(Files.readAllBytes(Paths.get(metaFile.getAbsolutePath())));
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public boolean isComplete() {
		return videoFile != null && metaFile != null && thumbFile != null;
	}
	
	public JSONObject getJSON() {
		return new JSONObject(readMetaFile());
	}
	
	private File getOneFile() {
		if(videoFile != null) return videoFile;
		if(metaFile != null) return metaFile;
		return null;
	}
	
	public String getName() {
		File f = getOneFile();
		if(f != null) return Utils.getFileName(f); else return "";
	}
	
	public String getPath() {
		File f = getOneFile();
		String path = f.getName();
		for (int i = 0; i < depth; i++) {
			f = f.getParentFile();
			path = f.getName() + "/" + path;
		}
		return path;
	}

	public boolean isUse() {
		return use.isSelected();
	}
}
