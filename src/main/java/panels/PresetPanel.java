package panels;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import main.GuiLogic;
import xGui.XBorderPanel;
import xGui.XButton;
import xGui.XFrame;
import xGui.XLabel;
import xGui.XPanel;
import xGui.XScrollPanel;
import xGui.XTextField;
import xUtils.SelfClosing;

public class PresetPanel extends XBorderPanel {

	private class CreateNewPanel extends XBorderPanel implements SelfClosing {
		
		private static final long serialVersionUID = 1L;
		private XTextField input = new XTextField();
		private List<Consumer<String>> callbacks = new ArrayList<>();
		
		public CreateNewPanel() {
			super();
			north.add(new XLabel("Create new preset from curent settings"));
			center.add(input);
			south.add(new XButton("Cancel", e -> cancel()));
			south.add(new XButton("Create", e -> create()));
			input.setColumns(20);
		}
		
		void cancel() {
			for (Consumer<String> callback : callbacks) {				
				callback.accept(null);
			}
		}
		
		void create() {
			if(input.getText().trim().length() == 0) return;
			for (Consumer<String> callback : callbacks) {				
				callback.accept(input.getText().trim());
			}
		}

		@Override
		public void addClosingListener(Consumer<String> c) {
			callbacks.add(c);
		}
	}
	
	private static final long serialVersionUID = 1152648210903757005L;

	private XScrollPanel body = new XScrollPanel();
	private GuiLogic guiLogic = GuiLogic.getInstance();
	
	public PresetPanel() {
		super();
		north.add(new XLabel("Presets"));
		north.add(new XButton("Create New", e -> createNew()));
		north.add(new XButton("Refresh", e -> reloadPresets()));
		center.add(body);
		reloadPresets();
	}
	
	private XPanel getPresetPanel(String name) {
		XPanel panel = new XPanel();
		panel.add(new XLabel(name));
		panel.add(new XButton("Load", e -> guiLogic.loadPreset(name)));
		panel.add(new XButton("Update", e -> guiLogic.savePreset(name)));
		panel.add(new XButton("Delete", e -> {
			guiLogic.removePreset(name);
			reloadPresets();
		}));
		return panel;
	}
	
	void reloadPresets() {
		List<String> presets = guiLogic.getPresets();
		body.content.removeAll();
		for (String preset : presets) {
			body.content.add(getPresetPanel(preset));
		}
		revalidate();
		repaint();
	}
	
	void createNew() {
		CreateNewPanel p = new CreateNewPanel();
		p.addClosingListener(name -> guiLogic.savePreset(name));
		XFrame.popup(p, "Create new Preset", 300, 150);
		reloadPresets();
	}
}