/**
 * 
 */
package sma.agent;

import java.util.HashMap;
import java.util.logging.Level;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

/**
 * @author Jérémy
 * Gere la vente de ressource
 */

public class AgentCommercialBehvioursListener extends Behaviour {

	private static final long serialVersionUID = 1L;
	
	private HashMap<String, AgentCommercialBehvioursListenerModule> listener;
	private java.util.logging.Logger logger;
	private boolean stop = false;
	
	public AgentCommercialBehvioursListener() {
		listener = new HashMap<String, AgentCommercialBehvioursListenerModule>();
		logger = Logger.getMyLogger(this.getClass().getName());
	}
	
	@Override
	public void action() {
		//Verrifie les messages et creer les behvioursTransaction si besoin
		MessageTemplate mt = MessageTemplate.MatchPerformative( ACLMessage.CFP );
		ACLMessage msg = myAgent.receive(mt);
		if(msg != null) {
			if(listener.containsKey(msg.getSender().toString()) == false){
				logger.log(Logger.INFO, "Create ListerModule for :"+msg.getSender());
				
				AgentCommercialBehvioursListenerModule listener_agent = new AgentCommercialBehvioursListenerModule(msg.getSender());
				myAgent.addBehaviour(listener_agent);
				listener_agent.setAgent(myAgent);
				listener_agent.sendPropose(msg);
				listener.put(msg.getSender().toString(), listener_agent);
			}else{
				AgentCommercialBehvioursListenerModule listener_agent = listener.get(msg.getSender().toString());
				listener_agent.sendPropose(msg);
			}
		}
	}
	
	@Override
	public boolean done() {
		return stop ;
	}
	
}


class AgentCommercialBehvioursListenerModule extends Behaviour {

	private static final long serialVersionUID = 1L;
	
	private java.util.logging.Logger logger;
	private AgentCommercial myAgentCommercial;
	private AID sender;
	//
	private float priceSend;
	private int quantitySend;
	private boolean stop = false;

	public AgentCommercialBehvioursListenerModule(AID sender) {
		super();
		this.sender = sender;
		this.logger = Logger.getMyLogger(this.getClass().getName());
		
		logger.log(Logger.CONFIG, "Create AgentCommercialBehvioursListener for : "+sender);
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
		if(myAgentCommercial != null){
			MessageTemplate mt = MessageTemplate.and( 
					MessageTemplate.or( MessageTemplate.MatchPerformative( ACLMessage.CFP ), MessageTemplate.MatchPerformative( ACLMessage.ACCEPT_PROPOSAL )), 
					MessageTemplate.MatchSender(sender) );
			//MessageTemplate mt = MessageTemplate.MatchSender(sender);
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null) {
				logger.log(Logger.INFO, "ControlerAgent Receive("+myAgent.getName()+"):"+msg);
				
				switch(msg.getPerformative()){
					case ACLMessage.CFP:
						sendPropose(msg);
						break;
					case ACLMessage.ACCEPT_PROPOSAL:
						sendConfirm(msg);
						break;
					default:
						break;
				}
			}
		}else{
			if(myAgent != null){
				myAgentCommercial = (AgentCommercial) myAgent;
			}
		}
	}
	
	@Override
	public int onEnd() {
		//Message test de log
		logger.log(Logger.INFO, "Entrée dans onEnd."); 	
		return super.onEnd();
	}

	@Override
	public boolean done() {
		return stop;
	}

	@Override
	public void setAgent(Agent a) {
		super.setAgent(a);
		this.myAgentCommercial = (AgentCommercial) this.myAgent;
	}
	
	//-----------Private Methode----------------

	public void sendPropose(ACLMessage msg){
		ACLMessage reply = msg.createReply();
		
		quantitySend = (int)myAgentCommercial.getStock_production();
		priceSend = myAgentCommercial.getPrice();
		
		reply.setPerformative(ACLMessage.PROPOSE);
		reply.setContent("PROPOSE "+quantitySend+" "+priceSend);
		myAgent.send(reply);
	}
	
	public synchronized void sendConfirm(ACLMessage msg) {
		//Check Price
		if(priceSend == myAgentCommercial.getPrice()){
			int quantityR;
			try {
				quantityR = Integer.parseInt(msg.getContent().split(" ")[1]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}
			//Check Quantity
			if(quantityR <= myAgentCommercial.getStock_production()){
				//Send Confirm
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.CONFIRM);
				reply.setContent("CONFIRM "+quantityR+" "+priceSend);
				myAgent.send(reply);
				logger.log(Level.INFO, myAgent.getName()+" :Transaction accepted! Send Confirm!");
				
				//Execute transaction
				myAgentCommercial.sell(quantityR, priceSend);
			}else{
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.CANCEL);
				myAgent.send(reply);
				logger.log(Level.INFO, myAgent.getName()+" :Transaction refused! Not enough produc!t");
			}
		}else{
			ACLMessage reply = msg.createReply();
			reply.setPerformative(ACLMessage.CANCEL);
			myAgent.send(reply);
			logger.log(Level.INFO, myAgent.getName()+" : Price change! ReSend propose!");
		}
	}
	
}
