package getters;

import java.io.File;
import java.util.function.Consumer;

import main.BooleanToT;
import main.VideoMeta;
import xGui.XAskPanel;
import xGui.XFrame;
import xGui.XPanel;

public abstract class VideoMetaGetter extends XPanel {

	private static final long serialVersionUID = 1L;

	public abstract void get(VideoMeta meta, Consumer<String> callback);
	
	protected static void getOrAsk(BooleanToT<String> supplier, String name, Consumer<String> callback, boolean showCause, File pointerFile) {
		getOrAsk(supplier, name, callback, showCause, pointerFile, false);
	}
	
	public static void getOrAsk(BooleanToT<String> supplier, String name, Consumer<String> callback, boolean showCause, File pointerFile, boolean retry) {
		try {
			callback.accept(supplier.get(retry));
		} catch (Exception e) {
			XAskPanel popup = new XAskPanel(name + (showCause ? "   (" + e.getMessage() + ")" : ""), pointerFile);
			popup.addClosingListener(value -> {
				if(value == null) {
					callback.accept(null); // cancel
					return;
				}
				if(value.contentEquals(XAskPanel.RETRY))
					getOrAsk(supplier, name, callback, showCause, pointerFile, true); // retry
				else
					callback.accept(value); // okay
			});
			XFrame.popup(popup, name, 500, 160, true);
		}
	}
	
	public abstract void set(String setup);
	
	public abstract String getSetup();
}