package panels;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import main.GuiLogic;
import main.VideoMeta;
import xGui.XButton;
import xGui.XPanel;
import xGui.XScrollPane;
import xThemes.XStyle;

public class TemplateInputPanel extends XPanel {

	private static final long serialVersionUID = 1L;

	private List<TemplateInput> inputs = new ArrayList<>();
	
	private XPanel head = new XPanel();
	private XPanel tablePanel = new XPanel(new GridBagLayout());
	
	private XButton addBtn = new XButton("Add input");
	
	public TemplateInputPanel() {
		super(new BorderLayout());
		GuiLogic.getInstance().registerTemplateInput(this);
		addBtn.addActionListener(e -> addInput());
		
		head.add(addBtn);
		
		XScrollPane bodyScroll = new XScrollPane(tablePanel);
		
		add(head, BorderLayout.NORTH);
		add(bodyScroll, BorderLayout.CENTER);
		
		recalculateIndices();
		
	}

	List<String> inputStrings = new ArrayList<>();
	
	public void getInputs(VideoMeta video, Consumer<List<String>> callback) {
		inputStrings = new ArrayList<>();
		if(inputs.size() == 0) // Handle empty inputs
			callback.accept(inputStrings);
		else
			getInputs(video, callback, inputs.get(0)); // start recursive
	}
	
	public void getInputs(VideoMeta video, Consumer<List<String>> callback, TemplateInput current) {
		current.getFrom(video, result -> {
			if(result == null) {
				callback.accept(null);
				return;
			}
			inputStrings.add(result);
			if(current == inputs.get(inputs.size() - 1)) {
				callback.accept(inputStrings);
			} else {
				getInputs(video, callback, inputs.get(inputs.indexOf(current) + 1));
			}
		});
	}
	
	void removeInput(TemplateInput input) {
		inputs.remove(input);
		tablePanel.remove(input);
		recalculateIndices();
	}
	
	void moveInput(TemplateInput input, boolean up) {
		int oldI = inputs.indexOf(input);
		int newI = oldI + (up ? 1 : -1);
		if(up && oldI > inputs.size() - 2) return;
		if(!up && oldI <=  0) return;
		inputs.remove(input);
		inputs.add(newI, input);
		tablePanel.removeAll();
		for (TemplateInput templateInput : inputs) {
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.NORTH;
			gbc.gridy = tablePanel.getComponentCount();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1000;
			tablePanel.add(templateInput, gbc);
		}
		recalculateIndices();
	}
	
	public void removeAllInputs() {
		
	}
	
	public void addInput(TemplateInput input) {
		input.setParent(this);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridy = tablePanel.getComponentCount();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1000;
		tablePanel.add(input, gbc);
		inputs.add(input);
		recalculateIndices();
	}
	
	private void addInput() {
		addInput(new TemplateInput());
	}
	
	private void recalculateIndices() {
		for (int i = 0; i < inputs.size(); i++) {
			inputs.get(i).setIndex(i);
			inputs.get(i).updateStyle(i % 2 == 0 ? XStyle.LAYER1 : XStyle.LAYER2);
		}
		revalidate();
		repaint();
	}

	public String stringify() {
		String out = "";
		for (TemplateInput input : inputs) {
			out += input.strinify();
		}
		return out;
	}
	
	public void clearAll() {
		inputs.clear();
		tablePanel.removeAll();
	}
}