/**
 * 
 */
package sma.agent;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.util.Logger;

/**
 * @author J�r�my
 *
 */
public class AgentCommercialBehviours extends TickerBehaviour {

	private static final long serialVersionUID = 1L;
	private java.util.logging.Logger logger;
	private AgentCommercial myAgentCommercial;

	public AgentCommercialBehviours(Agent a, long period) {
		super(a, period);
		
		myAgentCommercial = (AgentCommercial) myAgent;
		
		logger = Logger.getMyLogger(this.getClass().getName());
		
		//Permet de fixer la dur�e d'un tick a la valeur "period"
		setFixedPeriod(true);
		
	}

	@Override
	public void onStart() {
		super.onStart();
		//Message test de log
		logger.log(Logger.INFO, "Entr�e dans onStart."); 
	}
	
	@Override
	protected void onTick() {
		//Message test de log
		logger.log(Logger.INFO, "Entr�e dans onTick."); 
		
		//TODO Tache a effectuer
		/* Produire
		 * Acheter
		 * Vendre
		 * Verification de la Satifaction
		 * Verification si peux se dupliquer
		 * ..ETC?
		 */
	}
	
	@Override
	public int onEnd() {
		//Message test de log
		logger.log(Logger.INFO, "Entr�e dans onEnd."); 	
		return super.onEnd();
	}

}
