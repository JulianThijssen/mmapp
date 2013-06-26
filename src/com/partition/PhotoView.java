package com.partition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class PhotoView extends View {
	private static final String NO_IMAGES = "No Images";
	private static final String CORRUPT_IMAGE = "Corrupt Image";
	
	String path = null;
	File[] photos = null;
	List<Bitmap> previewList = new ArrayList<Bitmap>();
	int photoIndex = 0;

	public PhotoView(Context context) {
		super(context);
	}

	public PhotoView(Context context, AttributeSet attributes) {
		super(context, attributes);
	}
	
	public PhotoView(Context context, AttributeSet attributes, int style) {
		super(context, attributes, style);
	}
	
	public int getPhotoCount() {
		if(photos != null) {
			return photos.length;
		}
		return 0;
	}
	
	public void reloadPhotos() {	
		if(path != null) {
			File dir = new File(path);
			photos = dir.listFiles();
			Log.i("Size", "Width: " + getWidth() + " Height: " + getHeight());
			for(int i = 0; i < photos.length; i++) {
				if(photos[i].length() > 10) {
					Bitmap preview = BitmapFactory.decodeFile(photos[i].getAbsolutePath());
					preview = ImageFilter.scale(preview, getWidth(), getHeight());
					previewList.add(preview);
				}
			}
			Log.i("Length", "Length: " + photos.length);
		}
	}
	
	public void setPhotoIndex(int index) {
		photoIndex = index;
	}
	
	public File getCurrentPhoto() {
		return photos[photoIndex];
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawColor(0xFFa9a072);
		
		int textSize = getWidth() / 10;
		Paint paint = new Paint();
		paint.setColor(0xFFFFFFFF);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setTextSize(textSize);
		
		if(previewList.size() > 0) {
			canvas.drawBitmap(previewList.get(photoIndex), 180, 0, paint);
		} else {
			canvas.drawText(NO_IMAGES, (getWidth() - paint.getTextSize() * (CORRUPT_IMAGE.length() / 2f))/2, getHeight()/2, paint);
		}
	}
}
