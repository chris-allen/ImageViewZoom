package it.sephiroth.android.library.imagezoom;

import it.sephiroth.android.library.imagezoom.utils.MapMarker;
import it.sephiroth.android.library.imagezoom.utils.MarkerOverlay;
import it.sephiroth.android.library.imagezoom.utils.OnMarkerTapListener;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class ImageMap extends ImageViewTouch {
	
	private Context context;
	private List<MarkerOverlay> overlays = new ArrayList<MarkerOverlay>();
	private List<MarkerOverlay> rOverlays = new ArrayList<MarkerOverlay>();
	
	private GestureDetector mGestureDetector;
	private OnMarkerTapListener listener;
	private float[] baseValues = new float[9];

	public ImageMap(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		context = ctx;
		mGestureDetector = new GestureDetector(ctx, new GestureListener(), null, true);
	}
	
	public void addOverlay(Bitmap mImage, List<MapMarker> markers) {
		MarkerOverlay overlay = new MarkerOverlay(context, mImage, markers);
		overlays.add(overlay);
		rOverlays.add(0, overlay);
	}
	
	public void setOnMarkerTapListener(OnMarkerTapListener l) {
		listener = l;
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		getImageViewMatrix().getValues(baseValues);
		Matrix m;
		int halfWidth, height;
		for(MarkerOverlay overlay: overlays) {
			halfWidth = overlay.markerImage.getWidth()/2;
			height = overlay.markerImage.getHeight();
			for(MapMarker marker: overlay.markers) {
				m = new Matrix();
				m.setValues(baseValues);
				m.postTranslate((marker.x-halfWidth)*getScale(), 
						(marker.y-height)*getScale());
				canvas.drawBitmap(overlay.markerImage, m, null);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		mGestureDetector.onTouchEvent(ev);
		return super.onTouchEvent(ev);
	}

	public class GestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent ev) {
			getImageViewMatrix().getValues(baseValues);
			
			MapMarker m = null;
			for(MarkerOverlay overlay: rOverlays) {
				m = overlay.onTap((int) ev.getX(), (int) ev.getY(), baseValues);
				if(m != null) {
					if(listener != null) {
						listener.onTap(m);
						break;
					}
				}
			}
			return true;
		}
		
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) { }

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return false;
		}
	}
}
