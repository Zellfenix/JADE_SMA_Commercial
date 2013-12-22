/**
 * 
 */
package sma.agent;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.Logger;
import sma.tools.Config;

/**
 * @author Jérémy
 *
 */
public class AgentCommercial extends Agent {

	private static final long serialVersionUID = 1L;
	private Logger logger;
	
	public Products production;
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
		register();
		
		//Message du creation de l'agent
		logger.log(Logger.INFO, "Create Agent : "+this); 
		
		//Ajout des classe Behviours
		addBehaviour(new AgentCommercialBehviours(this, Config.TICKER_DELAY));
		
	}

	@Override
	protected void takeDown() {
		super.takeDown();
		deregister();
		logger.log(Logger.INFO, "Destroy agent :"+this); 
	}
	
	/**
	 * Initialise l'agent avec les valeurs par defaut
	 */
	public void init(){
		Object[] args = getArguments();
		if(args != null && args.length > 2){
		    String arg_production = (String)args[0];
		    String arg_consommation = (String)args[1];
		    production = Products.valueOf(arg_production);
		    consumption = Products.valueOf(arg_consommation);
		}else{
			production = Products.getRandom();
			do{
				consumption = Products.getRandom();
			}while(consumption.equals(production));
		}
		
		stock_production = 0;
		stock_max_production = Config.STOCK_MAX_PRODUCTION;
		
		stock_consumption = 0;
		stock_max_consumption = Config.STOCK_MAX_CONSUMPTION;
		
		satisfaction = 1;
		money = Config.INIT_MONEY;
		price = Config.INIT_PRICE;
	}
	
	/**
	 * Recherche au près du DF les vendeurs disponibles
	 * @param product Produit recherché
	 * @return Liste des vendeurs
	 */
	public DFAgentDescription[] search(String product){
		// Update the list of seller agents
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("CFP "+product);
		template.addServices(sd);
		
		DFAgentDescription[] result;
		try {
			result = DFService.search(this, template);
			return result;
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		return null;
	}
	public DFAgentDescription[] search(){
		return search(consumption.toString());
	}
	
	
	/**
	 * Enregistre l'agent dans le DF
	 */
	public void register(){
		// Register the ComputeAgent service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("CFP "+production);
		sd.setName(getName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
			logger.log(Logger.INFO, "Agent is register!"); 
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
	/**
	 * Retire l'agent du DF
	 */
	public void deregister(){
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
			logger.log(Logger.INFO, "Agent is deregister!"); 
		}catch(FIPAException fe) {
			fe.printStackTrace();
		}
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
