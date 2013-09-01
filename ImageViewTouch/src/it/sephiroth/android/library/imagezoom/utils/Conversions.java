package it.sephiroth.android.library.imagezoom.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**A utility class for conversions
 * 
 * @author Chris Allen
 */
public class Conversions {

	/**Converts the pixel value to the context-specific density pixels
	 * 
	 * @param ctx Context to determine screen density
	 * @param dp Pixel value to convert to density pixels
	 * @return A float representation of the pixel value converted to density pixels
	 */
	public static float dpToPixels(Context ctx, int dp) {
		Resources r = ctx.getResources();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
				dp, r.getDisplayMetrics());
	}  
}