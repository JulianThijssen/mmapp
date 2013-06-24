package com.partition;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class GalleryActivity extends Activity {
	private PhotoView   photoView      = null;
	private Button      cameraButton   = null;
	private Button      playButton     = null;
	private SeekBar     scrollBar      = null;
	private SeekBar     rotateSlider   = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		
		photoView 		= (PhotoView)findViewById(R.id.photoView);
		cameraButton 	= (Button)findViewById(R.id.cameraButton);
		playButton 		= (Button)findViewById(R.id.playButton);
		scrollBar       = (SeekBar)findViewById(R.id.scrollBar);
		
		cameraButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dispatchTakePictureIntent(1);
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
		Log.i("DO ETT", "DO EET");
		if(photoView != null) {
			photoView.reloadPhotos();
			scrollBar.setMax(photoView.getPhotoCount());
			scrollBar.setProgress(photoView.getPhotoCount() - 1);
			photoView.invalidate();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gallery, menu);
		return true;
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
	    //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    //String imageFileName = "pif_";
	    
	    File image = File.createTempFile("IMG_", ".jpg", getAlbumDir());
	    
	    return image;
	}
	
	private String getAlbumName() {
		return getString(R.string.album_name);
	}
	
	private File getAlbumDir() {
		File storageDir = null;
		
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getAlbumName());

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