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
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import sma.tools.Config;
import sma.tools.analyse.Analyse;

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
	/**
	 * Temps passé sans pouvoir consommé de produit
	 */
	private float famine;
	
	@Override
	protected void setup() {
		super.setup();
		
		//Ajoute l'agent commercial au systeme d'analyse de la simulation
		Analyse.getInstance().agent_setup(this);
		
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
		if(stock_production == stock_max_production){
			//Augmentation de la satifaction
		}
		
		logger.log(Logger.INFO, "Agent : "+this.getName()+", produce :"+stock_production+"(+"+total+") (+"+quantity+" /sec)");
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
		
		logger.log(Logger.INFO, "Agent : "+this.getName()+", consomme :"+stock_consumption+"(-"+total+") (-"+quantity+" /sec)");
	}
	public void consomme(float delta){
		consomme(delta, 1);
	}
	
	
	/**
	 * Vérifie la satifaction et effectue les opérations nécéssaires
	 */
	public void check_satisfaction(float delta){
		if(satisfaction <= 0.0){
			logger.log(Logger.INFO, "Agent : "+this.getName()+", is starving to death !");
			kill();
		}
		
		if(satisfaction == 1.0){//TODO condition de Duplication ?
			duplication();
		}
		
		if(stock_consumption <= 0){
			famine += delta;// * 1;
			reduceSatifaction(delta);
			logger.log(Logger.INFO, "Agent : "+this.getName()+", Famine increased to "+famine+" !");
		}else{
			famine = 0;
		}
	}
	
	//---------------------Private Methode------------------------------------------------------
	
	private void kill(){
		AgentContainer c = getContainerController();
		try {
			AgentController ac = c.getAgent(this.getAID().getLocalName());
			ac.kill();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
	}
	
	private void duplication(){
		//TODO
	}
	
	/**
	 * Ajoute des produits au stock de produit creé
	 * @param quantity Quantité de produit ajouté
	 */
	private void addStock_Product(float quantity){
		stock_production += quantity;
		if(stock_production > stock_max_production){
			stock_production = stock_max_production;
		}
	}
	
	/**
	 * Retire des produits au stock de produit creé
	 * @param quantity Quantité de produit retiré
	 */
	private void removeStock_Product(float quantity){
		stock_production -= quantity;
		if(stock_production < 0){
			stock_production = 0;
		}
	}
	
	/**
	 * Ajoute des produits au stock de produit consommé
	 * @param quantity Quantité de produit ajouté
	 */
	private void addStock_Consomme(float quantity){
		stock_consumption += quantity;
		if(stock_consumption > stock_max_consumption){
			stock_consumption = stock_max_consumption;
		}
	}
	
	/**
	 * Retire des produits au stock de produit consommé
	 * @param quantity Quantité de produit retiré
	 */
	private void removeStock_Consomme(float quantity){
		stock_consumption -= quantity;
		if(stock_consumption < 0){
			stock_consumption = 0;
		}
	}
	
	/**
	 * Reduit exponentiellement la satifaction
	 * @param delta
	 */
	private void reduceSatifaction(float delta){ 
		float reduction = (float) (delta * Math.pow(Config.CONST_REDUCE_SATIFACTION, famine)); //TODO
		satisfaction -= reduction;
	}
	
	//-----------------------GETTER------------------------------------------------
	
	public Products getConsumption() {
		return consumption;
	}
	
	public float getStock_consumption() {
		return stock_consumption;
	}
	
	public float getStock_max_consumption() {
		return stock_max_consumption;
	}
	
	public Products getProduction() {
		return production;
	}
	
	public float getStock_production() {
		return stock_production;
	}
	
	public float getStock_max_production() {
		return stock_max_production;
	}
	
	public float getPrice() {
		return price;
	}
	
	public float getSatisfaction() {
		return satisfaction;
	}
	
	public float getFamine() {
		return famine;
	}
	
	public float getMoney() {
		return money;
	}
	
	//----------------------ToString-----------------------------------------------
	
	@Override
	public String toString() {
		return "AgentCommercial [production=" + production
				+ ", stock_production=" + stock_production
				+ ", stock_max_production=" + stock_max_production + ", price="
				+ price + ", consumption=" + consumption
				+ ", stock_consumption=" + stock_consumption
				+ ", stock_max_consumption=" + stock_max_consumption
				+ ", money=" + money + ", satisfaction=" + satisfaction
				+ ", famine=" + famine + "]";
	}
	
}
