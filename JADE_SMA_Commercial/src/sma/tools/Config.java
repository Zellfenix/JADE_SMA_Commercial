/**
 * 
 */
package sma.tools;


/**
 * @author J�r�my
 *
 */
public class Config {

	public static final int TICKER_DELAY = 1200;
	public static final int STOCK_MAX_PRODUCTION = 100;
	public static final int STOCK_MAX_CONSUMPTION = 100;
	public static final float INIT_MONEY = 100;
	public static final float INIT_PRICE = 1;
	
	/**
	 * Constante de reduction de la satifaction
	 * Voir methode: private void reduceSatifaction(float delta)
	 */
	public static final double CONST_REDUCE_SATIFACTION = 1;
	
	//System.out.println(System.getProperty("user.home"));
}