/**
 * 
 */
package sma.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;

import sma.tools.Config;

/**
 * @author Jérémy
 * Gere l'achat de ressource
 */
public class AgentCommercialBehvioursTransaction extends TickerBehaviour {

	private static final long serialVersionUID = 1L;

	private java.util.logging.Logger logger;
	private AgentCommercial myAgentCommercial;

	//Regroupe les prix de chaque agent
	private HashMap<AID, Double[]> price_table;
	private int init_quantity;

	//private int state;

	private AID min_seller;
	private int min_quantity;
	private double min_price;
	private double quantity_for_duplication;

	public AgentCommercialBehvioursTransaction(Agent a, long period) {
		super(a, period);
		this.logger = Logger.getMyLogger(this.getClass().getName());

		price_table = new HashMap<AID, Double[]>();

		//this.init_quantity = 10;
		this.init_quantity = 1;

		logger.log(Logger.CONFIG, "Create AgentCommercialBehvioursTransaction", this);

	}

	@Override
	public void onStart() {
		super.onStart();
		//Message test de log
		logger.log(Logger.INFO, "Entrée dans onStart.", this);
		this.myAgentCommercial = (AgentCommercial) this.myAgent;

		//Behaviour de recherche d'un vendeur
		//myAgent.addBehaviour(new PriceResearch(myAgent, 5000));
		//myAgent.addBehaviour(new PriceResearch(myAgent, 1000));
		/*
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 */
		myAgentCommercial.setRunningState(0);
	}

	@Override
	protected void onTick() {
		//Condition d'achat
		if(myAgentCommercial.getStock_consumption() == myAgentCommercial.getStock_max_consumption()){
			return;
		}

		if(myAgentCommercial.getRunningState() == 4){
			myAgentCommercial.setRunningState(0);
		}

		//Achete
		pricesResearch();
		buyProduct();

	}

	@Override
	public int onEnd() {
		//Message test de log
		logger.log(Logger.INFO, "End of transaction!", this); 	
		return super.onEnd();
	}


	public void setQuantity(int quantity) {	
		this.init_quantity = quantity;
	}

	//-----------Private Methode----------------
	private int moreSuitableQuantity(double price,int quantity){
		int test = (int) Math.min(myAgentCommercial.getStock_max_consumption()-myAgentCommercial.getStock_consumption(), Math.min(myAgentCommercial.getMoney()/price,quantity));
		//System.out.println("max : "+quantity +" | price :"+price + " | money : "+myAgentCommercial.getMoney()+"| quantity : "+test);
		return test;

	}

