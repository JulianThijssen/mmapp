package com.partition;

import java.io.File;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class AudioActivity extends Activity {
	private Button		playButton     = null;
	private Button      pauseButton    = null;
	private Button      stopButton     = null;
	private SeekBar     tempoBar       = null;
	
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

	protected void playMIDI() {
		if(reset){
			try {
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				File filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
				Uri myUri1 = Uri.parse(Uri.fromFile(filePath).getPath());
				mediaPlayer.setDataSource(getApplicationContext(), myUri1);
				
			} catch (Exception e) {
				e.printStackTrace();
			}		
			try {
				mediaPlayer.prepare();
			}  catch (Exception e) {
				e.printStackTrace();
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
