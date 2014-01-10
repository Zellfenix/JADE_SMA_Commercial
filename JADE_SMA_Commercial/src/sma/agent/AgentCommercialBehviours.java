/**
 * 
 */
package sma.agent;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.util.Logger;

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
	private AgentCommercialBehvioursTransaction buyAgent;
	
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
		
		buyAgent = new AgentCommercialBehvioursTransaction(10); //TODO
		myAgent.addBehaviour(buyAgent);
		
		last_update = new Date();
	}
	
	@Override
	protected void onTick() {
		float delta = (float) (((new Date()).getTime() - last_update.getTime()) / 1000.0);
		
		//Message test de log
		logger.log(Logger.FINE, "Entrée dans onTick. delta="+delta); 
		
		myAgentCommercial.produce(delta);
		//myAgentCommercial.consomme(delta);
		//myAgentCommercial.check_satisfaction(delta);
		
		if(myAgentCommercial.getStock_consumption() < myAgentCommercial.getStock_max_consumption()* 2/3 && buyAgent.done()){
			buyAgent.reset();
		}
		
		//TODO Tache a effectuer
		/* Produire
		 * Acheter
		 * Vendre
		 * Verification de la Satifaction
		 * Verification si peux se dupliquer
		 * ..ETC?
		 */
		
		last_update = new Date();
		
		//Met a jour les informations de la simulation
		Analyse.getInstance().agent_update(myAgentCommercial);
		
		
		
		
	}
	
	@Override
	public int onEnd() {
		//Message test de log
		logger.log(Logger.INFO, "Entrée dans onEnd."); 	
		return super.onEnd();
	}

}
