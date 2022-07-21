package main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ThumbnailSetResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoProcessingDetails;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;

import xGui.XBorderPanel;
import xGui.XLabel;
import xGui.XScrollPanel;
import xGui.XTextArea;

public class YTVideo extends XBorderPanel {

	private static final long serialVersionUID = 1L;

	// Data
	private String title;
	private String description;
	private String privacyStatus; // private, public, unlisted
	private String categoryId; // list of cetegories available at https://techpostplus.com/youtube-video-categories-list-faqs-and-solutions/
	private List<String> tags = new ArrayList<>();
	private boolean embeddable;
	private boolean madeForChildren;
	private File videoFile;
	private File thumbnailFile;
	
	private GuiLogic guiLogic = GuiLogic.getInstance();
	
	private List<ProgressListener> progressListeners = new ArrayList<>(); // calls periodically with percentages 0.5 = 50%, >= 1 = done, < 0 = failed
	
	// GUI
	private XScrollPanel body = new XScrollPanel();
	
	private XTextArea titleArea = new XTextArea(false);
	private XTextArea descriptionArea = new XTextArea(false);
	private XTextArea tagsArea = new XTextArea(false);
	private XTextArea categoryArea = new XTextArea(false);
	private XTextArea allowEmbedding = new XTextArea(false);
	private XTextArea restrictions = new XTextArea(false);
	private XTextArea privacyStatusArea = new XTextArea(false);
	
	public YTVideo() {
		super();
		body.content.add(titleArea);
		body.content.add(new XLabel("Description:"));
		body.content.add(descriptionArea);
		body.content.add(new XLabel("Tags:"));
		body.content.add(tagsArea);
		body.content.add(new XLabel("Category:"));
		body.content.add(categoryArea);
		body.content.add(restrictions);
		body.content.add(new XLabel("Visibility:"));
		body.content.add(privacyStatusArea);
		
		center.add(body);
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public File getThumbnail() {
		return thumbnailFile;
	}
	
	public void setTitle(String title) {
		this.title = title;
		titleArea.setText(title);
	}
	
	public void setDescription(String description) {
		this.description = description;
		descriptionArea.setText(description);
	}
	
	public void setThumbnail(File thumbnailFile) {
		this.thumbnailFile = thumbnailFile;
	}
	
	public String getCategoryId() {
		return categoryId;
	}

	public void setCategory(String categoryId, String categoryName) {
		this.categoryId = categoryId;
		categoryArea.setText(categoryName);
	}
	
	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
		tagsArea.setText("");
		for (String tag : tags) {
			tagsArea.setText(tagsArea.getText() + "\n" + tag);
		}
	}

	public boolean isEmbeddable() {
		return embeddable;
	}

	public void setEmbeddable(boolean embeddable) {
		this.embeddable = embeddable;
		allowEmbedding.setText("Embedding " + (embeddable ? "allowed" : "deidenied"));
	}
	
	public boolean isMadeForChildren() {
		return madeForChildren;
	}

	public void setMadeForChildren(boolean madeForChildren) {
		this.madeForChildren = madeForChildren;
		if(madeForChildren) {			
			restrictions.setText("Restrictions: Made for children");
		} else {			
			restrictions.setText("Restrictions: None");
		}
	}
	
	public void setPrivacyStatus(String privacyStatus) {
		this.privacyStatus = privacyStatus;
		privacyStatusArea.setText(privacyStatus);
	}
	
	public File getVideoFile() {
		return videoFile;
	}

	public void setVideoFile(File videoFile) {
		this.videoFile = videoFile;
	}
	
	public void addProgressListener(ProgressListener progressListener) {
		progressListeners.add(progressListener);
	}

	public Task toTask() {
		return new Task(title, (progressListener) -> {
			try {
				Video video = upload();
				if(video.getStatus().getFailureReason() != null) { // error
					System.out.println(video.getStatus().getFailureReason());
					progressListener.progressChanged(-1);
				} else { // succsess
					addProgressListener(progressListener);
				}
			} catch (IOException e) {
				e.printStackTrace();
				progressListener.progressChanged(-1);
			}
		}, e -> guiLogic.previewVideo(this));
	}
	
	/**
	 * 10000 Youtube quotas per day
	 * each insert costs 1600
	 * each thumbnail set costs 50
	 * 
	 * 10000 / 1650 = 6 Videos per day
	 */
	public Video upload() throws IOException {
//		Upload video
		Video video = new Video();
        
        // Add the snippet object property to the Video object.
        VideoSnippet snippet = new VideoSnippet();
        snippet.setCategoryId(categoryId);
        snippet.setDescription(description);
        snippet.setTitle(title);
        snippet.setTags(tags);
        video.setSnippet(snippet);
        
        // Add the status object property to the Video object.
        VideoStatus status = new VideoStatus();
        status.setPrivacyStatus(privacyStatus);
        status.setEmbeddable(embeddable);
        status.set("selfDeclaredMadeForKids", isMadeForChildren());
        video.setStatus(status);
//        VideoProcessingDetails p = new VideoProcessingDetails();
//        video.setProcessingDetails(p);

        // The maximum file size for this operation is 274877906944.
        InputStreamContent mediaContent = new InputStreamContent("application/octet-stream", new BufferedInputStream(new FileInputStream(videoFile)));
        mediaContent.setLength(videoFile.length());

        // Define and execute the API request
        YouTube youtube = guiLogic.getYoutubeService();
        if(youtube == null) {
        	throw new NullPointerException("Youtube could not be authorized");
        }
        YouTube.Videos.Insert request = youtube.videos().insert("snippet,status", video, mediaContent);
        Video vidResponse = request.execute();
        System.out.println(vidResponse);
        
//      Upload thumbnail
        if(thumbnailFile != null) {        	
        	InputStreamContent thumbnailContent = new InputStreamContent("application/octet-stream", new BufferedInputStream(new FileInputStream(thumbnailFile)));
        	thumbnailContent.setLength(thumbnailFile.length());
        	YouTube.Thumbnails.Set thumbRequest = youtube.thumbnails().set(vidResponse.getId(), thumbnailContent);
        	ThumbnailSetResponse thumbResponse = thumbRequest.execute();
        	System.out.println(thumbResponse);
        }
        
        Timer t = new Timer(1000, e -> { // status checker
//        	System.out.println(vidResponse.getStatus().getUploadStatus());
//        	vidResponse
//        	System.out.println(p);
        	
//        	System.out.println(vidResponse);
        });
        t.start();
        
        return vidResponse;
	}
}