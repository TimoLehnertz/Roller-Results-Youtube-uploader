package getters;

import java.util.function.Consumer;

import main.GuiLogic;
import main.VideoMeta;
import xGui.XLabel;

public class FileHierarchyGetter extends VideoMetaGetter {

	private static final long serialVersionUID = 1L;
	
	public FileHierarchyGetter() {
		super();
		add(new XLabel("relative from the origin"));
	}
	
	@Override
	public void get(VideoMeta meta, Consumer<String> callback) {
		String relative = GuiLogic.getInstance().getSourceFile().toURI().relativize(meta.videoFile.toURI()).getPath();
		callback.accept(relative);
	}

	@Override
	public void set(String setup) {

	}

	@Override
	public String getSetup() {
		return null;
	}

}
