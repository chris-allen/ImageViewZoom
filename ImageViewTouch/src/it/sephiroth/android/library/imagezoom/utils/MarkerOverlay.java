package it.sephiroth.android.library.imagezoom.utils;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;

/**Overlay that stores markers to be drawn on an 
 * {@link it.sephiroth.android.library.imagezoom.ImageMap ImageMap}.
 * 
 * @author Chris Allen
 */
public class MarkerOverlay {

	public final Bitmap markerImage;
	public final List<MapMarker> markers;
	private boolean visible = true;
	
	public MarkerOverlay(Context ctx, Bitmap mImage, List<MapMarker> m) {
		markerImage = mImage;
		markers = m;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public MapMarker onTap(RectF origin, int x, int y, float originScale, float coordScale, float xPad, float yPad) {
		if(visible) {
			int mX, mY;
			int height = (int) (markerImage.getHeight());
			int width = (int) (markerImage.getWidth());
			int halfWidth = width/2;
			
			for(MapMarker m: markers) {
				mX = (int) (origin.left + m.x*coordScale*originScale - halfWidth);
				mY = (int) (origin.top + m.y*coordScale*originScale - height);
				
				if(x >= mX && x <= mX+width)
					if(y >= mY && y <= mY+height)
						return m;
			}
		}
		return null;
	}
}