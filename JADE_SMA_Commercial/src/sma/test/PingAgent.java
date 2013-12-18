package sma.test;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.AMSService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.lang.acl.ACLMessage;

public class PingAgent extends Agent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AMSAgentDescription[] agents;

	protected void setup() {
		//Printout a welcome message
		System.out.println("PingAgent "+getAID().getName()+"is ready.");
		try {
			agents = AMSService.search(this, new AMSAgentDescription());
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		addBehaviour(new PongBehaviour());
	}
	
	protected void takeDown() {
		System.out.println("PingAgent "+getAID().getName()+" terminating.");
	} 
	
	private class PongBehaviour extends Behaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage msg = receive();
			if(msg != null) {
				System.out.println("PING Receive:"+msg);
				ACLMessage reply = msg.createReply();
				reply.setContent("PING");
				
				send(reply);
			} 
		}

		@Override
		public boolean done() {
			return false;
		}
		
	}

	
}
