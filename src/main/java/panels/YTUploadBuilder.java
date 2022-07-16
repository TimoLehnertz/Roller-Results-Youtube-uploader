package panels;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComboBox;

import main.GuiLogic;
import main.VideoMeta;
import main.YTVideo;
import xGui.XCheckBox;
import xGui.XLabel;
import xGui.XScrollPanel;
import xGui.XTextArea;
import xGui.XTextField;

public class YTUploadBuilder extends XScrollPanel {
	
	private static final long serialVersionUID = 1L;

	private XTextField title = new XTextField();
	private XTextArea description = new XTextArea();
	private XTextArea tags = new XTextArea();
	private JComboBox<String> category = new JComboBox<>();
	XCheckBox allowEmbedding = new XCheckBox("Allow embedding");
	XCheckBox madeForChildren = new XCheckBox("Is made for children");
	JComboBox<String> visibility = new JComboBox<>(new String[]{"private", "public", "unlisted"});
	
	private static final Map<String, String> ytCategories = new HashMap<>();
	static {
		ytCategories.put("1", "Film & Animation");
		ytCategories.put("2", "Autos & Vehicles");
		ytCategories.put("10", "Music");
		ytCategories.put("15", "Pets & Animals");
		ytCategories.put("17", "Sports");
		ytCategories.put("19", "Travel & Events");
		ytCategories.put("20", "Gaming");
		ytCategories.put("22", "People & Blogs");
		ytCategories.put("23", "Comedy");
		ytCategories.put("24", "Entertainment");
		ytCategories.put("25", "News & Politics");
		ytCategories.put("26", "Howto & Style");
		ytCategories.put("27", "Education");
		ytCategories.put("28", "Science & Technology");
		ytCategories.put("29", "Nonprofits & Activism");
	}
	
	public YTUploadBuilder() {
		super();
		GuiLogic.getInstance().registerUploadBuilder(this);
		title.setToolTipText("");
		title.setText("");
		description.setText("");
		description.setToolTipText("");
		tags.setText("");
		
		content.add(new XLabel("Title"));
		content.add(title);
		content.add(new XLabel("Description"));
		content.add(description);
		content.add(new XLabel("Tags (Seperated by ,)"));
		content.add(tags);
		content.add(new XLabel("Visibility:"));
		content.add(visibility);
		content.add(new XLabel("Category"));
		content.add(category);
		content.add(allowEmbedding);
		content.add(madeForChildren);
		
		for (Entry<String, String> entry : ytCategories.entrySet()) {
			category.addItem(entry.getValue());
		}
		
		revalidate();
		repaint();
	}
	
	public void setProperty(String property, String value) {
		switch(property) {
		case "<title>": 		title.setText(value); break;
		case "<description>": 	description.setText(value); break;
		case "<tags>": 			tags.setText(value); break;
		case "<category>": 		category.setSelectedItem(ytCategories.get(value)); break;
		case "<allowEmbedding>":allowEmbedding.setSelected(value.contentEquals("true")); break;
		case "<madeForChildren>":madeForChildren.setSelected(value.contentEquals("true")); break;
		case "<visibility>":	visibility.setSelectedItem(value); break;
		}
	}
	
	public static final String renderText(String text, List<String> inputs) {
		MessageFormat mf = new MessageFormat(text);
		return mf.format(inputs.toArray());
	}

	public String getCategoryId() {
		for (Entry<String, String> entry : ytCategories.entrySet()) {
			if(entry.getValue().contentEquals(category.getSelectedItem().toString())) {
				return entry.getKey();
			}
		}
		return "22"; // dummy default value
	}
	
	public YTVideo createVideo(List<String> templateInputs, VideoMeta videoMeta) {
		YTVideo video = new YTVideo();
		video.setVideoFile(videoMeta.videoFile);
		video.setThumbnail(videoMeta.thumbFile);
		video.setTitle(renderText(title.getText(), templateInputs));
		video.setDescription(renderText(description.getText(), templateInputs));
		video.setTags(Arrays.asList(renderText(tags.getText(), templateInputs).split(",")));
		video.setCategory(getCategoryId(), category.getSelectedItem().toString());
		video.setEmbeddable(allowEmbedding.isSelected());
		video.setMadeForChildren(madeForChildren.isSelected());
		video.setPrivacyStatus(visibility.getSelectedItem().toString());
		return video;
	}
	
	public String stringify() {
		return "<title>" + title.getText() + "</title>"
				+ "<description>" + description.getText() + "</description>"
				+ "<tags>" + tags.getText() + "</tags>"
				+ "<category>" + getCategoryId() + "</category>"
				+ "<category>" + getCategoryId() + "</category>"
				+ "<madeForChildren>" + (madeForChildren.isSelected() ? "true" : "false")+ "</madeForChildren>"
				+ "<visibility>" + visibility.getSelectedItem() + "</visibility>"
				+ "<allowEmbedding>" + (allowEmbedding.isSelected() ? "true" : "false") + "</allowEmbedding>";
	}

	public void clearAll() {
		title.setText("");
		description.setText("");
		tags.setText("");
		category.setSelectedItem(ytCategories.get("22"));
		madeForChildren.setSelected(false);
		allowEmbedding.setSelected(false);
		visibility.setSelectedItem("private");
	}
}