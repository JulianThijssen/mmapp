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
	String path = null;
	File[] photos = null;
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
		String noimages = "No Images";
		canvas.drawText(noimages, (getWidth() - paint.getTextSize() * (noimages.length() / 2f))/2, getHeight()/2, paint);
		
		if(photos != null && photos.length > 0) {
			String filePath = photos[photoIndex].getAbsolutePath();
			canvas.drawBitmap(BitmapFactory.decodeFile(filePath), 180, 0, paint);
		}
	}
}
