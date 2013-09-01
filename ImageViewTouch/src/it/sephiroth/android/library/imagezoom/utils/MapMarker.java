package it.sephiroth.android.library.imagezoom.utils;

/**Simple class for storing marker location and title
 * 
 * @author Chris Allen
 */
public class MapMarker {
	public final String title;
	public final String keywords;
	public final int x;
	public final int y;
	
	public MapMarker(String title, int x, int y) {
		this(title, "", x, y);
	}
	
	public MapMarker(String title, String keywords, int x, int y) {
		this.title = title;
		this.keywords = keywords;
		this.x = x;
		this.y = y;
	}
}
