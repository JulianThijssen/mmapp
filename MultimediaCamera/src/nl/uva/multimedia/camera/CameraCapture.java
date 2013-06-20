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


/* Handles the camera data 
 *
 * The size is based on the size of the onscreen preview and calculated in
 * CameraView. This means that smaller screen devices, will, yes, have less
 * to calculate on.
 */
import android.hardware.Camera;
import android.util.Log;

import nl.uva.multimedia.camera.CameraData;
import nl.uva.multimedia.camera.CameraView;

class CameraCapture implements CameraView.PreviewCallback {
	protected CanvasView m_canvas_view = null;

	/* Is called by Android when a frame is ready */
	public void onPreviewFrame(byte[] data, Camera camera, boolean rotated) {
		Camera.Parameters parameters = camera.getParameters();
		Camera.Size       size       = parameters.getPreviewSize();

		/* Rotated is true if the height and width parameters should be swapped
		 * due to the image having been rotated internally. (Turns out it is
		 * rather tricky to patch a method in Java....) 
		 */
		if (rotated) {
			int holder;
			holder = size.height;
			size.height = size.width;
			size.width = holder;
		}

		Log.e("CameraCapture", "Width: " + size.width + " Height: " + size.height);
		int[] argb = new int[size.width*size.height];
	
		/* Use the appropriate YUV conversion routine to retrieve the
		 * data we actually intend to process.
		 */
		CameraData.convertYUV420SPtoARGB(argb, data, size.width, size.height);

		/* Work on the argb array */

		/* Transfer data/results to the canvas */

		/* Invalidate the canvas, forcing it to be redrawn with the new data.
		 * You can do this in other places, evaluate what makes sense to you.
		 */
		m_canvas_view.invalidate();
	}
	
	/*
	 * Getters and setters
	 */
	public CanvasView getCanvasView() {
		return m_canvas_view;
	}
	
	public void setCanvasView(CanvasView canvas_view) {
		m_canvas_view = canvas_view;
	}
}