	/**
	 * Recherche le vendeur le moins chere
	 */
	private void pricesResearch(){
		//Recuperation des resultats depuis le DF
		DFAgentDescription[] sellers = myAgentCommercial.search();
		//int nb_reponce = 0;
		//int nb_try = 0;

		//Envois des CFP
		if(myAgentCommercial.getRunningState() == 0){
			price_table.clear();
			for(DFAgentDescription seller : sellers){
				sendCFP(seller.getName());
			}
			myAgentCommercial.setRunningState(1);
			myAgentCommercial.addTransactionInit();
		}

		//Attente des reponses
		if(price_table.size() < sellers.length && myAgentCommercial.getRunningState() == 1){
			//nb_try++;
			//MessageTemplate mt = MessageTemplate.or( MessageTemplate.MatchPerformative( ACLMessage.PROPOSE ), MessageTemplate.MatchPerformative( ACLMessage.CONFIRM ));
			MessageTemplate mt = MessageTemplate.MatchPerformative( ACLMessage.PROPOSE );
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null) {
				//logger.log(Logger.INFO, /*getClass().getName()+*/myAgent.getLocalName() +" Receive("+msg.getContent()+"): from :"+msg.getSender().getLocalName(), this);
				//logger.log(Logger.INFO, "ControlerAgent Receive("+myAgent.getLocalName()+"):"+msg.getContent());
				logger.log(Logger.INFO, "Receive("+msg.getContent()+"): from :"+msg.getSender().getLocalName(), this);
				
				switch(msg.getPerformative()){
				case ACLMessage.PROPOSE:
					String[] propose = msg.getContent().split(" ");
					try {
						int quantity = Integer.parseInt(propose[1]);
						double price = Double.parseDouble(propose[2]);
						Double[] tmp = {price, (double) quantity};
						price_table.put(msg.getSender(), tmp);
						//nb_reponce++;
						//System.out.println("Taille price_table : "+price_table.size());
						logger.log(Level.FINE, "Taille price_table : "+price_table.size(), this);
					} catch (NumberFormatException e) {
						e.printStackTrace();
						return;
					}
					break;
				default:
					break;
				}
			}
		}else if(myAgentCommercial.getRunningState() == 1){
			myAgentCommercial.setRunningState(2);
		}
	}

	/**
	 * Achete des produits a consommer
	 */
	private void buyProduct(){
		boolean one_is_accepted = false;
		if(price_table.size() > 0 && myAgentCommercial.getRunningState() >= 2){
			if(myAgentCommercial.getRunningState() == 2){

				//Init variables
				//int quantity = init_quantity;
				min_price = Config.INFINI;
				min_seller = null;
				min_quantity = 0;
				

				if(myAgentCommercial.getLifeState() == 0 ){
					for(AID seller : price_table.keySet()){
						Double[] price_tmp = price_table.get(seller);
						if(moreSuitableQuantity(price_tmp[0],price_tmp[1].intValue()) > min_quantity || min_seller == null){ //TODO
							min_price = price_tmp[0];
							min_seller = seller;
							min_quantity = moreSuitableQuantity(price_tmp[0],price_tmp[1].intValue());
							if(min_quantity > 1){
								Random r = new Random();
								if(r.nextBoolean() == true){
									break;
								}
							}
						}
					}
				}else if(myAgentCommercial.getLifeState() == 2){
					double quantity;
					quantity = Config.INFINI;
					for(AID seller : price_table.keySet()){
						Double[] price_tmp = price_table.get(seller);
						quantity_for_duplication = (int) Config.INIT_CONSUMPTION*2 - myAgentCommercial.getStock_consumption();
						if(Math.abs(quantity_for_duplication - moreSuitableQuantity(price_tmp[0],price_tmp[1].intValue())) < quantity || min_seller == null){ //TODO
							min_price = price_tmp[0];
							min_seller = seller;
							min_quantity = moreSuitableQuantity(price_tmp[0],price_tmp[1].intValue());
							quantity = Math.abs(quantity_for_duplication - moreSuitableQuantity(price_tmp[0],price_tmp[1].intValue()));
						}
					}
				}else if(myAgentCommercial.getLifeState() == 1){
					//Recherche du vendeur le moins chere dans le tableau de prix
					for(AID seller : price_table.keySet()){
						Double[] price_tmp = price_table.get(seller);

						if((price_tmp[0] < min_price && price_tmp[1].intValue() > 0) || min_seller == null){ //TODO
							min_price = price_tmp[0];
							min_seller = seller;
							min_quantity = moreSuitableQuantity(price_tmp[0],price_tmp[1].intValue());
						}
					}
				}else{
					for(AID seller : price_table.keySet()){
						min_price = -1;
						Double[] price_tmp = price_table.get(seller);
						if((price_tmp[0] > min_price && price_tmp[1].intValue() > 0) || min_seller == null){ //TODO
							min_price = price_tmp[0];
							min_seller = seller;
							min_quantity = moreSuitableQuantity(price_tmp[0],price_tmp[1].intValue());
						}
					}
				}
				/*	
				//Vérification de la quantité disponible
				if(min_quantity < quantity){
					quantity = min_quantity;
					return; //TODO
				}
				//Vérification de la quantité demandé
				if(min_quantity == 0){
					return;
				}
				 */	

				//Envois de l'acceptation

				for(AID seller : price_table.keySet()){
					if(seller.equals(min_seller) == false){
						sendReject_Proposal(seller);
					}else{
						if(min_quantity == 0){
							sendReject_Proposal(seller);
							logger.log(Level.INFO, "QUANTITY = 0 | money : "+myAgentCommercial.getMoney(), this);
							//System.out.println("QUANTITY = 0 | money : "+myAgentCommercial.getMoney());
							myAgentCommercial.setRunningState(4);
						}else{
							sendAccept_Proposal(min_seller, min_quantity, min_price);
							one_is_accepted = true;
							myAgentCommercial.setRunningState(3);
						}
					}
				}
			}

			if(one_is_accepted == true || myAgentCommercial.getRunningState() == 3){
				//Attente de la confirmation
				int nb_reponce = 0;
				int nb_try = 0;
				
				if(nb_reponce < 1 ){					
					nb_try++;
					MessageTemplate mt = MessageTemplate.and(
							MessageTemplate.or(MessageTemplate.MatchPerformative( ACLMessage.CONFIRM ), MessageTemplate.MatchPerformative( ACLMessage.CANCEL ))
					,MessageTemplate.MatchSender(min_seller));
					ACLMessage msg = myAgent.receive(mt);
					if(msg != null) {
						
						//logger.log(Logger.INFO, /*getClass().getName()+*/myAgent.getLocalName() +"Receive("+msg.getContent()+"): from :"+msg.getSender().getLocalName(), this);
						logger.log(Logger.INFO, "Receive("+msg.getContent()+"): from :"+msg.getSender().getLocalName(), this);
						
						nb_reponce++;
						switch(msg.getPerformative()){	
						case ACLMessage.CONFIRM:
							//if(min_seller != null && min_price != Config.INFINI)
							executeTransaction(min_quantity, min_price);
							myAgentCommercial.setRunningState(4);
							myAgentCommercial.addTransactionConfirm();
							break;
						case ACLMessage.CANCEL:
							myAgentCommercial.setRunningState(4);
							myAgentCommercial.addTransactionCancel();
							break;
						default:
							logger.log(Logger.INFO, "DEFAULT : to : "+myAgent.getLocalName() +"Receive("+msg.getContent()+"): from :"+msg.getSender().getLocalName(), this);
							myAgentCommercial.setMoney();
							break;
						}
					}else{
						/*try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {e.printStackTrace();}*/
					}
				}
			}
		}
	}

	private void sendCFP(AID aid){
		ACLMessage msg = new ACLMessage(ACLMessage.CFP);
		msg.addReceiver(aid);
		myAgent.send(msg);
	}

	private void sendAccept_Proposal(AID aid, int quantity, double price){
		ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
		msg.setContent("ACCEPT_PROPOSAL "+quantity);
		msg.addReceiver(aid);
		myAgent.send(msg);

	}

	private void sendReject_Proposal(AID aid){
		ACLMessage msg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
		msg.setContent("REJECT_PROPOSAL");
		msg.addReceiver(aid);
		myAgent.send(msg);

	}

	private void executeTransaction(int quantity, double price) {
		myAgentCommercial.buy(quantity, price);
	}


	//--------------------Private Behaviours----------------------

	private class PriceResearch extends TickerBehaviour{
		private static final long serialVersionUID = 1L;
		public PriceResearch(Agent a, long period) {
			super(a, period);
		}
		@Override
		protected void onTick() {
			pricesResearch();
		}
	}

	@Override
	public String toString() {
		return myAgent.getLocalName();
	}
}
