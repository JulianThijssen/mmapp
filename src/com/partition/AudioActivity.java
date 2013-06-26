package com.partition;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;

public class AudioActivity extends Activity {
	private Button		playButton     = null;
	private Button      pauseButton    = null;
	private Button      stopButton     = null;
	private SeekBar     tempoBar       = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		
		playButton      = (Button)findViewById(R.id.playButton);
		pauseButton     = (Button)findViewById(R.id.pauseButton);
		stopButton      = (Button)findViewById(R.id.stopButton);
	}
}
