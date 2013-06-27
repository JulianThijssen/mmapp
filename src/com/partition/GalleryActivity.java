package com.partition;

import java.io.File;
import java.io.IOException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class GalleryActivity extends Activity {
	private PhotoView   photoView      = null;
	private Button      cameraButton   = null;
	private Button      convertButton  = null;
	private SeekBar     scrollBar      = null;
	private MidiClient  midiClient     = null;
	private boolean     buttonsEnabled = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		
		photoView 		= (PhotoView)findViewById(R.id.photoView);
		cameraButton 	= (Button)findViewById(R.id.cameraButton);
		convertButton   = (Button)findViewById(R.id.convertButton);
		scrollBar       = (SeekBar)findViewById(R.id.scrollBar);
		
		midiClient = new MidiClient();
		
		photoView.setPath(getAlbumDir().getAbsolutePath());
		
		cameraButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!buttonsEnabled) {
					return;
				}
				buttonsEnabled = false;
				dispatchTakePictureIntent(1);
			}
		});
		
		convertButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!buttonsEnabled) {
					return;
				}
				buttonsEnabled = false;
				File photo = photoView.getCurrentPhoto();
				
				if (photo == null) {
					Log.e("Error", "ERR: Take a picture first.");
				} else {
					midiClient.setHost(getString(R.string.host));
					midiClient.setPath(getMusicPath().getAbsolutePath() + "/" + getString(R.string.album_name));
					midiClient.setMidiFileName(getString(R.string.midi_name));
					midiClient.uploadPhoto(photo);
				}
				dispatchAudioIntent(1);
			}
		});
		
		scrollBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				photoView.setPhotoIndex(progress);
				photoView.invalidate();
			}
			public void onStartTrackingTouch(SeekBar seekBar) {}
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});
	}
	
	protected void onResume() {
		super.onResume();
		if(photoView != null) {
			deleteUnusedFiles();
			photoView.reloadPhotos();
			scrollBar.setMax(photoView.getPhotoCount() - 1);
			scrollBar.setProgress(photoView.getPhotoCount() - 1);
			photoView.invalidate();
			buttonsEnabled = true;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gallery, menu);
		return true;
	}
	
	private void dispatchAudioIntent(int actionCode) {
		Intent audioIntent = new Intent(this, AudioActivity.class);
		startActivity(audioIntent);
	}

	private void dispatchTakePictureIntent(int actionCode) {
		photoView.setPath(getAlbumDir().getAbsolutePath());
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

	    try {
			File f = createImageFile();
			Log.i("Path", Uri.fromFile(f) + "");
		    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
	    startActivityForResult(takePictureIntent, actionCode);
	
	    Log.i("Activity", "Activity Started");
	}
	
	private File createImageFile() throws IOException {   
	    File image = File.createTempFile("IMG_", ".jpg", getAlbumDir());
	    
	    return image;
	}
	
	private void deleteUnusedFiles() {
		File filePath = getAlbumDir();
		File[] files = filePath.listFiles();
		for(int i = 0; i < files.length; i++) {
			//Delete the file if it is empty
			if(files[i].length() == 0) {
				files[i].delete();
			}
		}
	}
	
	private File getPhotoPath() {
		return Environment.getExternalStoragePublicDirectory(
			   Environment.DIRECTORY_PICTURES);
	}
	
	private File getMusicPath() {
		return Environment.getExternalStoragePublicDirectory(
			   Environment.DIRECTORY_MUSIC);
	}
	
	private File getAlbumDir() {
		File storageDir = null;
		
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			storageDir = new File(getPhotoPath(), getString(R.string.album_name));

			if(storageDir != null) {
				if(!storageDir.mkdirs()) {
					if(!storageDir.exists()) {
						Log.d("AlbumDir", "Failed to create storage directory");
						return null;
					}
				}
			}
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE");
		}
		photoView.setPath(storageDir.getAbsolutePath());
		
		return storageDir;
	}
}
