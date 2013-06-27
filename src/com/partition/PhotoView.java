package com.partition;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class PhotoView extends View {
	private static final String NO_IMAGES = "No Images";
	private static final String CORRUPT_IMAGE = "Corrupt Image";
	private static final int    BACKGROUND_COLOR = 0xFF303030;
	
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
		if(previewList != null) {
			return previewList.size();
		}
		return 0;
	}
	
	public void reloadPhotos() {
		previewList.clear();
		
		if(path != null) {
			File dir = new File(path);
			photos = dir.listFiles();

			for(int i = 0; i < photos.length; i++) {
				Bitmap preview = BitmapFactory.decodeFile(photos[i].getAbsolutePath());
				preview = ImageFilter.scale(preview, 400, 400);
				preview = ImageFilter.contrast(preview);
				previewList.add(preview);
			}
		}
	}
	
	public void setPhotoIndex(int index) {
		Log.d("index", "index: "+ index);
		photoIndex = index;
	}
	
	public File getCurrentPhoto() {
		if(photos != null) {
			return photos[photoIndex];
		}
		return null;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawColor(BACKGROUND_COLOR);
		
		int textSize = getWidth() / 10;
		Paint paint = new Paint();
		paint.setColor(0xFFFFFFFF);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setTextSize(textSize);
		
		if(previewList.size() > 0) {
			canvas.drawBitmap(previewList.get(photoIndex), getWidth()/2 - 200, getHeight()/2 - 200, paint);
		} else {
			canvas.drawText(NO_IMAGES, (getWidth() - paint.getTextSize() * (CORRUPT_IMAGE.length() / 2f))/2, getHeight()/2, paint);
		}
	}
}
