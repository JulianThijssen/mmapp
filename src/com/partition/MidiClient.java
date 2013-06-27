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
	private HttpClient 	 httpClient = null;
	private HttpPost 	 httpPost   = null;
	private String		 host       = null;
	private String       path       = null;
	private String		 midiName   = "default.midi";
	
	public void setHost(String host) {
		this.host = "http://" + host + "/upload.php";
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setMidiFileName(String midiName) {
		this.midiName = midiName;
	}
	
	public void uploadPhoto(File photo) {
		execute(photo);
	}
	
	public void init() {
		try {
			httpClient = new DefaultHttpClient();
			httpPost = new HttpPost(host);
		} catch(IllegalArgumentException e) {
			Log.d("HTTP", "Failed to initialize HTTP Post");
		}
	}
	protected File doInBackground(File... pictures) {
		//Initialize the http client
		init();
		
		MultipartEntity mpEntity = new MultipartEntity();
	    ContentBody cbFile = new FileBody(pictures[0], "image/jpeg");
	    mpEntity.addPart("userfile", cbFile);
	    
		httpPost.setEntity(mpEntity);
		
		HttpEntity responseEntity = null;
		try {
			HttpResponse httpResponse = httpClient.execute(httpPost);
			responseEntity = httpResponse.getEntity();
		} catch(Exception e) {
			Log.d("HTTP", "Failed to execute HTTP Post");
		}
		
		//Save the MIDI at the specified path
		try {
			String filePath = path + "/" + midiName;
			Log.d("PATH", "Path: " + filePath);
			File midiFile = new File(filePath);
			
			FileOutputStream os = new FileOutputStream(midiFile);
			BufferedHttpEntity buf = new BufferedHttpEntity(responseEntity);
			buf.writeTo(os);
			while (buf.isStreaming()) {
			   buf.writeTo(os);
			}
		} catch(IOException e) {
			Log.d("HTTP", "Failed to store MIDI at the specified path");
		}
			
		return null;
    }
}