package nl.uva.multimedia.camera;

/*
 * Framework for camera processing and visualisation
 *
 * For the Multimedia course in the BSc Computer Science 
 * at the University of Amsterdam 
 *
 * I.M.J. Kamps, S.J.R. van Schaik, R. de Vries (2013)
 */

/* XXX DO NOT change this code unless you know what you are doing XXX */

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.util.AttributeSet;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class CameraView extends ViewGroup implements SurfaceHolder.Callback, SensorEventListener, Camera.PreviewCallback {
	private final String TAG = "CameraView";

	/* BEST_FIT, including those too large, LARGEST_FIT, only up to
	 * those fitting in the preview frame.
	 */
	public enum SizeType { BEST_FIT, LARGEST_FIT };

	protected SurfaceView                m_surface_view     = null;
	protected Camera                     m_camera           = null;
	protected int                        m_camera_id        = 0;
	protected CameraView.PreviewCallback m_preview_callback = null;
	protected List<Camera.Size>          m_sizes            = null;
	protected Camera.Size                m_size             = null;
	protected SizeType                   m_size_type        = SizeType.BEST_FIT;
	protected int                        m_rotation         = -1;
	protected int                        m_real_rotation    = -1;
	protected int                        m_orientation      = 0;
	protected SensorManager              m_sensor_manager   = null;
	protected Sensor                     m_accelerometer    = null; 
	protected int                        m_frame_count      = 1;
	protected byte[]                     m_output           = new byte[1];

	public CameraView(Context context) {
		super(context);

		construct(context);
	}

	public CameraView(Context context, AttributeSet attributes) {
		super(context, attributes);

		construct(context);
	}

	public CameraView(Context context, AttributeSet attributes, int style) {
		super(context, attributes, style);

		construct(context);
	}

	public void construct(Context context) {
		SurfaceHolder surface_holder = null;

		/* Set up the surface view. */
		m_surface_view = new SurfaceView(context);
		addView(m_surface_view);

		/* Install the callback that gets notified whenever the surface has been
		 * created, destroyed or changed.
		 */
		surface_holder = m_surface_view.getHolder();
		surface_holder.addCallback(this);
		surface_holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		/* Some basic stuff to later handle rotations */
		m_sensor_manager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
		m_accelerometer  = m_sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}


	/* Acquire a camera, last used or the first back camera */
	public void acquireCamera() {
		CameraInfo info = new CameraInfo();
		int camera_id;

		if (m_camera_id != -1) {
			/* Re-open a previously opened camera. */
			acquireCamera(m_camera_id);
		} else {
			/* Find default back camera */
			for (camera_id = 0; camera_id < Camera.getNumberOfCameras(); camera_id++) {
				Camera.getCameraInfo(camera_id, info);

				if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
					break;
				}
			}

			acquireCamera(m_camera_id);
		}
	}

	/* Acquire a camera by ID. */
	public void acquireCamera(int camera_id) {
		/* Safe guard */
		if (m_camera != null) {
			releaseCamera();
		}

		m_camera_id = camera_id;
		m_camera = Camera.open(m_camera_id);

		/* Set initial orientation */
		setOrientation();

		/* Make sure we get rotation changes */
		m_sensor_manager.registerListener(this, m_accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

		if (m_camera != null) {
			m_camera.setPreviewCallback(null);
		}

		/* Get the supported preview sizes. */
		if (m_camera != null) {
			m_sizes = m_camera.getParameters().getSupportedPreviewSizes();

			requestLayout();
		}

		/* Toggle visibility to deal with mess */
		m_surface_view.setVisibility(INVISIBLE);
		m_surface_view.setVisibility(VISIBLE);
	}

	/* Release camera back to the system */
	public void releaseCamera() {
		if (m_camera != null) {
			m_camera.stopPreview();
			m_camera.setPreviewCallback(null);
			m_camera.release();
			m_camera = null;
			m_sensor_manager.unregisterListener(this);
		}
	}

	public int nextCamera() {
		releaseCamera();
		m_camera_id = (m_camera_id + 1) %  Camera.getNumberOfCameras();
		acquireCamera(m_camera_id);

		return m_camera_id;
	}

	private void setOrientation() {
		/* From public Android documentation */
		int degrees = 0;
		int result;
		CameraInfo info = new CameraInfo();

		Camera.getCameraInfo(m_camera_id, info);

		m_rotation = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
		switch(m_rotation) {
			case Surface.ROTATION_0: degrees = 0; break;
			case Surface.ROTATION_90: degrees = 90; break;
			case Surface.ROTATION_180: degrees = 180; break;
			case Surface.ROTATION_270: degrees = 270; break;
		}

		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;  /* mirror effect */
		} else {  
			result = (info.orientation - degrees + 360) % 360;
		}

		m_real_rotation = result;
		m_camera.stopPreview();
		m_camera.setDisplayOrientation(result);
		m_camera.startPreview();
	}

	/* Handle rotations so we can catch seascape/landscape changes */
	public void onSensorChanged(SensorEvent event) {
		int rotation = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();

		if(m_rotation != rotation && m_camera != null) {
			setOrientation();
		}

	}

	@Override protected void onMeasure(int width, int height) {
		int resolved_width  = resolveSize(getSuggestedMinimumWidth(), width);
		int resolved_height = resolveSize(getSuggestedMinimumHeight(), height);
	
		/* Compensate for rotations */
		if (m_real_rotation % 180 != 0) {
			int holder;
			holder = resolved_height;
			resolved_height = resolved_width;
			resolved_width = holder;
		}

		/* Get the selected type of camera size. */
		if (m_sizes != null)
			switch (m_size_type) {
				case LARGEST_FIT:
					m_size = getLargestSize(m_sizes, resolved_width, resolved_height);
					break;
				default:
					m_size = getOptimalSize(m_sizes, resolved_width, resolved_height);
			}

		super.onMeasure(width, height);
	}

	@Override protected void onLayout(boolean has_changed, int left, int top,
			int right, int bottom) {

		if (has_changed && getChildCount() > 0) {
			View    child         = getChildAt(0);
			double  width         = right - left;
			double  height        = bottom - top;
			double  camera_width  = width;
			double  camera_height = height;
			double  scale         = 0;

			Configuration config = getContext().getResources().getConfiguration();
			if (m_size != null) {
				if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
					camera_width  = m_size.height;
					camera_height = m_size.width;
				} else {
					camera_width  = m_size.width;
					camera_height = m_size.height;
				}
			}

			/* Find the most optimal rectangle describing the camera's surface
			 * that fits within the rectangle describing the view's surface,
			 * whilst maintaining the aspect ratio. 
			 */
			scale = Math.min(width / camera_width, height / camera_height);

			camera_width  *= scale;
			camera_height *= scale;

			/* Centre the lay-out. */
			left   = (int)((width - camera_width) / 2);
			top    = (int)((height - camera_height) / 2);
			right  = (int)((width + camera_width) / 2);
			bottom = (int)((height + camera_height) / 2);

			child.layout(left, top, right, bottom);
		}
	}

	/* Find the optimal size from the camera */
	private Camera.Size getOptimalSize(List<Camera.Size> sizes, int width,
			int height) {

		Camera.Size current         = sizes.get(0);
		double      lowest_distance = Double.MAX_VALUE;

		/* Walk through the list of sizes looking for the size where the
		 * absolute difference in width and the difference in height is the
		 * lowest.
		 */
		for (Camera.Size size : sizes) {
			double distance = (size.width - width) * (size.width - width) +
				(size.height - height) * (size.height - height);

			if (distance < lowest_distance) {
				lowest_distance = distance;
				current         = size;
			}
		}

		return current;
	}

	/* Find the largest size from the camera */
	private Camera.Size getLargestSize(List<Camera.Size> sizes, int width,
			int height) {

		Camera.Size current    = null;
		double      max_pixels = Double.MIN_VALUE;


		/* Walk through the list of sizes looking for the size where the
		 * pixel count is the highest while still fitting.
		 */
		for (Camera.Size size : sizes) {
				
			if (width > size.width && height > size.height) {
				double pixels = size.width * size.height;

				if (max_pixels < pixels) {
					max_pixels = pixels;
					current    = size;
				}
			}
		}

		/* Fallback on optimal size if we can't find a size that fits */
		if (current == null) {
			current = getOptimalSize(sizes, width, height);
		}

		return current;
	}

	public void surfaceCreated(SurfaceHolder surface_holder) {
		/* Set the render target for the camera. */
		try {
			if (m_camera != null)
				m_camera.setPreviewDisplay(surface_holder);
		} catch (IOException exception) {
			Log.e(TAG, "IOException caused by setPreviewDisplay().", exception);
		}
	}

	public void surfaceDestroyed(SurfaceHolder surface_holder) {
		/* Stop rendering. */
		if (m_camera != null)
			m_camera.stopPreview();
	}

	/* Called whenever the app surface changes, this includes after suspends,
	 * etc. So we have to essentially reset the whole camera.
	 *
	 * TODO verify this all
	 */
	public void surfaceChanged(SurfaceHolder surface_holder, int format,
			int width, int height) {
		List preview_fps_ranges;
		int preview_fps_range[];

		if (m_camera == null)
			return;

		Camera.Parameters parameters = null;

		/* Stop rendering. */
		m_camera.stopPreview();

		try {
			m_camera.setPreviewDisplay(surface_holder);
		} catch (IOException exception) {
			Log.e(TAG, "IOException caused by setPreviewDisplay().", exception);
		}

		/* Configure the camera's input size and frame rate. */
		parameters = m_camera.getParameters();

		if (m_size != null)
			parameters.setPreviewSize(m_size.width, m_size.height);

		requestLayout();

		preview_fps_ranges = parameters.getSupportedPreviewFpsRange();
		preview_fps_range = (int[]) preview_fps_ranges.get(preview_fps_ranges.size() - 1);
		parameters.setPreviewFpsRange(
				preview_fps_range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX], 
				preview_fps_range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
		m_camera.setParameters(parameters);
		m_camera.setPreviewCallback(null);
		m_camera.setPreviewCallback(this);

		/* Start rendering. */
		m_camera.startPreview();
	}

	/* Accessors */
	public int getCameraId() {
		return m_camera_id;
	}

	/* XXX TODO see if we can make this more efficient, this IS the bottleneck */
	public void onPreviewFrame(byte[] data, Camera camera) {
		/* Yes, this is above the code that does work, to make sure
		 * we don't needlessly allocate.
		 */
		if (m_real_rotation == 0) {
			m_preview_callback.onPreviewFrame(data, camera, false);
			return;
		}

		/* Now all the rotations */

		/* Precalculate a bunch of parameters */
		Camera.Parameters parameters = camera.getParameters();
		Camera.Size       size       = parameters.getPreviewSize();
		int               y_size     = size.width * size.height;
		int               uv_size    = y_size / 2;
		int               y_stride   = size.width;
		int               y_height   = size.height;
		int               y_off      = y_height - 1;
		int               uv_stride  = y_stride;
		int               uv_height  = y_height / 2;
		int               uv_off     = uv_height - 1;
		
		int i;

		/* Recyclable buffer */
		if (m_output.length != data.length) {
			m_output = new byte[data.length];
		}


		/* Do some magic rotation here on the YUV data... */
		switch (m_real_rotation) {
			case 180:
				/* Easiest case.... */
				for (i = 0; i < y_size; i++) {
					m_output[(y_size - 1) - i] = data[i]; 
				}
				for (i = 0; i < uv_size; i = i + 2) {
					m_output[(y_size + uv_size - 2) - i] = data[y_size + i]; 
					m_output[(y_size + uv_size - 1) - i] = data[y_size + i + 1]; 
				}
				m_preview_callback.onPreviewFrame(m_output, camera, false);
				break;
			case 90:
				/* Slightly more verbose code for this */
				for (i = 0; i < y_size; i++) {
					int x = (i % y_stride) * y_height;
					int y =  y_off - i / y_stride;
					m_output[x + y] = data[i];
				}
				for (i = 0; i < uv_size; i = i + 2) {
					int x = (i % uv_stride) * uv_height;
					int y =  (uv_off - i / uv_stride) * 2;
					m_output[y_size + x + y] = data[y_size + i]; 
					m_output[y_size + x + y + 1] = data[y_size + i + 1]; 
				}
				m_preview_callback.onPreviewFrame(m_output, camera, true);
				break;
			case 270:
				for (i = 0; i < y_size; i++) {
					int x = (i % y_stride) * y_height;
					int y =  y_off - i / y_stride;
					m_output[(y_size - 1) - (x + y)] = data[i];
				}
				for (i = 0; i < uv_size; i = i + 2) {
					int x = (i % uv_stride) * uv_height;
					int y =  (uv_off - i / uv_stride) * 2;
					m_output[(y_size + uv_size - 2) - (x + y)] = data[y_size + i]; 
					m_output[(y_size + uv_size - 1) - (x + y)] = data[y_size + i + 1]; 
				}
				m_preview_callback.onPreviewFrame(m_output, camera, true);
				break;
		}
				
	}

	public PreviewCallback getPreviewCallback() {
		return m_preview_callback;
	}

	public void setPreviewCallback(PreviewCallback preview_callback) {
		m_preview_callback = preview_callback;
	}

	public SizeType getSizeType() {
		return m_size_type;
	}

	public void setSizeType(SizeType size_type) {
		m_size_type = size_type;
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		/* NOP */
	}

	public interface PreviewCallback {
		public void onPreviewFrame(byte[] data, Camera camera, boolean rotated);
	}

}

