package sma.tools.analyse;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import sma.agent.AgentCommercial;

public class AgentAnalyser extends Agent{

	private static final long serialVersionUID = 1L;

	public AgentAnalyser() {
		
		addBehaviour(new listenerBehaviours());
		
	}
	
	
	private class listenerBehaviours extends Behaviour{

		private static final long serialVersionUID = 1L;
		
		private boolean stop = false;
		
		@Override
		public void action() {
			AgentCommercial agentCommercial;
			//MessageTemplate mt = MessageTemplate.MatchPerformative( ACLMessage. );
			ACLMessage msg = myAgent.receive();
			if(msg != null) {
				try {
					if(msg.getPerformative() == ACLMessage.INFORM){
						agentCommercial = (AgentCommercial) msg.getContentObject();
						Analyse.getInstance().agent_setup(agentCommercial);
					}else{
						agentCommercial = (AgentCommercial) msg.getContentObject();
						Analyse.getInstance().agent_update(agentCommercial);
					}
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public boolean done() {
			return stop;
		}
		@Override
		public void onStart() {
			super.onStart();
			register();
		}
		@Override
		public int onEnd() {
			deregister();
			return super.onEnd();
		}
		
		/**
		 * Enregistre l'agent dans le DF
		 */
		public void register(){
			// Register the ComputeAgent service in the yellow pages
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setType("SYSLOG");
			sd.setName(getName());
			dfd.addServices(sd);
			try {
				DFService.register(myAgent, dfd);
			}catch (FIPAException fe) {
				fe.printStackTrace();
			}
		}
		
		/**
		 * Retire l'agent du DF
		 */
		public void deregister(){
			// Deregister from the yellow pages
			try {
				DFService.deregister(myAgent);
			}catch(FIPAException fe) {
				fe.printStackTrace();
			}
		}
	}
}
