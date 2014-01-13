/**
 * 
 */
package sma.agent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
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
public class AgentCommercialBehvioursTransaction extends Behaviour {

	private static final long serialVersionUID = 1L;
	private java.util.logging.Logger logger;
	private AgentCommercial myAgentCommercial;
	private int init_quantity;
	private int quantity;
	private double min_price = Config.INFINI;
	private AID min_seller;
	private boolean stop;
	//Regroupe les prix de chaque agent
	private HashMap<AID, Double[]> price_table;
	
	public AgentCommercialBehvioursTransaction() {
		super();
		this.logger = Logger.getMyLogger(this.getClass().getName());
		
		price_table = new HashMap<AID, Double[]>();
		
		this.init_quantity = 2;
		
		logger.log(Logger.CONFIG, "Create AgentCommercialBehvioursTransaction");
	}

	@Override
	public void onStart() {
		super.onStart();
		//Message test de log
		logger.log(Logger.INFO, "Entrée dans onStart.");
		this.myAgentCommercial = (AgentCommercial) this.myAgent;
	}

	@Override
	public void action() {
		if(myAgentCommercial.getStock_consumption() == myAgentCommercial.getStock_max_consumption()){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {e.printStackTrace();}
			return;
		}
		
		quantity = init_quantity;
		
		DFAgentDescription[] sellers = myAgentCommercial.search();
		int nb_reponce = 0;
		int nb_try = 0;
		//Send CFP
		for(DFAgentDescription seller : sellers){
			sendCFP(seller.getName());
		}
		
		while(nb_reponce < sellers.length && nb_try < 100){
			nb_try++;
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
							nb_reponce++;
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
		
		//Recherche du vendeur le moins chere
		if(sellers.length > 0){
			min_price = Config.INFINI;
			min_seller = null;
			int min_quantity = 0;
			for(AID seller : price_table.keySet()){
				Double[] price_tmp = price_table.get(seller);
				if((price_tmp[0] < min_price && price_tmp[1].intValue() > 0) || min_seller == null){
					min_price = price_tmp[0];
					min_seller = seller;
					min_quantity = price_tmp[1].intValue();
				}
			}
			
			if(min_quantity < quantity){
				if(min_quantity == 0){
					return;
				}
				quantity = min_quantity;
			}
			
			//Envois de l'acceptation
			sendAccept_Proposal(min_seller, quantity, min_price);
			
			//Attente de la confirmation
			nb_reponce = 0;
			nb_try = 0;
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
								executeTransaction(quantity, min_price);
							break;
						case ACLMessage.CANCEL:
							break;
						default:
							break;
					}
				}
			}
		}
		
		//System.out.println("\n"+price_table+"\n");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int onEnd() {
		//Message test de log
		logger.log(Logger.INFO, "End of transaction!"); 	
		return super.onEnd();
	}

	@Override
	public boolean done() {
		return stop;
	}
	
	public void setQuantity(int quantity) {	
		this.init_quantity = quantity;
	}
	
	//-----------Private Methode----------------

	private void sendCFP(AID aid){
		ACLMessage msg = new ACLMessage(ACLMessage.CFP);
		msg.addReceiver(aid);
		myAgent.send(msg);
	}
	
	private void sendAccept_Proposal(AID aid, int quantity, double price){
		//Check money
		if(myAgentCommercial.getMoney() >= quantity * price){
			ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			msg.setContent("ACCEPT_PROPOSAL "+quantity);
			msg.addReceiver(aid);
			myAgent.send(msg);
		}
	}
	
	private void executeTransaction(int quantity, double price) {
		myAgentCommercial.buy(quantity, price);
	}

}
