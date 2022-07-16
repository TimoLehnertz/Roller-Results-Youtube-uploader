package main;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import panels.PresetPanel;
import panels.PreviewPanel;
import panels.SettingsPanel;
import panels.SourceSelectorPanel;
import panels.TaskListPanel;
import panels.TemplateInputPanel;
import panels.YTUploadBuilder;
import xGui.PresetStep;
import xGui.SplitGetter;
import xGui.XFrame;
import xGui.XMenuBar;
import xGui.XPanel;
import xGui.XSystemConsole;
import xPresets.XIdePreset;

public class YTBatchUploader extends XIdePreset {
	
	private static final long serialVersionUID = 1L;

	private GuiLogic guiLogic = GuiLogic.getInstance();
	
	public static void main(String[] args) {
		new YTBatchUploader();
	}
	
	public YTBatchUploader() {
		super("Youtube Batch Uploader", "img/youtube.png", "1.0", false);
		revalidate();
		repaint();
		setVisible(true);
		guiLogic.loadPreset("Default");
	}

	@Override
	public List<SplitGetter> getSplitGetter() {
		return Arrays.asList(
				new SplitGetter("Task list", KeyEvent.VK_T, null, () -> new TaskListPanel(), false),
				new SplitGetter("Upload builder", KeyEvent.VK_B, null, () -> new YTUploadBuilder(), false),
				new SplitGetter("Template input", KeyEvent.VK_B, null, () -> new TemplateInputPanel(), false),
				new SplitGetter("Preview", KeyEvent.VK_B, null, () -> new PreviewPanel(), true),
				new SplitGetter("Console", KeyEvent.VK_B, null, () -> new XSystemConsole(), true),
				new SplitGetter("Source", KeyEvent.VK_E, null, () -> new SourceSelectorPanel(), false));
	}
	
	private SettingsPanel settings = new SettingsPanel();
	private PresetPanel presetPanel = new PresetPanel();

	@Override
	public void initHeaderLeftMenuBar(XMenuBar menubar) {
		JMenu file = new JMenu("File");
		JMenuItem openSettings = new JMenuItem("Settings");
		JMenuItem presets = new JMenuItem("Presets");
		JMenuItem savePreset = new JMenuItem("Save to current Preset");
		JMenuItem newItem = new JMenuItem("new");
		file.add(openSettings);
		file.add(presets);
		file.add(savePreset);
		file.add(newItem);
		savePreset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		presets.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
		newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		openSettings.addActionListener(e -> XFrame.popup(settings, "Settings", 700, 500, true));
		savePreset.addActionListener(e -> guiLogic.saveToCurrentPreset());
		presets.addActionListener(e -> XFrame.popup(presetPanel, "Presets", 700, 500, true));
		newItem.addActionListener(e -> guiLogic.clearAll());
		menubar.add(file);
	}
	
	@Override
	public void initHeaderRightContent(XPanel panel) {
		
	}

	@Override
	public PresetStep getPresetSteps() {
		return new PresetStep("", 0.0, false, new PresetStep("", 0.7, true, new PresetStep("", 0.7, true, new PresetStep("Source", .2, true), new PresetStep("", .2, true, new PresetStep("", .2, false), new PresetStep("Upload builder", .3, false, new PresetStep("Template input", .3, false), new PresetStep("Preview", .5, true)))), new PresetStep("Task list", 0.85, true)), new PresetStep("Console", 0.9, false));
	}
}