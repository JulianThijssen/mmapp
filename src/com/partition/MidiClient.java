package com.partition;

import java.io.File;
import java.io.FileOutputStream;
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
import android.os.Environment;
import android.util.Log;

public class MidiClient {
	protected String serverUrl;
	
	public MidiClient() {

	}
	
	public void setServer(String serverIp) {
		this.serverUrl = "http://" + serverIp + "/upload.php";
	}
	
	public void uploadPicture(File picture) {
		UploadPictureTask uploadPictureTask = new UploadPictureTask();
		uploadPictureTask.setServerUrl(serverUrl);
		uploadPictureTask.execute(picture);
	}
}

class UploadPictureTask extends AsyncTask<File, Void, File> {

    private Exception exception;
    protected String serverUrl;
    
    protected File doInBackground(File... pictures) {
        try {
        	Log.i("Server", "URL:" +serverUrl);
    		HttpClient httpClient = new DefaultHttpClient();
    		HttpPost httpPost = new HttpPost(serverUrl);
    		
    		MultipartEntity mpEntity = new MultipartEntity();
    	    ContentBody cbFile = new FileBody(pictures[0], "image/jpeg");
    	    mpEntity.addPart("userfile", cbFile);
    	    
    		httpPost.setEntity(mpEntity);
    		
    		HttpResponse httpResponse = httpClient.execute(httpPost);
    		HttpEntity responseEntity = httpResponse.getEntity();
    		String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
    		File midiFile = new File(filePath + "/sheet.midi");
    	
    		FileOutputStream os = new FileOutputStream(midiFile);

    		BufferedHttpEntity buf = new BufferedHttpEntity(responseEntity);
    		buf.writeTo(os);
    		while (buf.isStreaming()) {
    		   buf.writeTo(os);
    		}
    		
    		return pictures[0];
        } catch (Exception e) {
            this.exception = e;
            e.printStackTrace();
            return null;
        }
    }
    
    public void setServerUrl(String serverUrl) {
    	this.serverUrl = serverUrl;
    }
}