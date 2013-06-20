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
	
	public int image_width;
	public int image_height;
	private int[] argb;
	
	Bitmap m_image = null;
	private int width;
	private int height;
	private int[] argb_capture = new int[width*height];


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
	
		/* Save state */
		canvas.save();
		//canvas.translate(getWidth() * 0.1F, getHeight() * 0.1F);
		canvas.drawText("HEE", 0, 0, text);
		//argb_capture = argb;
		if(MyButton.button){
			argb_capture = argb;
			Log.e("AAP", Arrays.toString(argb_capture));
			MyButton.button = false;
		}
		canvas.drawBitmap(argb_capture, 0, width, 0f, 0f, width, height, false, null);
		canvas.restore();
		
		/* Paint a image if we have it, just a demo, clean this up so it works
		 * your way, or remove it if you don't need it
		 */
		if (m_image != null) {
			Rect rect = new Rect(
					(int) (getWidth()*0.25F), (int) (getHeight()*0.25F), 
					(int) (getWidth()*0.75F), (int) (getHeight()*0.75F));
			Paint bmp = new Paint();
			bmp.setAntiAlias(true);
			bmp.setFilterBitmap(true);
			bmp.setDither(true);


			canvas.drawBitmap(m_image, null, rect, paint);
		}
	}

	/* Accessors */
	public void setSelectedImage(Bitmap image) {
		m_image = image;
	}

	public Bitmap getSelectedImage() {
		return m_image;
	}

	/* set the argb value */
	public void setArgb(int[] argb, int width, int height) {
		this.width = width;
		this. height = height;
		this.argb = argb;
	}

	public static void saveImage(int[] argb) {
		//int[] img = new int[240*320];
		//Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
        //bitmap.copyPixelsFromBuffer(makeBuffer(vector, vector.length));
        //bitmap.setPixels(argb, 0, 240, 0, 0, 240, 320);
		//img[5] = this.argb[5];
	}
	
	
}

