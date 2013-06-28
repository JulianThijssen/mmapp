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
import android.view.View;

public class PhotoView extends View {
	private static final String NO_IMAGES = "No Images";
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
	
	/** Returns the amount of photos currently in the gallery */
	public int getPhotoCount() {
		if(previewList != null) {
			return previewList.size();
		}
		return 0;
	}
	
	/** Clears the list of previews, then loads up all the files that are
	 *  stored at the path. It then decodes these files into bitmaps and
	 *  scales them to a size that is can be previewed and applies contrast.
	 */
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
	
	/** Sets the index of the preview list to select which photo is shown
	 *  in the photo view. */
	public void setPhotoIndex(int index) {
		photoIndex = index;
	}
	
	/** Gets a file descriptor to the photo which is currently shown in
	 *  the photo view.
	 */
	public File getCurrentPhoto() {
		if(photos != null) {
			return photos[photoIndex];
		}
		return null;
	}
	
	/** Sets a path to find files in using the reloadPhotos method */
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
			canvas.drawText(NO_IMAGES, (getWidth() - paint.getTextSize() * (NO_IMAGES.length() / 2f))/2, getHeight()/2, paint);
		}
	}
}
