package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.XMLEvent;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;

import panels.PreviewPanel;
import panels.TaskListPanel;
import panels.TemplateInput;
import panels.TemplateInputPanel;
import panels.YTUploadBuilder;
import uils.FileExtensionFilter;
import uils.Utils;

public class GuiLogic {

	private static final GuiLogic instance = new GuiLogic();
	
	private List<PreviewPanel> previews = new ArrayList<>();
	private YTUploadBuilder activeBuilder;
	private TemplateInputPanel activeTemplateInput;
	private TaskListPanel taskList;
	
	private File clientSecrets = new File("clientsSecrets/client_secret_SpeedSkating_Channel.json");
	private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private final Collection<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/youtube");
	private static final String APPLICATION_NAME = "Inlinespeedskating Channel";
	private YouTube youtubeService = null;
	private String loadedPreset;
	
	private File sourceFile;
	
	private GuiLogic() {
		super();
	}
	
	public void registerTemplateInput(TemplateInputPanel templateInput) {
		activeTemplateInput = templateInput;
	}

	public void registerPreview(PreviewPanel previewPanel) {
		previews.add(previewPanel);
	}
	
	public void registerUploadBuilder(YTUploadBuilder builder) {
		activeBuilder = builder;
	}
	
	public void previewVideo(YTVideo video) {
		for (PreviewPanel previewPanel : previews) {
			previewPanel.displayYtVideo(video);
		}
	}
	
	public void processVideoMeta(VideoMeta videoMeta, Consumer<Boolean> callback) {
		if(activeBuilder == null || activeTemplateInput == null) return;
		activeTemplateInput.getInputs(videoMeta, res -> {
			if(res == null) {
				System.out.println("Skipped video creation of " + videoMeta.videoFile.getName());
				if(callback != null) callback.accept(false);
				return;
			}
			YTVideo video = activeBuilder.createVideo(res, videoMeta);
			previewVideo(video);
			taskList.addTask(video.toTask());
			if(callback != null) callback.accept(true);
		});
	}
	
	public void setClientSecrets(File clientSecrets) {
		this.clientSecrets = clientSecrets;
	}
	
	public File getClientSecrets() {
		return clientSecrets;
	}
	
	public void registerTaskList(TaskListPanel taskList) {
		this.taskList = taskList;
	}
	
	public void clearAll() {
		activeBuilder.clearAll();
		activeTemplateInput.clearAll();
		for (PreviewPanel preview : previews) {
			preview.clear();
		}
	}
	
	public File getSourceFile() {
		return sourceFile;
	}
	
	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}
	
	public List<String> getPresets() {
		List<String> presets = new ArrayList<>();
		File f = new File("presets\\");
		for (File presetFile : f.listFiles(new FileExtensionFilter("preset"))) {
			presets.add(Utils.getFileNameNoExtension(presetFile.getName()));
		}
		return presets;
	}

	public void removePreset(String name) {
		File f = new File("presets\\" + name + ".preset");
		f.delete();
	}
	
	/**
	 * 	<Preset>
	 * 		<inputs>
	 * 			<input>
	 * 				<name>name</name>
	 * 				<getter>Getter</getter>
	 * 				<setup>setup</setup>
	 * 			</input>
	 * 		</inputs>
	 * 		<title>title</title>
	 * 		<description>description</description>
	 * 		<tags>tags</tags>
	 * </Preset>
	 */
	public void loadPreset(String name) {
		File f = new File("presets\\" + name + ".preset");
		try {
			FileInputStream fis = new FileInputStream(f);
			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			XMLEventReader reader = xmlInputFactory.createXMLEventReader(fis);
			while(reader.hasNext()) {
				XMLEvent e = reader.nextEvent();
				if(e.getEventType() != XMLStreamConstants.START_ELEMENT) continue;
				if(e.toString().contentEquals("<inputs>")) {
					
					while((e = reader.nextEvent()).toString().contentEquals("<input>")) {
						activeTemplateInput.addInput(new TemplateInput(reader));
					}
				} else if(reader.hasNext()){
					XMLEvent nextEvent = reader.nextEvent();
					activeBuilder.setProperty(e.toString(), nextEvent.toString());
				}
			}
			reader.close();
			fis.close();
			loadedPreset = name;
			for (PreviewPanel preview : previews) {
				preview.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
			loadedPreset = null;
		}
	}

	public void saveToCurrentPreset() {
		if(loadedPreset == null) return;
		savePreset(loadedPreset);
	}
	
	/**
	 * 	<Preset>
	 * 		<inputs>
	 * 			<input>
	 * 				<name>name</name>
	 * 				<getter>Getter</getter>
	 * 				<setup>setup</setup>
	 * 			</input>
	 * 		</inputs>
	 * 		<title>title</title>
	 * 		<description>description</description>
	 * 		<tags>tags</tags>
	 * </Preset>
	 */
	public void savePreset(String name) {
		if(name == null) return;
		removePreset(name);
		try {
			String path = "presets\\" + name + ".preset";
			File f = new File(path);
			new File("presets").mkdirs();
			f.createNewFile();
			FileWriter writer = new FileWriter(path);
			writer.write("<?xml version='1.0' encoding='UTF-8' standalone='no'?>\n");
			writer.write("<Preset>\n\t<inputs>");
			writer.write(activeTemplateInput.stringify());
			writer.write("</inputs>");
			writer.write(activeBuilder.stringify());
			writer.write("\n</Preset>");
			writer.close();
			System.out.println("Succsessfully saved to " + name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
     * Create an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public Credential authorize(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets.
//      InputStream in = ApiExample.class.getResourceAsStream(CLIENT_SECRETS);
        InputStream in = new FileInputStream(clientSecrets);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
    }
	
	public void authorizeYoutube() {
		try {
			youtubeService = getService();
		} catch (GeneralSecurityException | IOException e) {
			youtubeService = null;
			e.printStackTrace();
		}
	}
	
	public YouTube getYoutubeService() {
		if(youtubeService == null) authorizeYoutube();
		return youtubeService;
	}
	
	public static GuiLogic getInstance() {
		return instance;
	}
}