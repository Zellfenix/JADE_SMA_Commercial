/**
 * 
 */
package sma.test;

import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

/**
 * @author Jeremy
 *
 */
public class FirstSimTest extends Agent{

	private static final long serialVersionUID = 1L;

	protected void setup() {
		AgentContainer c = getContainerController();
		try {
			AgentController Analyser = c.createNewAgent("Analyser", "sma.tools.analyse.AgentAnalyser", null);
			
			String[] argsA = {"A", "B"};
			String[] argsB = {"B", "C"};
			String[] argsC = {"C", "A"};
			
			
			AgentController AgentA1 = c.createNewAgent("AgentA1", "sma.agent.AgentCommercial", argsA);
			AgentController AgentB1 = c.createNewAgent("AgentB1", "sma.agent.AgentCommercial", argsB);
			AgentController AgentA2 = c.createNewAgent("AgentA2", "sma.agent.AgentCommercial", argsA);
			AgentController AgentB2 = c.createNewAgent("AgentB2", "sma.agent.AgentCommercial", argsB);
			AgentController AgentC1 = c.createNewAgent("AgentC1", "sma.agent.AgentCommercial", argsC);
			AgentController AgentC2 = c.createNewAgent("AgentC2", "sma.agent.AgentCommercial", argsC);
			
			Analyser.start();
			AgentA1.start();
			AgentB1.start();
			//AgentA2.start();
			//AgentB2.start();	
			AgentC1.start();
			//AgentC2.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
	}

	

}
