package vsubhuman.xlsmanager.color;

import java.awt.Color;

/**
 * Created to handle some routine problems with colors.
 * It's abstract, so you can't create an instance. 
 * 
 * @author VSubhuman
 * @version 1.0
 */
public abstract class ColorHandler {

	public final static int MAX_RGB = 0xffffff;
	private final static int HEX_RADIX = 16;
	
	/**
	 * @param hex string representation of color you want to get
	 * @return {@link Color} created from specified hex string AND
	 *  <code>null</code> if specified string is <code>null</code>
	 * @throws NumberFormatException if specified hex string is not hex or too
	 *  big for integer
	 * @since 1.0
	 */
	public static Color hexStringToColor(String hex) throws NumberFormatException {
		
		return hex == null ? null : new Color(Integer.parseInt(hex, HEX_RADIX));
	}
	
	/**
	 * @param color you want get hex representation of
	 * @return hex representation of specified color as <code>String</code> AND
	 *  <code>null</code> if specified color is <code>null</code>
	 * @since 1.0
	 */
	public static String colorToHexString(Color color) {
		
		return color == null ? null : Integer.toHexString(MAX_RGB & color.getRGB());
	}
}
