/**
 * 
 */
package sma.agent;

import jade.core.Agent;
import jade.util.Logger;
import sma.tools.Config;

/**
 * @author Jérémy
 *
 */
public class AgentCommercial extends Agent {

	private static final long serialVersionUID = 1L;
	private Logger logger;
	
	private Products production;
	private float stock_production;
	private float stock_max_production;
	private float price;
	
	private Products consumption;
	private float stock_consumption;
	private float stock_max_consumption;
	
	private float money;
	private float satisfaction;
	
	@Override
	protected void setup() {
		super.setup();
		
		//Récupération de la classe de gestion des log de jade
		logger = Logger.getMyLogger(this.getClass().getName());

		//Initialise les variables
		init();
		
		//Message du creation de l'agent
		logger.log(Logger.INFO, "Create Agent : "+this); 
		
		//Ajout des classe Behviours
		addBehaviour(new AgentCommercialBehviours(this, Config.TICKER_DELAY));
		
	}

	/**
	 * Initialise l'agent avec les valeurs par defaut
	 */
	public void init(){
		production = Products.getRandom();
		stock_production = 0;
		stock_max_production = Config.STOCK_MAX_PRODUCTION;
		
		do{
			consumption = Products.getRandom();
		}while(consumption.equals(production));
		stock_consumption = 0;
		stock_max_consumption = Config.STOCK_MAX_CONSUMPTION;
		
		satisfaction = 1;
		money = Config.INIT_MONEY;
		price = Config.INIT_PRICE;
	}
	
	@Override
	protected void takeDown() {
		super.takeDown();
		
		logger.log(Logger.INFO, "Destroy agent :"+this); 
	}
	
	/**
	 * l'agent creé une quantité de produit 
	 * @param delta Le temps depuis la dernière execution de cette methode
	 * @param quantity La quantité de produit creé par seconde
	 */
	public void produce(float delta, float quantity){
		float total = quantity * delta;
		addStock_Product(total);
		
		logger.log(Logger.INFO, "Agent : "+this.getName()+", produce : "+total+" ("+quantity+" /sec)");
	}
	public void produce(float delta){
		produce(delta, 1);
	}
	
	
	/**
	 * l'agent consomme des ressources
	 * @param delta Le temps depuis la dernière execution de cette methode
	 */
	public void consomme(float delta, float quantity){
		float total = quantity * delta;
		removeStock_Consomme(total);
		
		logger.log(Logger.INFO, "Agent : "+this.getName()+", consomme : "+total+" ("+quantity+" /sec)");
	}
	public void consomme(float delta){
		consomme(delta, 1);
	}
	
	//---------------------GETTER / SETTER------------------------------------------------------
	
	/**
	 * Ajoute des produits au stock de produit creé
	 * @param quantity Quantité de produit ajouté
	 */
	private void addStock_Product(float quantity){
		stock_production += quantity;
		if(stock_production > stock_max_production) stock_production = stock_max_production;
	}
	
	/**
	 * Retire des produits au stock de produit creé
	 * @param quantity Quantité de produit retiré
	 */
	private void removeStock_Product(float quantity){
		stock_production -= quantity;
		if(stock_production < 0) stock_production = 0;
	}
	
	/**
	 * Ajoute des produits au stock de produit consommé
	 * @param quantity Quantité de produit ajouté
	 */
	private void addStock_Consomme(float quantity){
		stock_consumption += quantity;
		if(stock_consumption > stock_max_consumption) stock_consumption = stock_max_consumption;
	}
	
	/**
	 * Retire des produits au stock de produit consommé
	 * @param quantity Quantité de produit retiré
	 */
	private void removeStock_Consomme(float quantity){
		stock_consumption -= quantity;
		if(stock_consumption < 0) stock_consumption = 0;
	}
	
	//----------------------ToString-----------------------------------------
	
	@Override
	public String toString() {
		String text = this.getName()+ " <"+this.getClass().getName()+"> : Produit : "+production+", Consomme : "+consumption;
		return text;
	}
	
}
