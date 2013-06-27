package com.partition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
	private Button      stopButton     = null;
	private SeekBar     tempoBar       = null;
	
	MediaPlayer mediaPlayer = null;
	private boolean reset = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio);
		
		playButton      = (Button)findViewById(R.id.playButton);
		stopButton      = (Button)findViewById(R.id.stopButton);
		tempoBar        = (SeekBar)findViewById(R.id.tempoBar);
		
		mediaPlayer     = new MediaPlayer();

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
		
		tempoBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            	mediaPlayer.reset();
            	reset = true;
            	changeTempo(progress * 5.12f);
            }
            public void onStartTrackingTouch(SeekBar arg0) {}
            public void onStopTrackingTouch(SeekBar arg0) {}
		});
	}
	
	private File getAlbumDir() {
		File storageDir = null;
		
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			storageDir = new File(Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_MUSIC), getString(R.string.album_name));

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
				File filePath = new File(getAlbumDir() + "/" + getString(R.string.midi_name));
				Log.d("PATH", "Trying to play MIDI from: " + filePath);
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
		if(!mediaPlayer.isPlaying()) {
			mediaPlayer.start();
			reset = false;
		} else
			mediaPlayer.pause();
	}
	
	protected void stopMIDI() {
		mediaPlayer.reset();
		reset = true;
	}
	
	private void changeTempo(float tempo) {
    	byte[] buffer, bytes;
    	File file, someFile;
    	FileInputStream file_in;
    	FileOutputStream file_out;
    	ByteArrayOutputStream byte_out;
    	
    	try {
    		file = new File(getAlbumDir() + "/" + getString(R.string.midi_name));
    		file_in = new FileInputStream(file);
    		byte_out = new ByteArrayOutputStream();
    		buffer = new byte[1024];
    		try {
    			for (int i; (i = file_in.read(buffer)) != -1;)
    				byte_out.write(buffer, 0, i);
    		} catch (IOException e) {
	        	 e.printStackTrace();
    		}
    		bytes = byte_out.toByteArray();

    		bytes[12] = (byte) (tempo / 128);
    	    bytes[13] = (byte) (tempo % 128);
    	    
	        someFile = new File(getAlbumDir() + "/" + getString(R.string.midi_name));
	        file_out = new FileOutputStream(someFile);
	        file_out.write(bytes);
	        file_out.flush();
	        file_out.close();
    	} catch (Exception e) {
 			e.printStackTrace();
    	}		
	}
}
