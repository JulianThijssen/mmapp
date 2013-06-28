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
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class GalleryActivity extends Activity {
	private PhotoView   photoView      = null;
	private Button      cameraButton   = null;
	private Button      convertButton  = null;
	private SeekBar     scrollBar      = null;
	public  TextView    status         = null;
	public  ProgressBar progressBar    = null;
	private MidiClient  midiClient     = null;
	private boolean     buttonsEnabled = true;

	/* This function gets called when an activity starts. (non-Javadoc)
	 * @see android.app.Activity#onCreate()
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		
		photoView 		= (PhotoView)findViewById(R.id.photoView);
		cameraButton 	= (Button)findViewById(R.id.cameraButton);
		convertButton   = (Button)findViewById(R.id.convertButton);
		scrollBar       = (SeekBar)findViewById(R.id.scrollBar);
		status          = (TextView)findViewById(R.id.status);
		progressBar     = (ProgressBar)findViewById(R.id.progressBar);
		
		photoView.setPath(getAlbumDir().getAbsolutePath());
		
		//Hide the views we do not need yet
		hideStatus();
		hideProgress();
		
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
				showProgress();
				File photo = photoView.getCurrentPhoto();
				
				if (photo == null) {
					Log.e("Error", "ERR: Take a picture first.");
				} else {
					midiClient = new MidiClient();
					midiClient.setGallery(GalleryActivity.this);
					midiClient.setHost(getString(R.string.host));
					midiClient.setPath(getMusicPath().getAbsolutePath() + "/" + getString(R.string.album_name));
					midiClient.setMidiFileName(getString(R.string.midi_name));
					midiClient.uploadPhoto(photo);
				}
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
	
	/* This function gets called when an activity starts, after onStart,
	 * or after another activity returns here. (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	protected void onResume() {
		super.onResume();
		hideProgress();
		hideStatus();
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
	
	protected void dispatchAudioIntent(int actionCode) {
		Intent audioIntent = new Intent(this, AudioActivity.class);
		startActivity(audioIntent);
	}

	/* Dispatch an intent to start the camera activity
	 * 
	 * @param   actionCode   Any integer to clarify your intent
	 */
	private void dispatchTakePictureIntent(int actionCode) {
		photoView.setPath(getAlbumDir().getAbsolutePath());
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

	    try {
			File f = createImageFile();
		    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
	    startActivityForResult(takePictureIntent, actionCode);
	
	    Log.i("Activity", "Activity Started");
	}
	
	/* Create a new image file to store the result of the camera activity in
	 * 
	 * @return    Returns a file descriptor to the new image file
	 */
	private File createImageFile() throws IOException {   
	    File image = File.createTempFile("IMG_", ".jpg", getAlbumDir());
	    
	    return image;
	}
	
	/** Deletes any corrupt or incomplete files from the Pictures folder */
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
	
	/** Hides the status bar from the gallery view */
	public void hideStatus() {
		status.setVisibility(View.INVISIBLE);
	}
	
	/** Shows the status bar in the gallery view */
	public void showStatus() {
		status.setVisibility(View.VISIBLE);
	}
	
	/** Hides the progress bar, call when the application is not busy */
	public void hideProgress() {
		progressBar.setVisibility(View.INVISIBLE);
		buttonsEnabled = true;
	}
	
	/** Shows the progress bar, call when the application is busy */
	public void showProgress() {
		progressBar.setVisibility(View.VISIBLE);
		buttonsEnabled = false;
	}
	
	/** Get the public path on any device to the Pictures folder */
	private File getPhotoPath() {
		return Environment.getExternalStoragePublicDirectory(
			   Environment.DIRECTORY_PICTURES);
	}
	
	/** Get the public path on any device to the Music folder */
	private File getMusicPath() {
		return Environment.getExternalStoragePublicDirectory(
			   Environment.DIRECTORY_MUSIC);
	}
	
	/** Get a directory reference to the directory called after our album name
	 *  in the public path to the Pictures folder.
	 *  
	 * @return    Returns a file descriptor of the directory
	 */
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
