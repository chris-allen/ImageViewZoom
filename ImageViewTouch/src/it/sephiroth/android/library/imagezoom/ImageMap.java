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
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class ImageMap extends ImageViewTouch {
	private Paint circlePaint = new Paint();
	
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
		circlePaint.setColor(Color.BLUE);
		circlePaint.setAlpha(175);
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
		int width, halfWidth, height;
		int mX, mY;
		RectF origin = new RectF(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight()), r;
		getImageViewMatrix().mapRect(origin);
		for(MarkerOverlay overlay: overlays) {
			if(overlay.isVisible()) {
				width = (int) (overlay.markerImage.getWidth()*getScale());
				halfWidth = (int) (width/2);
				height = (int) (overlay.markerImage.getHeight()*getScale());
				for(MapMarker marker: overlay.markers) {
					mX = (int) (marker.x*getValue(mBaseMatrix, Matrix.MSCALE_X));
					mY = (int) (marker.y*getValue(mBaseMatrix, Matrix.MSCALE_X));
					
					r = new RectF(origin);
					r.left += mX*getScale() - halfWidth;
					r.right = r.left+width;
					r.top += mY*getScale() - height;
					r.bottom = r.top+height;
					canvas.drawBitmap(overlay.markerImage, null, r, null);
				}
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
			RectF origin = new RectF(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
			getImageViewMatrix().mapRect(origin);
			for(MarkerOverlay overlay: rOverlays) {
				m = overlay.onTap(origin, (int) ev.getX(), (int) ev.getY(), 
						getScale(), getValue(mBaseMatrix, Matrix.MSCALE_X),
						baseValues[Matrix.MTRANS_X], baseValues[Matrix.MTRANS_Y]);
				if(m != null) {
					scrollBy((getWidth() / 2) - ev.getX(), 
							(getHeight() / 2) - ev.getY(),
							300);
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
