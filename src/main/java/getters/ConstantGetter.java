package getters;

import java.util.function.Consumer;

import main.VideoMeta;
import xGui.XTextField;

public class ConstantGetter extends VideoMetaGetter {

	private static final long serialVersionUID = 1L;

	private XTextField value = new XTextField();
	
	public ConstantGetter() {
		super();
		add(value);
		value.setColumns(15);
		value.setToolTipText("Constant value");
		value.setText("");
	}
	
	@Override
	public void get(VideoMeta meta, Consumer<String> callback) {
		callback.accept(value.getText());
	}
	
	@Override
	public void set(String setup) {
		value.setText(setup);
	}
	
	@Override
	public String getSetup() {
		return value.getText();
	}
}