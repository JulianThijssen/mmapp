package com.partition;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class AudioActivity extends Activity {
	private Button		playButton     = null;
	private Button      pauseButton    = null;
	private Button      stopButton     = null;
	private SeekBar     tempoBar       = null;
	
	private String path = null;
	MediaPlayer mediaPlayer = null;
	private boolean reset = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio);
		
		playButton      = (Button)findViewById(R.id.playButton);
		pauseButton     = (Button)findViewById(R.id.pauseButton);
		stopButton      = (Button)findViewById(R.id.stopButton);
		mediaPlayer     = new MediaPlayer();
		
		path = getAlbumDir().getAbsolutePath();
		
		playButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				playMIDI();
			}
		});
		
		stopButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				stopMIDI();
			}
		});
		
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
            	mediaPlayer.reset();
            	reset = true;
            }
        });
	}
	
	private String getAlbumName() {
		return getString(R.string.album_name);
	}
	
	private File getAlbumDir() {
		File storageDir = null;
		
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), getAlbumName());

			if(storageDir != null) {
				if(!storageDir.mkdirs()) {
					if(!storageDir.exists()) {
						Log.d("AlbumDir", "Failed to create music directory");
						return null;
					}
				}
			}
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE");
		}
		
		return storageDir;
	}

	protected void playMIDI() {
		if(reset){
			try {
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				File filePath = new File(path + "/" + getString(R.string.default_music_name));
				Uri uri = Uri.fromFile(filePath);
				mediaPlayer.setDataSource(getApplicationContext(), uri);
			} catch (Exception e) {
				Log.d("MIDI", "Failed to set data source");
			}		
			try {
				mediaPlayer.prepare();
			} catch (IllegalStateException e) {
				Log.d("MIDI", "Failed to prepare media player, Illegal State Exception");
			} catch (IOException e) {
				Log.d("MIDI", "Failed to prepare media player, IO Exception");
			}
		}
		if(!mediaPlayer.isPlaying()){
			mediaPlayer.start();
			reset = false;
		} else
			mediaPlayer.pause();
	}
	
	protected void stopMIDI() {
		mediaPlayer.reset();
		reset = true;
	}
}
