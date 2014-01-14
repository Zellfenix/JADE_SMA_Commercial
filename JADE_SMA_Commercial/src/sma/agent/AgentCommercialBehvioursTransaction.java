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
	
	public AgentCommercialBehvioursTransaction(Agent a, long period) {
		super(a, period);
		this.logger = Logger.getMyLogger(this.getClass().getName());
		
		price_table = new HashMap<AID, Double[]>();
		
		//this.init_quantity = 10;
		this.init_quantity = 1;
		
		logger.log(Logger.CONFIG, "Create AgentCommercialBehvioursTransaction");
	}

	@Override
	public void onStart() {
		super.onStart();
		//Message test de log
		logger.log(Logger.INFO, "Entrée dans onStart.");
		this.myAgentCommercial = (AgentCommercial) this.myAgent;
		
		//Behaviour de recherche d'un vendeur
		//myAgent.addBehaviour(new PriceResearch(myAgent, 5000));
		//myAgent.addBehaviour(new PriceResearch(myAgent, 1000));
	}

	@Override
	protected void onTick() {
		//Condition d'achat
		if(myAgentCommercial.getStock_consumption() == myAgentCommercial.getStock_max_consumption()){
			return;
		}
		
		//Achete
		if(price_table.isEmpty() == false){
			pricesResearch();
			buyProduct();
		}
	}
	
	@Override
	public int onEnd() {
		//Message test de log
		logger.log(Logger.INFO, "End of transaction!"); 	
		return super.onEnd();
	}

	
	public void setQuantity(int quantity) {	
		this.init_quantity = quantity;
	}
	
	//-----------Private Methode----------------
	private int moreSuitableQuantity(double price,int quantity){
		return (int) Math.min(myAgentCommercial.getStock_max_consumption()-myAgentCommercial.getStock_consumption(), Math.min(myAgentCommercial.getMoney()/price,quantity));
	}
	/**
	 * Recherche le vendeur le moins chere
	 */
	private void pricesResearch(){
		//Recuperation des resultats depuis le DF
		DFAgentDescription[] sellers = myAgentCommercial.search();
		int nb_reponce = 0;
		int nb_try = 0;
		price_table.clear();
		
		//Envois des CFP
		for(DFAgentDescription seller : sellers){
			sendCFP(seller.getName());
		}
		
		//Attente des reponses
		while(price_table.size() < sellers.length){
			//MessageTemplate mt = MessageTemplate.or( MessageTemplate.MatchPerformative( ACLMessage.PROPOSE ), MessageTemplate.MatchPerformative( ACLMessage.CONFIRM ));
			MessageTemplate mt = MessageTemplate.MatchPerformative( ACLMessage.PROPOSE );
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null) {
				logger.log(Logger.INFO, "ControlerAgent Receive("+myAgent.getName()+"):"+msg);
				
				switch(msg.getPerformative()){
					case ACLMessage.PROPOSE:
						String[] propose = msg.getContent().split(" ");
						try {
							int quantity = Integer.parseInt(propose[1]);
							double price = Double.parseDouble(propose[2]);
							Double[] tmp = {price, (double) quantity};
							price_table.put(msg.getSender(), tmp);
							//nb_reponce++;
							System.out.println("Taille price_table : "+price_table.size());
						} catch (NumberFormatException e) {
							e.printStackTrace();
							return;
						}
						break;
					default:
						break;
				}
			}
		}
	}
	
	/**
	 * Achete des produits a consommer
	 */
	private void buyProduct(){
		if(price_table.size() > 0){
			//Init variables
			//int quantity = init_quantity;
			double min_price = Config.INFINI;
			AID min_seller = null;
			int min_quantity = 0;
			
			//Recherche du vendeur le moins chere dans le tableau de prix
			for(AID seller : price_table.keySet()){
				Double[] price_tmp = price_table.get(seller);
				
				if((price_tmp[0] < min_price && price_tmp[1].intValue() > 0) || min_seller == null){ //TODO
					min_price = price_tmp[0];
					min_seller = seller;
					min_quantity = moreSuitableQuantity(price_tmp[0],price_tmp[1].intValue());
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
					}else{
						sendAccept_Proposal(min_seller, min_quantity, min_price);
					}
				}
			}
			
			//Attente de la confirmation
			int nb_reponce = 0;
			int nb_try = 0;
			while(nb_reponce < 1 && nb_try < 100){
				nb_try++;
				MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchPerformative( ACLMessage.CONFIRM ), MessageTemplate.MatchPerformative( ACLMessage.CANCEL ));
				ACLMessage msg = myAgent.receive(mt);
				if(msg != null) {
					logger.log(Logger.INFO, getClass().getName()+" Receive("+myAgent.getName()+"):"+msg);
					nb_reponce++;
					switch(msg.getPerformative()){	
						case ACLMessage.CONFIRM:
							if(min_seller != null && min_price != Config.INFINI)
								executeTransaction(min_quantity, min_price);
							break;
						case ACLMessage.CANCEL:
							break;
						default:
							break;
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
	
}
