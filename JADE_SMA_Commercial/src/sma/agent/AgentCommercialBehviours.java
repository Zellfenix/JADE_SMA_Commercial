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
	
	private Date last_update; 
	
	
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
		double delta = (((new Date()).getTime() - last_update.getTime()) / 1000.0);
		
		//Message test de log
		logger.log(Logger.INFO, myAgent.getName()+" : Entrée dans onTick. delta="+delta); 
		
		myAgentCommercial.produce(delta);
		myAgentCommercial.consomme(delta);
		myAgentCommercial.check_satisfaction(delta);
		myAgentCommercial.update_price();
		myAgentCommercial.compute_stats(delta);
		
		last_update = new Date();
		
		//Met a jour les informations de la simulation
		myAgentCommercial.sendInfoToAnalyser("UPDATE");
	}
	
	@Override
	public int onEnd() {
		//Message test de log
		logger.log(Logger.INFO, "Entrée dans onEnd."); 	
		return super.onEnd();
	}
	
}
