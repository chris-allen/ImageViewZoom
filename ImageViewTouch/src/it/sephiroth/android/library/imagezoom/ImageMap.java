package it.sephiroth.android.library.imagezoom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;

public class ImageMap extends ImageViewTouch {
	
	private Bitmap poiMarker;

	public ImageMap(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	
	public void setPOIMarker(Bitmap b) {
		poiMarker = b;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Matrix m = new Matrix(getImageViewMatrix());
		m.postTranslate(50, 50);
		canvas.drawBitmap(poiMarker, m, null);
	}

}
