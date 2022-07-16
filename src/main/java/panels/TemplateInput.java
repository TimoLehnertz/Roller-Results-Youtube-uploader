package panels;

import java.awt.FlowLayout;
import java.util.function.Consumer;

import javax.swing.JComboBox;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import getters.ConstantGetter;
import getters.FileHierarchyGetter;
import getters.JSONGetter;
import getters.VideoMetaGetter;
import main.VideoMeta;
import stringFilters.GenderFilter;
import stringFilters.StringFilter;
import stringFilters.WordsToCSV;
import stringFilters.YearFilter;
import xGui.XButton;
import xGui.XLabel;
import xGui.XPanel;
import xGui.XTextField;
import xThemes.XStyle;

public class TemplateInput extends XPanel {

	private static final long serialVersionUID = 1L;
	
	private JComboBox<String> inputSelect = new JComboBox<>(new String[] {"JSONGetter", "FileHierarchyGetter", "Constant"});
	private JComboBox<String> filterSelect = new JComboBox<>(new String[] {"None", "WordsToCSV", "YearFilter", "GenderFilter"});
	private VideoMetaGetter getter;
	
	private XPanel inputPanel = new XPanel();
	
	private XLabel indexLabel = new XLabel();
	
	private XButton upBtn = new XButton("up.jpg", 10, 10);
	private XButton downBtn = new XButton("down.png", 10, 10);
	private XTextField nameLabel = new XTextField();
	
	private XButton removeBtn = new XButton("X");
	
	public TemplateInput() {
		this("", "JSONGetter", "");
	}
	
	public TemplateInput(String name, String getter, String setup) {
		super(XStyle.LAYER1);
		setLayout(new FlowLayout(FlowLayout.LEFT));

		indexLabel = new XLabel();
		
		nameLabel.setText(name);

		inputSelect.addActionListener(e -> updateInput());
		filterSelect.setToolTipText("Filter that gets aplied to the text last");
		inputSelect.setToolTipText("Source of this getter");
		nameLabel.setToolTipText("Display name");
		
		nameLabel.setColumns(10);
		
		add(indexLabel);
		add(nameLabel);
		add(inputSelect);
		add(inputPanel);
		add(new XLabel("Filter:"));
		add(filterSelect);
		add(upBtn);
		add(downBtn);
		add(removeBtn);
		
		updateInput();
		
		setGetter(getter);
		setGetterSetup(setup);
	}

	public TemplateInput(XMLEventReader reader) {
		this();
		try {
			int depth = 0;
			int mode = 0;
			while(reader.hasNext()) {
				XMLEvent e = reader.nextEvent();
				int type = e.getEventType();
				// flow
				if(type == XMLStreamConstants.START_ELEMENT) {
					if(e.toString().contentEquals("<name>")) 			mode = 1;
					if(e.toString().contentEquals("<getter>")) 			mode = 2;
					if(e.toString().contentEquals("<setup>")) 			mode = 3;
					if(e.toString().contentEquals("<stringFilter>")) 	mode = 4;
					depth++;
				}
				if(type == XMLStreamConstants.END_ELEMENT) {
					mode = 0;
					depth--;
				}
				
				// setters
				if(type == XMLStreamConstants.CHARACTERS && depth == 1) {
					switch(mode) {
					case 1: setName(e.toString());
					case 2: setGetter(e.toString());
					case 3: setGetterSetup(e.toString());
					case 4: setStringFilter(e.toString());
					}
				}
				if(depth < 0) return;
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}

	public void setParent(TemplateInputPanel parent) {
		removeBtn.addActionListener(e -> parent.removeInput(this));
		upBtn.addActionListener(e -> parent.moveInput(this, false));
		downBtn.addActionListener(e -> parent.moveInput(this, true));
	}

	private void updateInput() {
		inputPanel.removeAll();
		if(((String) inputSelect.getSelectedItem()).contentEquals("JSONGetter")) {
			getter = new JSONGetter();
			inputPanel.add(getter);
		}
		if(((String) inputSelect.getSelectedItem()).contentEquals("Constant")) {
			getter = new ConstantGetter();
			inputPanel.add(getter);
		}
		if(((String) inputSelect.getSelectedItem()).contentEquals("FileHierarchyGetter")) {
			getter = new FileHierarchyGetter();
			inputPanel.add(getter);
		}
		revalidate();
		repaint();
	}
	
	void setIndex(int index) {
		indexLabel.setText(index + "");
	}
	
	public void setName(String name) {
		nameLabel.setText(name);
	}
	
	public void setGetter(String getter) {
		for (int i = 0; i < inputSelect.getItemCount(); i++) {
			if(inputSelect.getItemAt(i).contentEquals(getter)) {
				inputSelect.setSelectedIndex(i);
				return;
			}
		}
	}
	
	public void setGetterSetup(String setup) {
		getter.set(setup);
	}
	
	public void setStringFilter(String stringFilter) {
		for (int i = 0; i < filterSelect.getItemCount(); i++) {
			if(filterSelect.getItemAt(i).contentEquals(stringFilter)) {
				filterSelect.setSelectedIndex(i);
				return;
			}
		}
	}
	
	private StringFilter getStringFilter() {
		switch((String) filterSelect.getSelectedItem()) {
			case "WordsToCSV": return new WordsToCSV();
			case "YearFilter": return new YearFilter();
			case "GenderFilter": return new GenderFilter();
			default: return new WordsToCSV();
		}
	}
	
	public void getFrom(VideoMeta video, Consumer<String> callback) {
		getter.get(video, e -> {
			if(e == null) callback.accept(null);
			getStringFilter().filter(e, callback);
		});
	}

	public String strinify() {
		return "<input><name>" + nameLabel.getText() + "</name><getter>" + getter.getClass().getSimpleName() + "</getter><setup>" + getter.getSetup() + "</setup><stringFilter>" + filterSelect.getSelectedItem() + "</stringFilter></input>";
	}
}