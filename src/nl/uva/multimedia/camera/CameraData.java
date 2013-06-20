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

/**
 * Class holding conversion functions for some of the many image formats camera
 * data can be supplied in or can be needed in. This format is guaranteed 
 * supported on all Android devices.
 *
 * This class can't be instantiated, it just holds functions.  Trying to write
 * this as a class just makes it too slow due to the many layers of copies
 */
public final class CameraData {
	private CameraData() { }
	private static final int y_scale  = (int)(1.164063 * 1024);
	private static final int rv_scale = (int)(1.595703 * 1024);
	private static final int gu_scale = (int)(0.390625 * 1024);
	private static final int gv_scale = (int)(0.813477 * 1024);
	private static final int bu_scale = (int)(2.017578 * 1024);

	/** 
	 * convert YUV data to RGB data.
	 *
	 * @param rgb Output array of integers, containing ARGB data, in that
	 * order, preallocate this to the correct size for the image.
	 *
	 * @param yuv Input array of YUV byte data, as supplied by Android cameras
	 * @param width The width of the output image
	 * @param height The height of the output image
	 */
	public static void convertYUV420SPtoARGB(int[] rgb, byte[] yuv, int width,
		int height) {

		/* Sanity check, we won't do anything for wrongly sized data */
		if (yuv.length != (width * height) + 2 * (width/2 * height/2)) {
			return;
		}

		int frame_size = width * height;
		
		int y_plane  = 0;
		int uv_plane = frame_size;
		
		int y_value  = 0;
		int u_value  = 0;
		int v_value  = 0;
		
		int r_value  = 0;
		int g_value  = 0;
		int b_value  = 0;
		
		for (int y = 0; y < height; ++y) {
			/* Use the same strip in the UV-plane for every two vertical
			 * Y-components.
			 */
			uv_plane = frame_size + (y / 2) * width;
		
			for (int x = 0; x < width; ++x) {
				/* Grab the Y-component from the Y-plane, subtract 16 to fix
				 * the range.
				 */
				y_value = (yuv[y_plane] & 0xFF) - 16;
			
				/* Extract the UV-components from the UV-plane, and use it for
				 * every two horizontal Y-components.
				 */
				if ((x % 2) == 0) {
					/* Subtract 128 to fix the range. */
					v_value = (yuv[uv_plane++] & 0xFF) - 128;
					u_value = (yuv[uv_plane++] & 0xFF) - 128;
				}
			
				/* Multiply the YUV-vector with the YUVtoRGB matrix. */
				y_value *= y_scale;
				r_value = (y_value + rv_scale * v_value) / 1024;
				g_value = (y_value - gu_scale * u_value - gv_scale * v_value) /
					1024;
				b_value = (y_value + bu_scale * u_value) / 1024;
				
				/* Clamp the RGB-vector. */
				if (r_value > 255)
					r_value = 255;
				else if (r_value < 0)
					r_value = 0;
				
				if (g_value > 255)
					g_value = 255;
				else if (g_value < 0)
					g_value = 0;
				
				if (b_value > 255)
					b_value = 255;
				else if (b_value < 0)
					b_value = 0;
				
				/* Pack the RGB-vector. */
				rgb[y_plane] = (0xff << 24) | (r_value << 16) | (g_value << 8) |
					(b_value << 0);
				
				++y_plane;
			}
		}
	}
}

