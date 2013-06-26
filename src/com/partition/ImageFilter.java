package com.partition;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

public class ImageFilter {
	public static int CUT_OFF = 100;
	
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
	
	public static Bitmap scale(Bitmap image, int width, int height) {
		Log.v("Bitmap", "Bitmap: " + image.getWidth() + " " + image.getHeight());
		int srcwidth = image.getWidth();
		int srcheight = image.getHeight();
		float xRatio = ((float) width) / srcwidth;
		float yRatio = ((float) height) / srcheight;
		Matrix matrix = new Matrix();
		matrix.postScale(xRatio, yRatio);
		Log.v("Bitmap", "Bitmap: " + width + " " + height);
		Log.v("Matrix", "Matrix: " + matrix.toShortString());
		Bitmap newimage = Bitmap.createBitmap(image, 0, 0, srcwidth, srcheight, matrix, false);
		return newimage;
	}
}
