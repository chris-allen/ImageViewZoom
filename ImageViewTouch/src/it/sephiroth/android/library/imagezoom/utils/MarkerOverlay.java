package it.sephiroth.android.library.imagezoom.utils;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

public class MarkerOverlay {

	public final Bitmap markerImage;
	public final List<MapMarker> markers;
	
	public MarkerOverlay(Context ctx, Bitmap mImage, List<MapMarker> m) {
		markerImage = mImage;
		markers = m;
	}
	
	public MapMarker onTap(int x, int y, float[] matrix) {
		float scale = matrix[Matrix.MSCALE_X];
		Log.i("MarkerOverlay", "scale: ("+scale+") tap x: ("+x+") y: ("+y+")");
		int halfWidth = (int) (markerImage.getWidth()/2*scale);
		int height = (int) (markerImage.getHeight()*scale);
		for(MapMarker m: markers) {
			Log.i("MarkerOverlay", "marker x: "+m.x+" y: "+m.y);
			if(x >= m.x-halfWidth && x <= m.x+halfWidth)
				if(y >= m.y-height && y <=m.y)
					return m;
		}
		return null;
	}
}