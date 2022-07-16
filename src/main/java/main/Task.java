package main;

import java.awt.FlowLayout;
import java.util.function.Consumer;

import panels.TaskListPanel;
import uils.Utils;
import xGui.XButton;
import xGui.XCheckBox;
import xGui.XLabel;
import xGui.XPanel;
import xThemes.XStyle;
import xThemes.XStyle.BackgroundType;

public class Task extends XPanel {
	
	private static final long serialVersionUID = 1L;
	
	private double status = 0;
	private XCheckBox checkbox;
	public TaskListPanel taskList = null;
	private XButton removeButton = new XButton("X");
	private Consumer<ProgressListener> startCallback; // callback to be called and returning a progressListener
	private XLabel indexLabel = new XLabel("0");
	private Consumer<String> previewCallback;
	private XButton previewBtn = new XButton("Preview", e -> preview());
	private XLabel statusLabel = new XLabel("");
	
	public Task(String name, Consumer<ProgressListener> startCallback, Consumer<String> previewCallback) {
		super();
		this.startCallback = startCallback;
		this.previewCallback = previewCallback;
		setStatus(0);
		checkbox = new XCheckBox(Utils.elipse(name, 25), true);
		checkbox.setToolTipText(name);
		add(indexLabel);
		add(statusLabel);
		add(checkbox);
		add(previewBtn);
		add(removeButton);
		removeButton.addActionListener(e -> {
			if(taskList != null) {
				taskList.removeTask(this);
			}
		});
		setLayout(new FlowLayout(FlowLayout.RIGHT));
	}
	
	private void preview() {
		previewCallback.accept("Moin");
	}
	
	public void start(Consumer<Boolean> oncomplete) {
		updateStyle(new XStyle(BackgroundType.highlight1b));
		startCallback.accept(status -> {
			setStatus(status);
			if(status >= 1) oncomplete.accept(true);
			if(status < 0) oncomplete.accept(false);
		});
	}
	
	/**
	 * changes color and percent accordingly
	 * @param status
	 */
	private void setStatus(double status) {
		System.out.println("status: " + status);
		this.status = status;
		if(status > 1) {
			updateStyle(new XStyle(BackgroundType.succsess));
			statusLabel.setText("");
		} else if(status < 0) {
			updateStyle(new XStyle(BackgroundType.failure));
			statusLabel.setText("Failed");
		} else if (status == 0){
			updateStyle(new XStyle(BackgroundType.layer1));
			statusLabel.setText("");
		} else { // progressing
			updateStyle(new XStyle(BackgroundType.highlight3b));
			statusLabel.setText(Math.round(status * 100) + "%");
		}
	}
	
	public boolean isDone() {
		return status > 0;
	}
	
	public boolean isChecked() {
		return checkbox.isSelected();
	}
	
	public void setSelected(boolean selected) {
		checkbox.setSelected(selected);
	}
	
	public void setIndex(int index) {
		indexLabel.setText(index + "");
	}
}