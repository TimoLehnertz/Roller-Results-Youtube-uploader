package getters;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.json.JSONObject;

import main.VideoMeta;
import xGui.XTextField;

public class JSONGetter extends VideoMetaGetter {

	private static final long serialVersionUID = 1L;

	private XTextField jsonPath = new XTextField();
	
	public JSONGetter() {
		super();
		add(jsonPath);
		jsonPath.setColumns(15);
		jsonPath.setText("title");
	}
	
	class AtomicJSONObject {
		public JSONObject json;
		AtomicJSONObject(JSONObject json) {
			super();
			this.json = json;
		}
	}
	
	/**
	 * Dirtiest method you'll find :)
	 */
	@Override
	public void get(VideoMeta meta, Consumer<String> callback) {
		AtomicJSONObject json = new AtomicJSONObject(meta.getJSON());
		String[] list = jsonPath.getText().split("\\.");
		String errorMessage = "Video: \"" + meta.metaFile.getName() + "\".\"" + jsonPath.getText() + "\" could not be found!";
		for (AtomicInteger i = new AtomicInteger(0); i.get() < list.length - 1; i.incrementAndGet()) {
			VideoMetaGetter.getOrAsk((retry) -> {
				if(retry) {
					i.set(0);
					json.json = meta.getJSON();
				}
				json.json = json.json.getJSONObject(list[i.get()]);
				return "Succsess123";
				}, errorMessage, e -> {
					if(!e.contentEquals("Succsess123"))
						callback.accept(e);
				}, false, meta.metaFile);
		}
		VideoMetaGetter.getOrAsk((retry) -> {
			if(retry) {
				AtomicJSONObject json1 = new AtomicJSONObject(meta.getJSON());
				for (AtomicInteger i = new AtomicInteger(0); i.get() < list.length - 1; i.incrementAndGet()) {
					VideoMetaGetter.getOrAsk((retry1) -> {
						if(retry1) {
							i.set(0);
							json1.json = meta.getJSON();
						}
						json1.json = json1.json.getJSONObject(list[i.get()]);
						return "Succsess123";
						}, errorMessage, e -> {
							if(!e.contentEquals("Succsess123"))
								callback.accept(e);
						}, false, meta.metaFile);
				}
				return json1.json.getString(list[list.length - 1]);
			}
			return json.json.getString(list[list.length - 1]);
		}, errorMessage, callback, false, meta.metaFile);
	}

	@Override
	public void set(String setup) {
		jsonPath.setText(setup);
	}

	@Override
	public String getSetup() {
		return jsonPath.getText();
	}
}