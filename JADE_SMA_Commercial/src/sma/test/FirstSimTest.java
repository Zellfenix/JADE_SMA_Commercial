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
public class FirstSimTest extends Agent {

	private static final long serialVersionUID = 1L;

	protected void setup() {
		AgentContainer c = getContainerController();
		try {
			AgentController Analyser = c.createNewAgent("Analyser", "sma.tools.analyse.AgentAnalyser", null);

			String[] argsA = { "A", "B" };
			AgentController AgentA = c.createNewAgent("AgentA", "sma.agent.AgentCommercial", argsA);

			String[] argsB = { "B", "A" };
			AgentController AgentB = c.createNewAgent("AgentB", "sma.agent.AgentCommercial", argsB);

			Analyser.start();
			AgentA.start();
			AgentB.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

	}

}
