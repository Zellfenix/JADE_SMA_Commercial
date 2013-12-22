/**
 * 
 */
package sma.agent;

import java.util.Random;

/**
 * @author Jérémy
 *
 */
public enum Products {

	//Liste des produits
	A(),
	B();
	
	private Products() {
	}
	
	public static Products getRandom(){
		Random r = new Random();
		int index = r.nextInt(Products.values().length);
		return Products.values()[index];
	}
	
}

