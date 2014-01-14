/**
 * 
 */
package sma.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

import java.io.IOException;
import java.util.Date;

import sma.tools.analyse.Analyse;

/**
 * @author Jérémy
 *
 */
public class AgentCommercialBehviours extends TickerBehaviour {

	private static final long serialVersionUID = 1L;
	private java.util.logging.Logger logger;
	private AgentCommercial myAgentCommercial;
	
	private static Date last_update; 
	
	
	public AgentCommercialBehviours(Agent a, long period) {
		super(a, period);
		//Permet de fixer la durée d'un tick a la valeur "period"
		setFixedPeriod(true);
		
		myAgentCommercial = (AgentCommercial) myAgent;
		
		logger = Logger.getMyLogger(this.getClass().getName());
	}

	@Override
	public void onStart() {
		super.onStart();
		//Message test de log
		logger.log(Logger.INFO, "Entrée dans onStart.");
		
		sendInfoToAnalyser("SETUP");
		
		last_update = new Date();
	}
	
	
	//TODO Tache a effectuer
	/* Produire
	 * Acheter
	 * Vendre
	 * Verification de la Satifaction
	 * Verification si peux se dupliquer
	 * ..ETC?
	 */
	@Override
	protected void onTick() {
		float delta = (float) (((new Date()).getTime() - last_update.getTime()) / 1000.0);
		
		//Message test de log
		logger.log(Logger.FINE, "Entrée dans onTick. delta="+delta); 
		
		myAgentCommercial.produce(delta);
		myAgentCommercial.consomme(delta);
		myAgentCommercial.check_satisfaction(delta);
		myAgentCommercial.compute_stats(delta);
		
		last_update = new Date();
		
		//Met a jour les informations de la simulation
		sendInfoToAnalyser("UPDATE");
	}
	
	@Override
	public int onEnd() {
		//Message test de log
		logger.log(Logger.INFO, "Entrée dans onEnd."); 	
		sendInfoToAnalyser("END");
		return super.onEnd();
	}
	
	/**
	 * Envois les données de l'agent commercial a l'agent d'analyse
	 * @param action
	 */
	private void sendInfoToAnalyser(String action){
		//Analyse.getInstance().agent_update(myAgentCommercial); 
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("SYSLOG");
		template.addServices(sd);
		
		DFAgentDescription[] result;
		try {
			result = DFService.search(myAgent, template);
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
					msg.setContentObject(myAgentCommercial);
					msg.addReceiver(result[i].getName());
					myAgent.send(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
}
