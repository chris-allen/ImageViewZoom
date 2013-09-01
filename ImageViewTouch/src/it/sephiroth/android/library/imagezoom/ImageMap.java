package it.sephiroth.android.library.imagezoom;

import it.sephiroth.android.library.imagezoom.utils.Conversions;
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
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class ImageMap extends ImageViewTouch {
	private Paint balloonPaint = new Paint();
	
	private Context context;
	private NinePatchDrawable balloon;
	private Rect balloonPadding = new Rect();
	private int balloonTextPadding = 6; //dp
	private MapMarker currMarker;
	private boolean balloonVisible;
	private List<MarkerOverlay> overlays = new ArrayList<MarkerOverlay>();
	private List<MarkerOverlay> rOverlays = new ArrayList<MarkerOverlay>();
	
	private GestureDetector mGestureDetector;
	private OnMarkerTapListener listener;
	private float[] baseValues = new float[9];

	public ImageMap(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		context = ctx;
		mGestureDetector = new GestureDetector(ctx, new GestureListener(), null, true);
		
		balloonPaint.setColor(Color.BLACK);
		balloonPaint.setAntiAlias(true);
		balloonPaint.setFakeBoldText(true);
		balloonPaint.setTextSize(Conversions.dpToPixels(ctx, 12));
		
		balloon = (NinePatchDrawable) ctx.getResources().getDrawable(R.drawable.map_balloon);
		balloon.getPadding(balloonPadding);
		balloonTextPadding = (int) Conversions.dpToPixels(ctx, balloonTextPadding);
	}
	
	public MarkerOverlay addOverlay(Bitmap mImage, List<MapMarker> markers) {
		MarkerOverlay overlay = new MarkerOverlay(context, mImage, markers);
		overlays.add(overlay);
		rOverlays.add(0, overlay);
		return overlay;
	}
	
	public void setOnMarkerTapListener(OnMarkerTapListener l) {
		listener = l;
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		MarkerOverlay currOverlay = null;
		
		
		getImageViewMatrix().getValues(baseValues);
		int width, halfWidth, height;
		int mX, mY;
		RectF origin = new RectF(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight()), r;
		getImageViewMatrix().mapRect(origin);
		
		// Draw markers
		for(MarkerOverlay overlay: overlays) {
			if(overlay.isVisible()) {
				width = (int) (overlay.markerImage.getWidth());
				halfWidth = (int) (width/2);
				height = (int) (overlay.markerImage.getHeight());
				
				for(MapMarker marker: overlay.markers) {
					mX = (int) (marker.x*getValue(mBaseMatrix, Matrix.MSCALE_X));
					mY = (int) (marker.y*getValue(mBaseMatrix, Matrix.MSCALE_X));
					
					r = new RectF(origin);
					r.left += mX*getScale() - halfWidth;
					r.right = r.left+width;
					r.top += mY*getScale() - height;
					r.bottom = r.top+height;
					canvas.drawBitmap(overlay.markerImage, null, r, null);
					
					if(marker == currMarker)
						currOverlay = overlay;
				}
			}
		}
		
		// Draw balloon
		if(balloonVisible && currOverlay != null && currMarker.title.length() > 0) {
			Rect textBounds = new Rect();
			balloonPaint.getTextBounds(currMarker.title, 0, currMarker.title.length(), textBounds);
			
			mX = (int) (currMarker.x*getValue(mBaseMatrix, Matrix.MSCALE_X));
			mY = (int) (currMarker.y*getValue(mBaseMatrix, Matrix.MSCALE_X));
			
			width = Math.max(textBounds.width() + balloonPadding.width(), balloon.getIntrinsicWidth()) + balloonTextPadding*2;
			height = Math.max(textBounds.height() + balloonPadding.height(), balloon.getIntrinsicHeight());
			float markerHeight = currOverlay.markerImage.getHeight();
			
			r = new RectF(origin);
			r.left += mX*getScale() - width/2;
			r.right = r.left + width;
			r.top += mY*getScale() - height - markerHeight;
			r.bottom = r.top + height;
			
			Rect bounds = new Rect();
			r.round(bounds);
			balloon.setBounds(bounds);
			balloon.draw(canvas);
			
			
			canvas.drawText(currMarker.title, 
					r.left + (width - textBounds.width())/2,
					r.top + (height - textBounds.height())/2,
					balloonPaint);
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
					currMarker = m;
					balloonVisible = true;
					if(listener != null)
						listener.onTap(m);
					invalidate();
					return true;
				}
			}
			balloonVisible = false;
			invalidate();
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
