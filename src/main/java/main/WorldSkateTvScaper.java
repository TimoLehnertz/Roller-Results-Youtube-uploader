package main;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

public class WorldSkateTvScaper {

	public static void main(String[] args) {
		String res = get("http://videochannel.enetres.net/iduser/A217BCEBB2594BDF8FE2E65131DBF663/videochannel/2/section/channel/channel/24-speed.html?getcmsdata");
		
		JSONObject obj = new JSONObject(res);
		JSONArray chanels = (JSONArray) obj.get("subchannels");
		for (Object chanel : chanels) {
			JSONObject chanelObj = (JSONObject) chanel;
			System.out.println("scraping competition " + chanelObj.get("title"));
			JSONObject competition = new JSONObject(get("http://videochannel.enetres.net/iduser/A217BCEBB2594BDF8FE2E65131DBF663/videochannel/2/section/channel/channel/" + chanelObj.get("normalizedPath") + ".html?getcmsdata"));
			JSONArray subCompetitions = (JSONArray) competition.get("subchannels");
			if(subCompetitions.length() == 0) {
				JSONArray currentChanels = (JSONArray) competition.get("currentChannel");
				JSONObject currentChanel = currentChanels.getJSONObject(0);
				scrapeChanelVideos(currentChanel.getString("normalizedPath"), "speed/" + encodeFilename(chanelObj.getString("title")));
			} else {
				for (Object subComp : subCompetitions) {
					JSONObject subCompObj = (JSONObject) subComp;
//					System.out.println("\t" + subCompObj.get("description"));
					scrapeChanelVideos(subCompObj.get("normalizedPath").toString(), "speed/" + encodeFilename(chanelObj.getString("title")) + "/" + encodeFilename(subCompObj.getString("title")));
				}
			}
		}
	}
	
	public static void scrapeChanelVideos(String normalizedChanelPath, String dirPath) {
		System.out.println("\tscraping: " + normalizedChanelPath);
		new File(dirPath).mkdirs();
		JSONObject chanel = new JSONObject(get("http://videochannel.enetres.net/iduser/A217BCEBB2594BDF8FE2E65131DBF663/videochannel/2/section/channel/channel/" + normalizedChanelPath + ".html?getcmsdata"));
		JSONArray videos = chanel.getJSONArray("videos");
		for (Object object : videos) {
			JSONObject video = (JSONObject) object;
			String title = encodeFilename(video.getString("title"));
			
//			write info json
			if(!new File(dirPath + "/" + title + ".txt").exists()) {
				List<String> lines = Arrays.asList(video.toString(3));
				Path path = Paths.get(dirPath + "/" + title + ".txt");
				try {
					System.out.println(dirPath + "/" + title + ".txt");
					new File(dirPath + "/" + title + ".txt").createNewFile();
					Files.write(path, lines, StandardCharsets.UTF_8);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Skipping info.txt");
			}
			
//			Download video
			if(!new File(dirPath + "/" + title + ".mp4").exists()) {
				try (BufferedInputStream in = new BufferedInputStream(new URL(video.getString("downloadURL")).openStream()); FileOutputStream fileOutputStream = new FileOutputStream(dirPath + "/" + title + ".mp4")) {
					int size = getFileSize(video.getString("downloadURL"));
//					double sizeMB = Math.round(size / 1024.0/ 1024.0 * 100) / 100.0;
					int bytesDownloaded = 0;
					byte dataBuffer[] = new byte[1024 * 1024];
					int bytesRead;
					while ((bytesRead = in.read(dataBuffer, 0, 1024 * 1024)) != -1) {
//						double doneMB = Math.round(bytesDownloaded / 1024.0 / 1024.0 * 100) / 100.0;
						printProgress(0, size, bytesDownloaded);
						fileOutputStream.write(dataBuffer, 0, bytesRead);
						bytesDownloaded += bytesRead;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Skipping vid.mp4");
			}
		}
	}
	
	public static int getFileSize(String path) {
		try {
			URL url = new URL(path);
			HttpURLConnection conn;
			// open stream to get size of page
			conn = (HttpURLConnection)url.openConnection();
			  
			// set request method.
			conn.setRequestMethod("HEAD");
			  
			// get the input stream of process
			conn.getInputStream();
			  
			// store size of file
			int size = conn.getContentLength();
			conn.getInputStream().close();
			return size;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static void printProgress(long startTime, long total, long current) {
		current = Math.min(total, current);
//	    long eta = current == 0 ? 0 : 
//	        (total - current) * (startTime) / current;

//	    String etaHms = current == 0 ? "N/A" : 
//	            String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
//	                    TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
//	                    TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

	    StringBuilder string = new StringBuilder(140);   
	    int percent = (int) (current * 100 / total);
	    string
	        .append('\r')
	        .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
	        .append(String.format(" %d%% [", percent))
	        .append(String.join("", Collections.nCopies(percent, "=")))
	        .append('>')
	        .append(String.join("", Collections.nCopies(100 - percent, " ")))
	        .append(']')
//	        .append(String.join("", Collections.nCopies((int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
	        .append(String.format(" %fMB/%fMB", Math.round(current / 1024.0 / 1024.0 * 100) / 100.0, Math.round(total / 1024.0 / 1024.0 * 100) / 100.0));

	    System.out.print(string);
	}

	
	public static String encodeFilename(String s) {
		return s.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
	}

	
	public static String get(String urlString) {
//		String out = "";
		try {
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			InputStream is = conn.getInputStream();
			StringBuilder sb = new StringBuilder();
		    int cp;
		    while ((cp = is.read()) != -1) {
		      sb.append((char) cp);
		    }
		    return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String post(String urlString, Map<String, String> values) {
		try {
			String urlParameters  = "param1=a&param2=b&param3=c";
			for (Entry<String, String> val : values.entrySet()) {
				urlParameters += val.getKey() + "=" + val.getValue() + "&";
			}
			byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
			int    postDataLength = postData.length;
			URL    url            = new URL( urlString );
			HttpURLConnection conn= (HttpURLConnection) url.openConnection();           
			conn.setDoOutput( true );
			conn.setInstanceFollowRedirects( false );
			conn.setRequestMethod( "POST" );
			conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
			conn.setRequestProperty( "charset", "utf-8");
			conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
			conn.setUseCaches( false );
			DataOutputStream wr = new DataOutputStream( conn.getOutputStream());
			wr.write( postData );
			InputStream is = conn.getInputStream();
			StringBuilder sb = new StringBuilder();
		    int cp;
		    while ((cp = is.read()) != -1) {
		      sb.append((char) cp);
		    }
		    return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}