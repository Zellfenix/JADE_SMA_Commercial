/**
 * 
 */
package sma.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.io.IOException;
import java.util.logging.Level;

import sma.tools.Config;

/**
 * @author Jérémy
 *
 */
public class AgentCommercial extends Agent {

	private static final long serialVersionUID = 1L;
	private Logger logger;
	
	private Product production;
	private double stock_production;
	private double stock_max_production;
	private double price;
	
	private Product consumption;
	private double stock_consumption;
	private double stock_max_consumption;
	
	private double money;
	private double satisfaction;
	
	private int iteration = 0;
	private double average_price;
	private double average_satifaction;
	private double average_money;
	private double life_time;
	
	private int lineage = 0;
	
	/**
	 * Temps passé sans pouvoir consommé de produit
	 */
	private double famine;
	
	//Etat de l'agent
	private boolean is_at_work = true;
	private int lifeState = 1; // 0 : survie, 1 : normal, 2 : reproduction
	
	@Override
	protected void setup() {
		super.setup();
		
		//Ajoute l'agent commercial au systeme d'analyse de la simulation
		//Analyse.getInstance().agent_setup(this); //==>a été Deplacer dans le behaviours
		
		//Récupération de la classe de gestion des log de jade
		logger = Logger.getMyLogger(this.getClass().getName());

		//Initialise les variables
		init();
		register();
		
		//Message du creation de l'agent
		logger.log(Logger.INFO, "Create Agent : "+this); 
		
		//Ajout des classe Behviours
		addBehaviour(new AgentCommercialBehviours(this, Config.TICKER_DELAY));
		addBehaviour(new AgentCommercialBehvioursListener());	
		addBehaviour(new AgentCommercialBehvioursTransaction(this, 1000));

		sendInfoToAnalyser("SETUP");	
	}

	@Override
	protected void takeDown() {
		super.takeDown();
		deregister();
		sendInfoToAnalyser("END");
		logger.log(Logger.INFO, "Destroy agent :"+this); 
	}
	
