package nl.uva.multimedia.camera;
/* 
 * Framework for camera processing and visualisation
 *
 * For the Multimedia course in the BSc Computer Science 
 * at the University of Amsterdam 
 *
 * I.M.J. Kamps, S.J.R. van Schaik, R. de Vries (2013)
 */

/* XXX Yes, you should change stuff here */

/* 
 * This file contains the main activity and all plumbing setup between
 * the different instances
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent; 
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import nl.uva.multimedia.camera.CameraCapture;
import nl.uva.multimedia.camera.CameraView;
import nl.uva.multimedia.camera.CanvasView;
import nl.uva.multimedia.camera.MySlider;
import nl.uva.multimedia.camera.MyButton;

/* Main activity */
public class Application extends Activity {
	CameraCapture   m_camera_capture   = new CameraCapture();
	CanvasView      m_canvas_view      = null;
	MySlider        m_slider           = null;
	CameraView      m_camera_view      = null;
	MyButton        m_button           = null;

	/* Change this to enable the image chooser */
	final private boolean SHOW_SELECT_IMAGE = false;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* Hide the window caption if we have a menu key (else those options
		 * appaer there and attempt to use fullscreen. 
		 */
		if (ViewConfiguration.get(this).hasPermanentMenuKey()) {
			getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		}
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.main);
		
		/* Grab the views and widgets from the lay-out. */
		m_camera_view      = (CameraView)findViewById(R.id.cameraView);
		m_canvas_view      = (CanvasView)findViewById(R.id.canvasView);
		m_slider           = (MySlider)findViewById(R.id.slider);
		m_button           = (MyButton)findViewById(R.id.button);
		
		/* Do some basic plumbing */
		m_camera_capture.setCanvasView(m_canvas_view);
		/* Can also be BEST_FIT, but BEST might be larger then
		 * the actual size of the preview canvas, experiment
		 * with this. Even with LARGEST_FIT it can fallback on
		 * BEST_FIT if no image is small enough.
		 */
		m_camera_view.setSizeType(CameraView.SizeType.LARGEST_FIT);
		m_camera_view.setPreviewCallback(m_camera_capture);
	}
	
	@Override protected void onResume() {
		super.onResume();

		Configuration config = getResources().getConfiguration();
		
		m_camera_view.acquireCamera();
	}
	
	@Override protected void onPause() {
		super.onPause();
		
		m_camera_view.releaseCamera();
	}
	
	@Override public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
	
		/* Need to reacquire camera on these */
		m_camera_view.releaseCamera();
		m_camera_view.acquireCamera();
	}

	/* Menu options handling, you probably don't need to change this */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater;
		MenuItem     camera_switch;
		MenuItem     select_image;
		
		inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		camera_switch = menu.findItem(R.id.camera_switch);
		select_image = menu.findItem(R.id.select_image);

		if (Camera.getNumberOfCameras() == 1) {
			camera_switch.setEnabled(false);
		}
		
		select_image.setVisible(SHOW_SELECT_IMAGE);

		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
		switch (item.getItemId()) {
			case R.id.camera_switch:
				if (m_camera_view != null) {
					m_camera_view.nextCamera();
				}
				return true;
			case R.id.select_image:
				intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Image"), 42);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	protected void onActivityResult(int request_code, int result_code, Intent data) {
		super.onActivityResult(request_code, result_code, data);

	    if (request_code == 42 && result_code == RESULT_OK && data != null) {
			Uri    selected_image;
			Cursor cursor;
			String path;

			selected_image = data.getData();
			cursor = getContentResolver().query(selected_image,
				new String[]{ MediaStore.Images.Media.DATA }, null, null, null);

            cursor.moveToFirst();

            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
			cursor.close();

			m_canvas_view.setSelectedImage(BitmapFactory.decodeFile(path));
		}
	}
}

