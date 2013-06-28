package com.partition;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ImageFilter {
	public static int CUT_OFF = 90;
	
	/** Contrasts an bitmap by looking at each component in each pixel and 
	 *  seeing if all the components are above the cut-off value. If they
	 *  are make the pixel value white, if they aren't, make it black.
	 *  
	 * @param   image   The bitmap to be contrasted
	 * @return          Returns the contrasted bitmap
	 */
	public static Bitmap contrast(Bitmap image) {
		for(int x = 0; x < image.getWidth(); x++) {
			for(int y = 0; y < image.getHeight(); y++) {
				int color = image.getPixel(x, y);
				int red   = (color >> 16) & 0x000000FF;
				int green = (color >> 8)  & 0x000000FF;
				int blue  = (color >> 0)  & 0x000000FF;
				if(red > CUT_OFF && green > CUT_OFF && blue > CUT_OFF) {
					image.setPixel(x, y, 0xFFFFFFFF);
				}
				if(red < CUT_OFF && green < CUT_OFF && blue < CUT_OFF) {
					image.setPixel(x, y, 0xFF000000);
				}
			}
		}
		return image;
	}
	
	/** Scales a bitmap by using a scaling matrix
	 * 
	 * @param   image    The bitmap to be scaled
	 * @param   width    The new width of the bitmap
	 * @param   height   The new height of the bitmap
	 * @return           Returns the scaled bitmap
	 */
	public static Bitmap scale(Bitmap image, int width, int height) {
		int srcwidth = image.getWidth();
		int srcheight = image.getHeight();
		float xRatio = ((float) width) / srcwidth;
		float yRatio = ((float) height) / srcheight;
		Matrix matrix = new Matrix();
		matrix.postScale(xRatio, yRatio);

		Bitmap newimage = Bitmap.createBitmap(image, 0, 0, srcwidth, srcheight, matrix, false);
		return newimage;
	}
}
