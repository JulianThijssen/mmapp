package com.partition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import android.os.AsyncTask;
import android.util.Log;

public class MidiClient extends AsyncTask<File, Void, File> {
	private GalleryActivity gallery    = null;
	private HttpClient 	    httpClient = null;
	private HttpPost 	    httpPost   = null;
	private String		    host       = null;
	private String          path       = null;
	private String		    midiName   = "default.midi";
	private boolean         success    = false;
	
	public void setGallery(GalleryActivity gallery) {
		this.gallery = gallery;
	}
	
	/** Sets the host uri which to send the photo to */
	public void setHost(String host) {
		this.host = "http://" + host + "/upload.php";
	}
	
	/** Sets the path which to retrieve photos from */
	public void setPath(String path) {
		this.path = path;
	}
	
	/** Sets the name of the MIDI file where the result of the query
	 *  will be stored. */
	public void setMidiFileName(String midiName) {
		this.midiName = midiName;
	}
	
	/** Uploads the photo to the web service, which will convert it
	 *  and store the resulting MIDI file. */
	public void uploadPhoto(File photo) {
		execute(photo);
	}
	
	/** Initializes the HTTP client and HTTP post objects */
	public void init() {
		try {
			httpClient = new DefaultHttpClient();
			httpPost = new HttpPost(host);
		} catch(IllegalArgumentException e) {
			Log.d("HTTP", "Failed to initialize HTTP Post");
		}
	}
	
	/** This function executes asynchronously and performs all the tasks
	 *  necessary to send the photo to the web service and retrieve the midi
	 *  file and store it.
	 */
	protected File doInBackground(File... pictures) {
		File midiFile = null;

		//Initialize the http client
		init();
		
		MultipartEntity mpEntity = new MultipartEntity();
	    ContentBody cbFile = new FileBody(pictures[0], "image/jpeg");
	    mpEntity.addPart("userfile", cbFile);
	    
		httpPost.setEntity(mpEntity);
		
		HttpEntity responseEntity = null;
		try {
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 500) {
				success = false;
				return null;
			}
			
			responseEntity = httpResponse.getEntity();
			
		} catch(Exception e) {
			Log.d("HTTP", "Failed to execute HTTP Post");
			success = false;
			return null;
		}
		
		//Save the MIDI at the specified path
		try {
			String filePath = path + "/" + midiName;
			midiFile = new File(filePath);
			
			FileOutputStream os = new FileOutputStream(midiFile);
			BufferedHttpEntity buf = new BufferedHttpEntity(responseEntity);
			buf.writeTo(os);
			while (buf.isStreaming()) {
			   buf.writeTo(os);
			}
			Log.d("MIDI", "Successfully saved MIDI at: " + filePath);
			success = true;
		} catch(IOException e) {
			Log.d("HTTP", "Failed to store MIDI at the specified path");
		}
			
		return midiFile;
    }
	
	/** This will get executed once doInBackground() finishes and either
	 *  dispatches an audio intent on succesful conversion or hides the
	 *  progress bar and shows an error message on failure.
	 */
    protected void onPostExecute(File midi) {
    	if(success) {
    		gallery.dispatchAudioIntent(1);
    	} else {
    		gallery.hideProgress();
    		gallery.showStatus();
    	}
    }
}