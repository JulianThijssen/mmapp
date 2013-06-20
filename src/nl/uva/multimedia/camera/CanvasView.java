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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Matrix;
import android.graphics.Bitmap;
import android.view.View;
import android.util.AttributeSet;
import android.util.Log;

import java.lang.Math;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;

public class CanvasView extends View {
	public static int CAMERA_MODE = 0;
	public static int PICTURE_MODE = 1;
	private int[] argb;
	private int width;
	private int height;
	
	private int[] picture;
	private int picture_width;
	private int picture_height;
	private int mode = CAMERA_MODE;


	public CanvasView(Context context) {
		super(context);
	}
	
	public CanvasView(Context context, AttributeSet attributes) {
		super(context, attributes);
	}
	
	public CanvasView(Context context, AttributeSet attributes, int style) {
		super(context, attributes, style);
	}
	
	@Override protected void onMeasure(int width, int height) {
		super.onMeasure(width, height);
	}

	/* Called whenever the canvas is dirty and needs redrawing */
	@Override protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		/* Define the basic paint */
		Paint paint = new Paint();
		paint.setColor(Color.rgb(0xa0,0xa0,0xb0));
		paint.setAntiAlias(true);
	
		/* text inherits from the basic paint */
		Paint text = new Paint(paint);
		text.setColor(Color.WHITE);
		text.setShadowLayer(3.0F,3.0F,3.0F,Color.rgb(0x20,0x20,0x20));
		text.setTextSize(getHeight() * 0.1F);

		if(mode == CAMERA_MODE) {
			if(argb != null) {
				canvas.drawBitmap(argb, 0, width, 0f, 0f, width, height, false, null);
			}
		}
		if(mode == PICTURE_MODE) {
			canvas.drawBitmap(picture, 0, picture_width, 0f, 0f, picture_width, picture_height, false, null);
		}
	}

	/* set the argb value */
	public void setArgb(int[] argb, int width, int height) {
		this.width = width;
		this.height = height;
		this.argb = argb;
	}
	
	public void setMode(int mode) {
		if(mode == PICTURE_MODE) {
			picture = argb.clone();
			picture_width = width;
			picture_height = height;
		}
		this.mode = mode;
	}

	public static void saveImage(int[] argb) {
		//int[] img = new int[240*320];
		//Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
        //bitmap.copyPixelsFromBuffer(makeBuffer(vector, vector.length));
        //bitmap.setPixels(argb, 0, 240, 0, 0, 240, 320);
		//img[5] = this.argb[5];
	}
}

