/**
 * 
 */
package sma.agent;

import java.util.Date;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.util.Logger;

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
		
		last_update = new Date();
	}
	
	@Override
	protected void onTick() {
		float delta = (float) (((new Date()).getTime() - last_update.getTime()) / 1000.0);
		
		//Message test de log
		logger.log(Logger.INFO, "Entrée dans onTick. delta="+delta); 
		
		myAgentCommercial.produce(delta);
		myAgentCommercial.consomme(delta);
		
		
		myAgentCommercial.check_satisfaction(delta);
		
		//TODO Tache a effectuer
		/* Produire
		 * Acheter
		 * Vendre
		 * Verification de la Satifaction
		 * Verification si peux se dupliquer
		 * ..ETC?
		 */
		
		last_update = new Date();
	}
	
	@Override
	public int onEnd() {
		//Message test de log
		logger.log(Logger.INFO, "Entrée dans onEnd."); 	
		return super.onEnd();
	}

}