	/**
	 * Initialise l'agent avec les valeurs par defaut
	 */
	public void init(){
		Object[] args = getArguments();
		if(args.length >= 2){
			System.out.println("Args[0]:"+(String)args[0]);
			System.out.println("Args[1]:"+(String)args[1]);
		}
		if(args != null && args.length >= 2){
		    String arg_production = (String)args[0];
		    String arg_consommation = (String)args[1];
		    production = Product.valueOf(arg_production);
		    consumption = Product.valueOf(arg_consommation);
		}else{
			production = Product.getRandom();
			do{
				consumption = Product.getRandom();
			}while(consumption.equals(production));
		}
		
		stock_production = 0;
		stock_max_production = Config.STOCK_MAX_PRODUCTION;
		
		stock_consumption =  Config.INIT_CONSUMPTION;
		stock_max_consumption = Config.STOCK_MAX_CONSUMPTION;
		
		satisfaction = 100;
		//satisfaction = 10;
		money = Config.INIT_MONEY;
		price = Config.INIT_PRICE;
		
		average_money = money;
		average_price = price;
		average_satifaction = satisfaction;
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
	public void produce(double delta, double quantity){
		double total = quantity * delta;
		//double total = quantity * delta * 10;
		addStock_Product(total);
		if(stock_production == stock_max_production){
			//Augmentation de la satifaction
		}
		
		logger.log(Logger.FINE, "Agent : "+this.getName()+", produce :"+stock_production+"(+"+total+") (+"+quantity+" /sec)");
	}
	public void produce(double delta){
		produce(delta, Config.CONST_PROD);
	}
	
	
	/**
	 * l'agent consomme des ressources
	 * @param delta Le temps depuis la dernière execution de cette methode
	 */
	public void consomme(double delta, double quantity){
		double total = quantity * delta;
		removeStock_Consomme(total);
		
		logger.log(Logger.FINE, "Agent : "+this.getName()+", consomme :"+stock_consumption+"(-"+total+") (-"+quantity+" /sec)");
	}
	public void consomme(double delta){
		consomme(delta, Config.CONST_CONSUM);
	}
	/**
	 * Mise à jour des prix
	 */
	public void update_price(){
		if(satisfaction >= Config.PRICE_MAX_SATISFACTION && stock_consumption >= Config.UP_PRICE_CONSUM){
			price += 0.05;
		}
		if(price > 1 && money <= Config.PRICE_MIN_MONEY){
			price = Math.max(price - 0.15, 0.5);
		}else if(price < 1.0 && stock_consumption >= 2.0){
			price += 0.1;
		}
	}
	
	public void check_lifeState(){
		if(satisfaction < 80.0){
			lifeState = 0;
		}else if(satisfaction == 100 && money >= Config.INIT_MONEY*1.5){
			lifeState = 2;
		}else{
			lifeState = 1;
		}
	}
	
	
	/**
	 * Vérifie la satifaction et effectue les opérations nécéssaires
	 */
	public void check_satisfaction(double delta){
		if(satisfaction <= 0.0){
			logger.log(Logger.INFO, "Agent : "+this.getName()+", is starving to death !");
			kill();
		}
		
		if(lifeState == 2 && stock_consumption >= Config.INIT_CONSUMPTION*2){
			duplication();
		}
		
		if(stock_production == stock_max_production && is_at_work == true){
			satisfaction = Math.min(satisfaction+50, 100);
			is_at_work = false;
		}else{
			is_at_work = true;
		}
		
		if(stock_consumption <= 0){
			famine += delta;// * 1;
			reduceSatifaction(delta);
			logger.log(Logger.FINE, "Agent : "+this.getName()+", Famine increased to "+famine+" !");
		}else{			
			satisfaction = Math.min(satisfaction + 10, 100);	
			famine = 0;
		}
		
		
		
		
		
		
	}
	
	public void compute_stats(double delta) {
		iteration++;
		average_price = compute_average(average_price, price);
		average_satifaction = compute_average(average_satifaction, satisfaction);
		average_money = compute_average(average_money, money);
		life_time += delta;
	}
	

	/**
	 * Envois les données de l'agent commercial a l'agent d'analyse
	 * @param action
	 */
	public void sendInfoToAnalyser(String action){
		//Analyse.getInstance().agent_update(myAgentCommercial); 
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("SYSLOG");
		template.addServices(sd);
		
		DFAgentDescription[] result;
		try {
			result = DFService.search(this, template);
			AID[] agents = new AID[result.length];
			for (int i = 0; i < result.length; ++i) {
				agents[i] = result[i].getName();
				//Send
				ACLMessage msg;
				if(action.equals("SETUP")){
					msg = new ACLMessage(ACLMessage.INFORM);
				}else if(action.equals("UPDATE")){
					msg = new ACLMessage(ACLMessage.PROPAGATE);
				}else{
					msg = new ACLMessage(ACLMessage.FAILURE);
				}
				try {
					msg.setContentObject(this);
					msg.addReceiver(result[i].getName());
					send(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
	
	//---------------------Private Methode------------------------------------------------------
	
	private double compute_average(double a, double b){
		return a + (b-a)/iteration;
	}
	
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
		AgentContainer c = getContainerController();
		
		String[] args = {production.toString(), consumption.toString()};
		lineage += 1;
		try {
			AgentController Agent = c.createNewAgent("Agent"+production.toString()+lineage+"_filsde_"+getLocalName(), "sma.agent.AgentCommercial", args);
			Agent.start();
			money -= Config.INIT_MONEY;
			stock_consumption -= Config.INIT_CONSUMPTION;
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Ajoute des produits au stock de produit creé
	 * @param quantity Quantité de produit ajouté
	 */
	private void addStock_Product(double quantity){
		stock_production += quantity;
		if(stock_production > stock_max_production){
			stock_production = stock_max_production;
		}
	}
	
	/**
	 * Retire des produits au stock de produit creé
	 * @param quantity Quantité de produit retiré
	 */
	private void removeStock_Product(double quantity){
		stock_production -= quantity;
		if(stock_production < 0){
			stock_production = 0;
		}
	}
	
	/**
	 * Ajoute des produits au stock de produit consommé
	 * @param quantity Quantité de produit ajouté
	 */
	private void addStock_Consomme(double quantity){
		stock_consumption += quantity;
		if(stock_consumption > stock_max_consumption){
			stock_consumption = stock_max_consumption;
		}
	}
	
	/**
	 * Retire des produits au stock de produit consommé
	 * @param quantity Quantité de produit retiré
	 */
	private void removeStock_Consomme(double quantity){
		stock_consumption -= quantity;
		if(stock_consumption < 0){
			stock_consumption = 0;
		}
	}
	
	/**
	 * Reduit exponentiellement la satifaction
	 * 100-exp( (x/5.35) - 1)
	 * @param delta
	 */
	private void reduceSatifaction(double delta){ 
		//double reduction = (double) (delta * Math.pow(Config.CONST_REDUCE_SATIFACTION, famine)); //TODO
		
		//double reduction = satisfaction - Math.exp( famine /5.35 - 1.0);
		//double reduction = 100.0 - satisfaction;
		//satisfaction = reduction;
		//satisfaction -= Math.exp( famine /5.35 - 1.0);
		satisfaction -= Math.exp( famine /5.6 - 1.0);
	}
	
	//-----------------------Transactions Methodes--------------------------------
	
	public synchronized void sell(int quantity, double price) {
		logger.log(Level.INFO, "Sell "+quantity+" for "+price+" $");
		stock_production -= quantity;
		money += price * quantity;
	}

	public synchronized void buy(int quantity, double price) {
		logger.log(Level.INFO, "Buy "+quantity+" for "+price+" $");
		stock_consumption += quantity;
		money -= price * quantity;
	}
	
	//-----------------------GETTER------------------------------------------------
	
	public Product getConsumption() {
		return consumption;
	}
	
	public double getStock_consumption() {
		return stock_consumption;
	}
	
	public double getStock_max_consumption() {
		return stock_max_consumption;
	}
	
	public Product getProduction() {
		return production;
	}
	
	public double getStock_production() {
		return stock_production;
	}
	
	public double getStock_max_production() {
		return stock_max_production;
	}
	
	public double getPrice() {
		return price;
	}
	
	public double getSatisfaction() {
		return satisfaction;
	}
	
	public double getFamine() {
		return famine;
	}
	
	public double getMoney() {
		return money;
	}
	
	public double getAverage_money() {
		return average_money;
	}
	
	public double getAverage_price() {
		return average_price;
	}
	
	public double getAverage_satifaction() {
		return average_satifaction;
	}
	
	public double getLife_time() {
		return life_time;
	}
	
	public double getLifeState(){
		return lifeState;
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
