/**
 * 
 */
package sma.tools;


/**
 * @author Jérémy
 *
 */
public class Config {

	public static final int TICKER_DELAY = 1000;
	public static final int STOCK_MAX_PRODUCTION = 50;
	public static final int STOCK_MAX_CONSUMPTION = 50;
	public static final float INIT_MONEY = 200;
	public static final float INIT_PRICE = 1;
	public static final double INFINI = 99999999;
	
	//Variable pour modification des prix
	public static final double PRICE_MIN_SATISFACTION = 90;
	public static final double PRICE_MAX_SATISFACTION = 100;
	public static final double PRICE_MIN_MONEY = 150;
	public static final double PRICE_MAX_MONEY = 230;
	
	/**
	 * Constante de reduction de la satifaction
	 * Voir methode: private void reduceSatifaction(float delta)
	 */
	public static final double CONST_REDUCE_SATIFACTION = 1;
	
	//System.out.println(System.getProperty("user.home"));
}
