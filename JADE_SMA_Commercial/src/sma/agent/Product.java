/**
 * 
 */
package sma.agent;

import java.util.Random;

/**
 * @author J�r�my
 *
 */
public enum Product {

	//Liste des produits
	A(),
	B();
	
	private Product() {
	}
	
	public static Product getRandom(){
		Random r = new Random();
		int index = r.nextInt(Product.values().length);
		return Product.values()[index];
	}
	
}

